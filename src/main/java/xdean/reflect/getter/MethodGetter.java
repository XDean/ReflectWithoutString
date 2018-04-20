package xdean.reflect.getter;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Get {@link Method} from invocation.
 *
 * @author Dean Xu (XDean@github.com)
 */
@FunctionalInterface
public interface MethodGetter<T> {

  /**
   * Get method from invocation. And the invocation's parameters are irrelevant.
   */
  Method getMethod(Consumer<T> invoke);

  /**
   * Convenient get method name from invocation.
   */
  default String getMethodName(Consumer<T> invoke) {
    return getMethod(invoke).getName();
  }

  /**
   * Convenient get method type from invocation.
   */
  default Class<?> getMethodType(Consumer<T> invoke) {
    return getMethod(invoke).getReturnType();
  }
}
