package xdean.reflect.getter;

import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * Get {@link Field} from invocation
 * 
 * @author Dean Xu (XDean@github.com)
 */
@FunctionalInterface
public interface FieldGetter<T> {
  /**
   * Get {@link Field} from invocation.
   */
  <O> Field getField(Function<T, O> getter);

  /**
   * Convenient get field name from invocation.
   */
  default <O> String getFieldName(Function<T, O> getter) {
    return getField(getter).getName();
  }

  /**
   * Convenient get field type from invocation.
   */
  default <O> Class<?> getFieldType(Function<T, O> getter) {
    return getField(getter).getType();
  }
}
