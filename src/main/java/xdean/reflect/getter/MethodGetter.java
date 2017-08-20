package xdean.reflect.getter;

import java.lang.reflect.Method;
import java.util.function.Function;

public interface MethodGetter<T> extends InvokeGetter<T, Method>, PropertyGetter<T> {

  int UP_LOW_GAP = 'a' - 'A';

  @Override
  default String getName(Function<T, ?> invoke) {
    return getterToName(get(invoke).getName());
  }

  @Override
  @SuppressWarnings("unchecked")
  default <C> Class<? extends C> getType(Function<T, C> invoke) {
    return (Class<? extends C>) get(invoke).getReturnType();
  }

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
