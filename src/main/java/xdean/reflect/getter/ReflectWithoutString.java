package xdean.reflect.getter;

import xdean.codecov.CodecovIgnore;
import xdean.reflect.getter.impl.ProxyMethodGetter;
import xdean.reflect.getter.impl.UnsafeFieldGetter;

/**
 * Entrance utility class to get {@link PropertyGetter}, {@link FieldGetter} or {@link MethodGetter}
 * 
 * @author Dean Xu (XDean@github.com)
 *
 */
@CodecovIgnore
public interface ReflectWithoutString {
  /**
   * Get a default {@link PropertyGetter}
   */
  static <T> PropertyGetter<T> propertyGetter(Class<T> clz) {
    return new UnsafeFieldGetter<>(clz);
  }

  /**
   * Get a default {@link FieldPropGetter}
   */
  static <T> FieldPropGetter<T> fieldGetter(Class<T> clz) {
    return new UnsafeFieldGetter<>(clz);
  }

  /**
   * Get a default {@link MethodGetter}
   */
  static <T> MethodGetter<T> methodGetter(Class<T> clz) {
    return new ProxyMethodGetter<>(clz);
  }
}
