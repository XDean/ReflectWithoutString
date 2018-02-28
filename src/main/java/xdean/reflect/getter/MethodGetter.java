package xdean.reflect.getter;

import static xdean.reflect.getter.util.TaskUtil.firstSuccess;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import xdean.reflect.getter.util.TaskUtil;

/**
 * Get {@link Method} from an invocation. <br>
 * If invoke getter/setter method, you can use the convenient method {@link #getName(Function)} and
 * {@link #getType(Function)} to get the property's information (from the Method's signature, so it works even there is
 * no backing field).
 *
 * @author XDean
 * @param <T>
 */
public interface MethodGetter<T> extends InvokeGetter<T, Method>, PropertyGetter<T> {

  int UP_LOW_GAP = 'a' - 'A';

  /**
   * An adapter for setter method reference
   *
   * @param setter
   * @return
   */
  default <O> Method get(BiConsumer<T, O> setter) {
    return get(Helper.setterToFunction(setter));
  }

  @Override
  default String getName(Function<T, ?> invoke) {
    String name = get(invoke).getName();
    return Optional.ofNullable(
        firstSuccess(
            () -> Helper.getterToName(name),
            () -> Helper.setterToName(name)))
        .orElseThrow(() -> new IllegalArgumentException("Can't get property name from method name: " + name));
  }

  default <O> String getName(BiConsumer<T, O> setter) {
    return getName(Helper.setterToFunction(setter));
  }

  @Override
  default Class<?> getType(Function<T, ?> invoke) {
    return get(invoke).getReturnType();
  }

  default <O> Class<?> getType(BiConsumer<T, O> setter) {
    return getType(Helper.setterToFunction(setter));
  }

  default <O> String nameOf(BiConsumer<T, O> setter) {
    return nameOf(Helper.setterToFunction(setter));
  }

  default <O> Class<?> typeOf(BiConsumer<T, O> setter) {
    return typeOf(Helper.setterToFunction(setter));
  }

  class Helper {
    /**
     * Convert getter method name to the property name
     *
     * @param getterName
     * @return
     */
    private static String getterToName(String getterName) {
      if (getterName.startsWith("get") && getterName.length() > 3) {
        return ((char) (getterName.charAt(3) + UP_LOW_GAP)) + getterName.substring(4);
      } else if (getterName.startsWith("is") && getterName.length() > 2) {
        return ((char) (getterName.charAt(2) + UP_LOW_GAP)) + getterName.substring(3);
      } else {
        throw new IllegalArgumentException("Getter method name must be (get|is)XxxXxx, but was \"" + getterName + "\".");
      }
    }

    private static String setterToName(String setterName) {
      if (setterName.startsWith("set") && setterName.length() > 3) {
        return ((char) (setterName.charAt(3) + UP_LOW_GAP)) + setterName.substring(4);
      } else {
        throw new IllegalArgumentException("Setter method name must be (get|is)XxxXxx, but was \"" + setterName + "\".");
      }
    }

    @SuppressWarnings("unchecked")
    private static <T, V> Function<T, ?> setterToFunction(BiConsumer<T, V> setter) {
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
}
