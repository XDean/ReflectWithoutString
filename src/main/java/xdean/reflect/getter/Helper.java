package xdean.reflect.getter;

import java.util.function.BiConsumer;
import java.util.function.Function;

import xdean.reflect.getter.internal.util.TaskUtil;

interface Helper {
  int UP_LOW_GAP = 'a' - 'A';

  /**
   * Convert getter method name to the property name
   *
   * @param getterName
   * @return
   */
  static String getterToName(String getterName) {
    if (getterName.startsWith("get") && getterName.length() > 3) {
      return ((char) (getterName.charAt(3) + UP_LOW_GAP)) + getterName.substring(4);
    } else if (getterName.startsWith("is") && getterName.length() > 2) {
      return ((char) (getterName.charAt(2) + UP_LOW_GAP)) + getterName.substring(3);
    } else {
      throw new IllegalArgumentException("Getter method name must be (get|is)XxxXxx, but was \"" + getterName + "\".");
    }
  }

  static String setterToName(String setterName) {
    if (setterName.startsWith("set") && setterName.length() > 3) {
      return ((char) (setterName.charAt(3) + UP_LOW_GAP)) + setterName.substring(4);
    } else {
      throw new IllegalArgumentException("Setter method name must be (get|is)XxxXxx, but was \"" + setterName + "\".");
    }
  }

  @SuppressWarnings("unchecked")
  static <T, V> Function<T, ?> setterToGetter(BiConsumer<T, V> setter) {
    return c -> {
      TaskUtil.firstSuccess(() -> setter.accept(c, null),
          () -> setter.accept(c, (V) (Integer) 0),
          () -> setter.accept(c, (V) (Double) 0.0),
          () -> setter.accept(c, (V) (Boolean) false),
          () -> setter.accept(c, (V) (Float) 0.0f),
          () -> setter.accept(c, (V) (Long) 0l),
          () -> setter.accept(c, (V) (Character) '0'),
          () -> setter.accept(c, (V) (Byte) (byte) 0),
          () -> setter.accept(c, (V) (Short) (short) 0));
      return null;
    };
  }
}
