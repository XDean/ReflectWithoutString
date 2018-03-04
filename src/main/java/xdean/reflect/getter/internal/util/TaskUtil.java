package xdean.reflect.getter.internal.util;

import static xdean.reflect.getter.internal.util.ExceptionUtil.uncatch;

import java.util.Optional;

/**
 * Utility methods for task flow control.
 *
 * @author XDean
 *
 */
public class TaskUtil {
  /**
   * Return the first result of these tasks<br>
   * IGNORE EXCEPTIONS.
   *
   * @param tasks
   * @return can be null
   * @throws IllegalStateException If all tasks failed.
   */
  @SafeVarargs
  public static <T> T firstSuccess(FuncE0<T, ?>... tasks) throws IllegalStateException {
    for (FuncE0<T, ?> task : tasks) {
      try {
        return task.call();
      } catch (Exception e) {
        // ignore
      }
    }
    throw new IllegalStateException("All tasks failed");
  }

  /**
   * Return the first result of these tasks<br>
   * IGNORE EXCEPTIONS.
   *
   * @param tasks
   * @throws IllegalStateException If all tasks failed.
   */
  @SafeVarargs
  public static void firstSuccess(ActionE0<?>... tasks) throws IllegalStateException {
    for (ActionE0<?> task : tasks) {
      try {
        task.call();
        return;
      } catch (Exception e) {
        // ignore
      }
    }
    throw new IllegalStateException("All tasks failed");
  }

  /**
   * Return the first non-null result of the given tasks or empty if all of them return null.<br>
   * IGNORE EXCEPTIONS.
   *
   * @param tasks
   * @return can be null
   */
  @SafeVarargs
  public static <T> Optional<T> firstNonNull(FuncE0<T, ?>... tasks) {
    for (FuncE0<T, ?> task : tasks) {
      T res = uncatch(task);
      if (res != null) {
        return Optional.of(res);
      }
    }
    return Optional.empty();
  }
}
