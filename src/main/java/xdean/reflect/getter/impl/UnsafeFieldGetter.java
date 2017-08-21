package xdean.reflect.getter.impl;

import static xdean.jex.util.lang.ExceptionUtil.throwIt;
import static xdean.jex.util.task.TaskUtil.firstSuccess;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import sun.misc.Unsafe;
import xdean.jex.util.lang.PrimitiveTypeUtil;
import xdean.jex.util.lang.UnsafeUtil;
import xdean.jex.util.log.Logable;
import xdean.jex.util.reflect.ReflectUtil;
import xdean.reflect.getter.FieldGetter;

/**
 * Based on {@link Unsafe}.<br>
 * Supports all java class, but limit by primitive type field amount. For each primitive type with n bytes size, there
 * at most have 2^n this type fields. For example, construct {@code FieldGetterImpl} with a class with 3 boolean fields
 * will lead a {@code IllegalArgumentException}.
 *
 * @author XDean
 *
 * @param <T>
 */
public class UnsafeFieldGetter<T> implements FieldGetter<T>, Logable {

  private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

  private T mockT;
  private Map<Object, Field> primitiveMap = new HashMap<>();
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
    enhancer.setUseCache(false);
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
    switch (field.getType().getName()) {
    case "int":
      handleInt(field);
      break;
    case "short":
      handleShort(field);
      break;
    case "long":
      handleLong(field);
      break;
    case "double":
      handleDouble(field);
      break;
    case "float":
      handleFloat(field);
      break;
    case "boolean":
      handleBoolean(field);
      break;
    case "char":
      handleChar(field);
      break;
    case "byte":
      handleByte(field);
      break;
    default:
      throw new IllegalArgumentException("Not a primitive type.");
    }
  }

  private short booleanCount = 0;

  private void handleBoolean(Field field) {
    checkRange(field, 1, booleanCount);
    long offset = UNSAFE.objectFieldOffset(field);
    boolean bool = booleanCount == 0;
    UNSAFE.putBoolean(mockT, offset, bool);
    primitiveMap.put(bool, field);
    booleanCount++;
  }

  private short byteCount = 0;

  private void handleByte(Field field) {
    checkRange(field, Byte.SIZE, byteCount);
    long offset = UNSAFE.objectFieldOffset(field);
    byte b = (byte) byteCount;
    UNSAFE.putByte(mockT, offset, b);
    primitiveMap.put(b, field);
    byteCount++;
  }

  private int charCount;

  private void handleChar(Field field) {
    checkRange(field, Character.SIZE, charCount);
    long offset = UNSAFE.objectFieldOffset(field);
    char i = (char) charCount;
    UNSAFE.putInt(mockT, offset, i);
    primitiveMap.put(i, field);
    charCount++;
  }

  private long intCount;

  private void handleInt(Field field) {
    checkRange(field, Integer.SIZE, intCount);
    long offset = UNSAFE.objectFieldOffset(field);
    int i = (int) intCount;
    UNSAFE.putInt(mockT, offset, i);
    primitiveMap.put(i, field);
    intCount++;
  }

  private int shortCount;

  private void handleShort(Field field) {
    checkRange(field, Short.SIZE, shortCount);
    long offset = UNSAFE.objectFieldOffset(field);
    short i = (short) shortCount;
    UNSAFE.putShort(mockT, offset, i);
    primitiveMap.put(i, field);
    shortCount++;
  }

  private long longCount;

  private void handleLong(Field field) {
    checkRange(field, Long.SIZE, longCount);
    long offset = UNSAFE.objectFieldOffset(field);
    long l = longCount;
    UNSAFE.putLong(mockT, offset, l);
    primitiveMap.put(l, field);
    longCount++;
  }

  private long floatCount;

  private void handleFloat(Field field) {
    checkRange(field, Float.SIZE, floatCount);
    long offset = UNSAFE.objectFieldOffset(field);
    float f = Float.intBitsToFloat((int) floatCount);
    UNSAFE.putFloat(mockT, offset, f);
    primitiveMap.put(f, field);
    floatCount++;
  }

  private long doubleCount;

  private void handleDouble(Field field) {
    checkRange(field, Double.SIZE, doubleCount);
    long offset = UNSAFE.objectFieldOffset(field);
    double d = Double.longBitsToDouble(doubleCount);
    UNSAFE.putDouble(mockT, offset, d);
    primitiveMap.put(d, field);
    doubleCount++;
  }

  private void checkRange(Field field, int bits, short currentCount) {
    checkRange(field, bits, Short.toUnsignedLong(currentCount));
  }

  private void checkRange(Field field, int bits, int currentCount) {
    checkRange(field, bits, Integer.toUnsignedLong(currentCount));
  }

  private void checkRange(Field field, int bits, long currentCount) {
    if ((bits < Long.SIZE && currentCount == 1L << bits) || currentCount == -1L) {
      RuntimeException e = getException(field, bits);
      log().error(e.getMessage(), e);
      throw e;
    }
  }

  private RuntimeException getException(Field field, int bits) {
    Class<?> type = field.getType();
    return new IllegalArgumentException(String.format(
        "Can't generate %s preoperty (%s)'s name getter, only support %s %ss.",
        type.getName(), field.getName(), 1L << bits, type.getName()));
  }

  /**
   * Get the mocked object. You can perform invocation on it directly. <br>
   * For example:
   *
   * <pre>
   * <code>
   * FieldGetterImpl fgi = new FieldGetterImpl(SomeClass.class);
   * fgi.getName(o -> o.prop);
   * // is same as
   * SomeClass sc = fgi.getMockObject();
   * fgi.getName(sc.prop);
   * </code>
   * </pre>
   *
   * @return
   */
  public T getMockObject() {
    return mockT;
  }

  @Override
  public boolean supportFieldInvoke() {
    return true;
  }

  @Override
  public Field get(Function<T, ?> invoke) {
    return get(invoke.apply(getMockObject()));
  }

  /**
   * Get field by a property value
   *
   * @param o a property value of the mock object
   * @return
   * @see #getMockObject()
   */
  public Field get(Object o) {
    return firstSuccess(
        () -> objectMap.get(o),
        () -> primitiveMap.get(o),
        () -> throwIt(new IllegalStateException("The given value isn't the mock object's property.")));
  }

  /**
   * Get field name by a property value
   *
   * @param o a property value of the mock object
   * @return
   * @see #getMockObject()
   */
  public String getName(Object o) {
    return get(o).getName();
  }

  /**
   * Get field type by a property value
   *
   * @param o a property value of the mock object
   * @return
   * @see #getMockObject()
   */
  @SuppressWarnings("unchecked")
  public <C> Class<? super C> getType(C o) {
    return (Class<? super C>) get(o).getType();
  }

  /**
   * More readable version of {@link #getName(String)}
   *
   * @param o a property value of the mock object
   * @see #getMockObject()
   * @return
   */
  public String nameOf(Object o) {
    return getName(o);
  }

  /**
   * More readable version of {@link #getName(String)}
   *
   * @param o a property value of the mock object
   * @see #getMockObject()
   * @return
   */
  public <C> Class<? super C> typeOf(C o) {
    return getType(o);
  }
}
