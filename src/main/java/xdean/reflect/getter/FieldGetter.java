package xdean.reflect.getter;

import java.lang.reflect.Field;
import java.util.function.Function;

@FunctionalInterface
public interface FieldGetter<T> {
  /**
   * Get field by invoke getter.
   */
  <O> Field getField(Function<T, O> getter);
}
