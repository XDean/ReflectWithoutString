package xdean.reflect.getter;

import static xdean.jex.util.task.TaskUtil.firstSuccess;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import xdean.jex.util.task.tryto.Try;

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
   * @param invoke
   * @return
   */
  default Method get(BiConsumer<T, ?> setter) {
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

  default String getName(BiConsumer<T, ?> setter) {
    return getName(Helper.setterToFunction(setter));
  }

  @Override
  default Class<?> getType(Function<T, ?> invoke) {
    return get(invoke).getReturnType();
  }

  default Class<?> getType(BiConsumer<T, ?> setter) {
    return getType(Helper.setterToFunction(setter));
  }

  default String nameOf(BiConsumer<T, ?> setter) {
    return nameOf(Helper.setterToFunction(setter));
  }

  default Class<?> typeOf(BiConsumer<T, ?> setter) {
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
        Try.to(() -> setter.accept(c, null))
            .recover(adapt(e -> setter.accept(c, (V) (Integer) 0)))
            .recover(adapt(e -> setter.accept(c, (V) (Double) 0.0)))
            .recover(adapt(e -> setter.accept(c, (V) (Boolean) false)))
            .recover(adapt(e -> setter.accept(c, (V) (Float) 0.0f)))
            .recover(adapt(e -> setter.accept(c, (V) (Long) 0l)))
            .recover(adapt(e -> setter.accept(c, (V) (Character) '0')))
            .recover(adapt(e -> setter.accept(c, (V) (Byte) (byte) 0)))
            .recover(adapt(e -> setter.accept(c, (V) (Short) (short) 0)));
        return null;
      };
    }

    private static <T, S> Function<T, S> adapt(Consumer<T> o) {
      return t -> {
        o.accept(t);
        return null;
      };
    }
  }
}
