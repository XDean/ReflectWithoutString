package xdean.reflect.getter.impl;

import static xdean.reflect.getter.internal.util.ExceptionUtil.uncheck;
import static xdean.reflect.getter.internal.util.TaskUtil.firstNonNull;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import sun.misc.Unsafe;
import xdean.reflect.getter.FieldPropGetter;
import xdean.reflect.getter.internal.util.PrimitiveTypeUtil;
import xdean.reflect.getter.internal.util.ReflectUtil;
import xdean.reflect.getter.internal.util.UnsafeUtil;

/**
 * Based on {@link Unsafe}.
 *
 * @author XDean
 *
 * @param <T>
 */
public class UnsafeFieldGetter<T> implements FieldPropGetter<T> {

  private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

  private T mockT;
  private Map<Class<?>, List<Field>> primitives = new HashMap<>();
  private Map<Object, Field> objectMap = new IdentityHashMap<>();

  /**
   *
   * @param clz
   * @throws IllegalStateException If construct the mock object failed.
   * @throws IllegalArgumentException If the class is not suitable. See the class doc.
   */
  public UnsafeFieldGetter(Class<T> clz) throws IllegalStateException, IllegalArgumentException {
    try {
      if (clz.isInterface()) {
        throw new IllegalArgumentException("Interface has no field.");
      }
      mockT = Modifier.isAbstract(clz.getModifiers()) ? newAbstract(clz) : newObject(clz);
      Field[] fields = ReflectUtil.getAllFields(clz, false);
      for (Field field : fields) {
        Class<?> type = field.getType();
        if (PrimitiveTypeUtil.isPrimitive(type)) {
          handlePrimitive(field);
        } else if (type.isArray()) {
          handleArray(field);
        } else {
          handleObject(field);
        }
      }
    } catch (InstantiationException e) {
      throw new IllegalStateException(e);
    }
  }

  private void handleObject(Field field) throws InstantiationException {
    Class<?> type = field.getType();
    Object o;
    if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
      o = newAbstract(type);
    } else if (type == Class.class) {
      o = newClass(Object.class);
    } else {
      o = newObject(type);
    }
    long offset = UNSAFE.objectFieldOffset(field);
    UNSAFE.putObject(mockT, offset, o);
    objectMap.put(o, field);
  }

  @SuppressWarnings("unchecked")
  private <C> C newObject(Class<C> clz) throws InstantiationException {
    return (C) UNSAFE.allocateInstance(clz);
  }

  private <C> C newAbstract(Class<C> clz) throws InstantiationException {
    return newObject(newClass(clz));
  }

  @SuppressWarnings("unchecked")
  private <C> Class<? extends C> newClass(Class<C> clz) {
    Enhancer enhancer = new Enhancer();
    if (clz.isInterface()) {
      enhancer.setInterfaces(new Class[] { clz });
    } else {
      enhancer.setSuperclass(clz);
    }
    enhancer.setUseCache(true);
    enhancer.setCallbackType(NoOp.class);
    return enhancer.createClass();
  }

  private void handleArray(Field field) {
    Array.newInstance(field.getType(), 0);
    long offset = UNSAFE.objectFieldOffset(field);
    Object array = Array.newInstance(Object.class, 0);
    UNSAFE.putObject(mockT, offset, array);
    objectMap.put(array, field);
  }

  private void handlePrimitive(Field field) {
    field.setAccessible(true);
    primitives.computeIfAbsent(field.getType(), k -> new ArrayList<>()).add(field);
  }

  private Field getPrimitive(Function<T, ?> invoke) {
    Object value = invoke.apply(mockT);
    Class<?> type = PrimitiveTypeUtil.toPrimitive(value.getClass());
    Collection<Field> fields = primitives.get(type);
    if (fields.size() == 1) {
      return fields.iterator().next();
    }
    int count = 0;
    while (true) {
      Map<Object, Field> map = new HashMap<>();
      Iterator<? extends Object> allValue = getAllValue(type);
      Iterator<Field> fieldIterator = fields.iterator();
      Object defaultValue = allValue.next();
      int i = count;
      while (i-- > 0) {
        uncheck(() -> fieldIterator.next().set(mockT, defaultValue));
      }
      while (allValue.hasNext()) {
        if (!fieldIterator.hasNext()) {
          break;
        }
        Field field = fieldIterator.next();
        Object next = allValue.next();
        uncheck(() -> field.set(mockT, next));
        map.put(next, field);
      }
      fieldIterator.forEachRemaining(f -> uncheck(() -> f.set(mockT, defaultValue)));
      value = invoke.apply(mockT);
      if (value != defaultValue) {
        return map.get(value);
      }
      count += map.size();
    }
  }

  @SuppressWarnings("unchecked")
  private <E> Iterator<E> getAllValue(Class<E> clz) {
    switch (PrimitiveTypeUtil.toPrimitive(clz).getName()) {
    case "int":
      return (Iterator<E>) this.<Integer> getIterator(0, i -> i == Integer.MAX_VALUE ? null : i + 1);
    case "short":
      return (Iterator<E>) this.<Short> getIterator((short) 0, i -> i == Short.MAX_VALUE ? null : (short) (i + 1));
    case "long":
      return (Iterator<E>) this.<Long> getIterator(0L, i -> i == Long.MAX_VALUE ? null : i + 1L);
    case "double":
      return (Iterator<E>) this.<Double> getIterator(0d, i -> i >= 100 ? null : i + 1d);
    case "float":
      return (Iterator<E>) this.<Float> getIterator(0f, i -> i >= 100 ? null : i + 1f);
    case "boolean":
      return (Iterator<E>) this.<Boolean> getIterator(Boolean.TRUE, i -> i ? Boolean.FALSE : null);
    case "char":
      return (Iterator<E>) this.<Character> getIterator((char) 0, i -> i == Character.MAX_VALUE ? null : (char) (i + 1));
    case "byte":
      return (Iterator<E>) this.<Byte> getIterator((byte) 0, i -> i == Byte.MAX_VALUE ? null : (byte) (i + 1));
    default:
      throw new IllegalArgumentException("Not a primitive type.");
    }
  }

  private <E> Iterator<E> getIterator(E first, Function<E, E> func) {
    return new Iterator<E>() {
      E next = first;

      @Override
      public boolean hasNext() {
        return next != null;
      }

      @Override
      public E next() {
        E current = next;
        next = func.apply(current);
        return current;
      }
    };
  }

  @Override
  public <O> Field getField(Function<T, O> invoke) {
    return firstNonNull(
        () -> objectMap.get(invoke.apply(mockT)),
        () -> getPrimitive(invoke))
            .orElseThrow(() -> new IllegalStateException("The given value isn't the mock object's property."));
  }
}
