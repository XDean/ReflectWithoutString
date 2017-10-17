package xdean.reflect.getter;

import java.lang.reflect.Field;
import java.util.function.Function;

import xdean.jex.internal.codecov.CodecovIgnore;

/**
 * Get {@link Field} from an invocation.
 *
 * @author XDean
 *
 * @param <T>
 */
@CodecovIgnore
public interface FieldGetter<T> extends InvokeGetter<T, Field>, PropertyGetter<T> {

  @Override
  default String getName(Function<T, ?> invoke) {
    return get(invoke).getName();
  }

  @Override
  default Class<?> getType(Function<T, ?> invoke) {
    return get(invoke).getType();
  }
}
