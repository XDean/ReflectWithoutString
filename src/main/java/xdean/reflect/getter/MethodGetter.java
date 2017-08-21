package xdean.reflect.getter;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Get {@link Method} from an invocation. <br>
 * If invoke getter method, you can use the convenient method {@link #getName(Function)} and {@link #getType(Function)}
 * to get the property's information (from the Method's signature, so it works even there is no backing field).
 *
 * @author XDean
 * @param <T>
 */
public interface MethodGetter<T> extends InvokeGetter<T, Method>, PropertyGetter<T> {

  int UP_LOW_GAP = 'a' - 'A';

  @Override
  default String getName(Function<T, ?> invoke) {
    return getterToName(get(invoke).getName());
  }

  @Override
  default Class<?> getType(Function<T, ?> invoke) {
    return get(invoke).getReturnType();
  }

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
}
