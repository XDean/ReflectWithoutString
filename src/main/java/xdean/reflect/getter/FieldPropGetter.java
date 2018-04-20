package xdean.reflect.getter;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * {@link FieldGetter} and {@link PropertyGetter}
 * 
 * @author Dean Xu (XDean@github.com)
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

  /**
   * Default not support setter because setter will be considered getter. For example
   * {@code getPropName(o -> o.id = 1)} will dispatch to getPropName(Function) not
   * getPropName(BiConsumer)
   */
  @Override
  default <O> String getPropName(BiConsumer<T, O> setter) {
    throw new UnsupportedOperationException("Not supported now, how about use getter instead.");
  }

  /**
   * @see #getPropName(BiConsumer)
   */
  @Override
  default <O> Class<?> getPropType(BiConsumer<T, O> setter) {
    throw new UnsupportedOperationException("Not supported now, how about use getter instead.");
  }

  @Override
  default boolean supportFieldInvoke() {
    return true;
  }
}
