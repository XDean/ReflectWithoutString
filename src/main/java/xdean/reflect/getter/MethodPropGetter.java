package xdean.reflect.getter;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Get {@link Method} from an invocation. <br>
 * If invoke getter/setter method, you can use the convenient method {@link #getName(Function)} and
 * {@link #getType(Function)} to get the property's information (from the Method's signature, so it
 * works even there is no backing field).
 *
 * @author XDean
 * @param <T>
 */
@FunctionalInterface
public interface MethodPropGetter<T> extends MethodGetter<T>, PropertyGetter<T> {

  @Override
  default <O> String getPropName(Function<T, O> getter) {
    return Helper.getterToName(getMethod(getter::apply).getName());
  }

  @Override
  default <O> Class<?> getPropType(Function<T, O> getter) {
    return getMethod(getter::apply).getReturnType();
  }

  @Override
  default <O> String getPropName(BiConsumer<T, O> setter) {
    return getPropName(Helper.setterToGetter(setter));
  }

  @Override
  default <O> Class<?> getPropType(BiConsumer<T, O> setter) {
    return getPropType(Helper.setterToGetter(setter));
  }
}
