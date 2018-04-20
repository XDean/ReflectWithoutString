package xdean.reflect.getter;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Get {@link Field} from an invocation.
 *
 * @author XDean
 *
 * @param <T>
 */
public interface FieldPropGetter<T> extends FieldGetter<T>, PropertyGetter<T> {
  @Override
  default <O> String getPropName(Function<T, O> getter) {
    return getField(getter::apply).getName();
  }

  @Override
  default <O> Class<?> getPropType(Function<T, O> getter) {
    return getField(getter::apply).getType();
  }

  @Override
  default <O> String getPropName(BiConsumer<T, O> setter) {
    throw new UnsupportedOperationException();
  }

  @Override
  default <O> Class<?> getPropType(BiConsumer<T, O> setter) {
    throw new UnsupportedOperationException();
  }
}
