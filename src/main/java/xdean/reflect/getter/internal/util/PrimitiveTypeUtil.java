package xdean.reflect.getter.internal.util;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveTypeUtil {

  private static final Map<Class<?>, Class<?>> wrapperToPrimitive = new HashMap<>();
  static {
    add(boolean.class, Boolean.class);
    add(byte.class, Byte.class);
    add(char.class, Character.class);
    add(double.class, Double.class);
    add(float.class, Float.class);
    add(int.class, Integer.class);
    add(long.class, Long.class);
    add(short.class, Short.class);
  }

  /**
   * Get primitive type of wrapper class. Or itself for other.
   *
   * @param wrapperType
   * @return
   */
  public static Class<?> toPrimitive(final Class<?> wrapperType) {
    return wrapperToPrimitive.getOrDefault(wrapperType, wrapperType);
  }

  /**
   * Determine the class is primitive or not.
   *
   * @param clz
   * @return
   */
  public static boolean isPrimitive(Class<?> clz) {
    return clz.isPrimitive();
  }

  private static void add(final Class<?> primitiveType, final Class<?> wrapperType) {
    wrapperToPrimitive.put(wrapperType, primitiveType);
  }
}
