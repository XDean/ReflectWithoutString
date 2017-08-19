package xdean.reflection.getter.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import sun.misc.Unsafe;
import xdean.jex.util.lang.PrimitiveTypeUtil;
import xdean.jex.util.lang.UnsafeUtil;
import xdean.jex.util.log.Logable;
import xdean.jex.util.reflect.ReflectUtil;
import xdean.reflection.getter.FieldGetter;

public class UnsafeFieldGetter<T> implements FieldGetter<T>, Logable {

  private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

  private T t;
  private Map<Object, Field> primitiveMap = new HashMap<>();
  private Map<Object, Field> objectMap = new IdentityHashMap<>();

  @SuppressWarnings("unchecked")
  public UnsafeFieldGetter(Class<T> clz) throws InstantiationException {
    t = (T) UNSAFE.allocateInstance(clz);
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
    UNSAFE.putObject(t, offset, o);
    objectMap.put(o, field);
  }

  private Object newObject(Class<?> clz) throws InstantiationException {
    return UNSAFE.allocateInstance(clz);
  }

  private Object newAbstract(Class<?> clz) throws InstantiationException {
    return newObject(newClass(clz));
  }

  private Class<?> newClass(Class<?> clz) {
    Enhancer enhancer = new Enhancer();
    if (clz.isInterface()) {
      enhancer.setInterfaces(new Class[] { clz });
    } else {
      enhancer.setSuperclass(clz);
    }
    enhancer.setCallbackType(NoOp.class);
    return enhancer.createClass();
  }

  private void handleArray(Field field) {
    Array.newInstance(field.getType(), 0);
    long offset = UNSAFE.objectFieldOffset(field);
    Object array = Array.newInstance(Object.class, 0);
    UNSAFE.putObject(t, offset, array);
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

  short booleanCount = 0;

  private void handleBoolean(Field field) {
    checkRange(field, 1, booleanCount);
    long offset = UNSAFE.objectFieldOffset(field);
    boolean bool = booleanCount == 0;
    UNSAFE.putBoolean(t, offset, bool);
    primitiveMap.put(bool, field);
    booleanCount++;
  }

  short byteCount = 0;

  private void handleByte(Field field) {
    checkRange(field, Byte.SIZE, byteCount);
    long offset = UNSAFE.objectFieldOffset(field);
    byte b = (byte) byteCount;
    UNSAFE.putByte(t, offset, b);
    primitiveMap.put(b, field);
    byteCount++;
  }

  int charCount;

  private void handleChar(Field field) {
    checkRange(field, Character.SIZE, charCount);
    long offset = UNSAFE.objectFieldOffset(field);
    char i = (char) charCount;
    UNSAFE.putInt(t, offset, i);
    primitiveMap.put(i, field);
    charCount++;
  }

  long intCount;

  private void handleInt(Field field) {
    checkRange(field, Integer.SIZE, intCount);
    long offset = UNSAFE.objectFieldOffset(field);
    int i = (int) intCount;
    UNSAFE.putInt(t, offset, i);
    primitiveMap.put(i, field);
    intCount++;
  }

  int shortCount;

  private void handleShort(Field field) {
    checkRange(field, Short.SIZE, shortCount);
    long offset = UNSAFE.objectFieldOffset(field);
    short i = (short) shortCount;
    UNSAFE.putShort(t, offset, i);
    primitiveMap.put(i, field);
    shortCount++;
  }

  long longCount;

  private void handleLong(Field field) {
    checkRange(field, Long.SIZE, longCount);
    long offset = UNSAFE.objectFieldOffset(field);
    long l = longCount;
    UNSAFE.putLong(t, offset, l);
    primitiveMap.put(l, field);
    longCount++;
  }

  long floatCount;

  private void handleFloat(Field field) {
    checkRange(field, Float.SIZE, floatCount);
    long offset = UNSAFE.objectFieldOffset(field);
    float f = Float.intBitsToFloat((int) floatCount);
    UNSAFE.putFloat(t, offset, f);
    primitiveMap.put(f, field);
    floatCount++;
  }

  long doubleCount;

  private void handleDouble(Field field) {
    checkRange(field, Double.SIZE, doubleCount);
    long offset = UNSAFE.objectFieldOffset(field);
    double d = Double.longBitsToDouble(doubleCount);
    UNSAFE.putDouble(t, offset, d);
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
      IllegalStateException e = getException(field, bits);
      log().error(e.getMessage(), e);
      throw e;
    }
  }

  private IllegalStateException getException(Field field, int bits) {
    Class<?> type = field.getType();
    return new IllegalStateException(String.format(
        "Can't generate %s preoperty (%s)'s name getter, only support %s %ss.",
        type.getName(), field.getName(), 1L << bits, type.getName()));
  }

  public T getMock() {
    return t;
  }

  public Field get(Object o) {
    return Optional.ofNullable(objectMap.get(o)).orElseGet(() -> primitiveMap.get(o));
  }

  @Override
  public Field get(Function<T, ?> invoke) {
    return get(invoke.apply(getMock()));
  }

}
