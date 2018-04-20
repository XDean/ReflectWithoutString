package xdean.reflect.getter;

import java.lang.reflect.Method;
import java.util.function.Consumer;

@FunctionalInterface
public interface MethodGetter<T> {
  /**
   * Get method by invoke getter.
   */
  Method getMethod(Consumer<T> invoke);
}
