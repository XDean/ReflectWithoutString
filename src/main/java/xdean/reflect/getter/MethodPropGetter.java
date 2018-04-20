package xdean.reflect.getter;

import java.util.function.BiConsumer;
import java.util.function.Function;

import xdean.reflect.getter.internal.util.GetterSetterUtil;

/**
 * {@link MethodGetter} and {@link PropertyGetter}
 * 
 * @author Dean Xu (XDean@github.com)
 */
@FunctionalInterface
public interface MethodPropGetter<T> extends MethodGetter<T>, PropertyGetter<T> {

  @Override
  default <O> String getPropName(Function<T, O> getter) {
    return GetterSetterUtil.getterToName(getMethod(getter::apply).getName());
  }

  @Override
  default <O> Class<?> getPropType(Function<T, O> getter) {
    return getMethod(getter::apply).getReturnType();
  }

  @Override
  default <O> String getPropName(BiConsumer<T, O> setter) {
    return GetterSetterUtil.setterToName(getMethod(GetterSetterUtil.setterToGetter(setter)::apply).getName());
  }

  @Override
  default <O> Class<?> getPropType(BiConsumer<T, O> setter) {
    return getPropType(GetterSetterUtil.setterToGetter(setter));
  }
}
