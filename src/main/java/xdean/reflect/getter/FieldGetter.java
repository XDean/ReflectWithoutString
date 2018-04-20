package xdean.reflect.getter;

import java.lang.reflect.Field;
import java.util.function.Function;

@FunctionalInterface
public interface FieldGetter<T> {
  /**
   * Get field by invoke getter.
   */
  <O> Field getField(Function<T, O> getter);

  default <O> String getFieldName(Function<T, O> getter) {
    return getField(getter).getName();
  }

  default <O> Class<?> getFieldType(Function<T, O> getter) {
    return getField(getter).getType();
  }
}
