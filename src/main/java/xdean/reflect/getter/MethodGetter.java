package xdean.reflect.getter;

import java.lang.reflect.Method;
import java.util.function.Consumer;

@FunctionalInterface
public interface MethodGetter<T> {
  /**
   * Get method by invoke getter.
   */
  Method getMethod(Consumer<T> invoke);

  default String getMethodName(Consumer<T> invoke) {
    return getMethod(invoke).getName();
  }

  default Class<?> getMethodType(Consumer<T> invoke) {
    return getMethod(invoke).getReturnType();
  }
}
