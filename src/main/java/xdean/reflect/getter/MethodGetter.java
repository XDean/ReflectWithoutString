package xdean.reflect.getter;

import java.lang.reflect.Method;

import xdean.reflect.getter.internal.util.ActionE1;

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
  Method getMethod(ActionE1<T, ?> invoke);

  /**
   * Convenient get method name from invocation.
   */
  default String getMethodName(ActionE1<T, ?> invoke) {
    return getMethod(invoke).getName();
  }

  /**
   * Convenient get method type from invocation.
   */
  default Class<?> getMethodType(ActionE1<T, ?> invoke) {
    return getMethod(invoke).getReturnType();
  }
}
