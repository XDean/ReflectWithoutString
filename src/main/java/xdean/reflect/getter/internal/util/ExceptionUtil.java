package xdean.reflect.getter.internal.util;

public interface ExceptionUtil {
  @SuppressWarnings("unchecked")
  public static <T extends Throwable, R> R throwAsUncheck(Throwable t) throws T {
    throw (T) t;
  }

  public static void uncheck(ActionE0<?> task) {
    try {
      task.call();
    } catch (Exception t) {
      throwAsUncheck(t);
    }
  }

  public static <T> T uncheck(FuncE0<T, ?> task) {
    try {
      return task.call();
    } catch (Exception t) {
      return throwAsUncheck(t);
    }
  }

  /**
   * @param task
   * @return can be null
   */
  public static <T> T uncatch(FuncE0<T, ?> task) {
    try {
      return task.call();
    } catch (Exception t) {
      return null;
    }
  }
}
