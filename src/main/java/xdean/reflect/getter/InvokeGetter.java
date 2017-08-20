package xdean.reflect.getter;

import java.util.function.Function;

/**
 * Get something from class by invocation.
 *
 * @author XDean
 *
 * @param <T> The target class to reflect
 * @param <O> The output
 */
public interface InvokeGetter<T, O> {
  O get(Function<T, ?> invoke);
}
