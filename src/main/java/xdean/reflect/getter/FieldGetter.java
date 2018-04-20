package xdean.reflect.getter;

import java.util.function.Function;

public interface FieldGetter<T> {
  /**
   * Get field by invoke getter.
   */
  <O> String getField(Function<T, O> getter);
}
