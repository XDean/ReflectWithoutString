package xdean.reflection.getter;

import java.lang.reflect.Method;
import java.util.function.Function;

public interface MethodGetter<T> extends InvokeGetter<T, Method>, PropertyGetter<T> {
  @Override
  default String getName(Function<T, ?> invoke) {
    return get(invoke).getName();
  }

  @Override
  @SuppressWarnings("unchecked")
  default <C> Class<? extends C> getType(Function<T, C> invoke) {
    return (Class<? extends C>) get(invoke).getReturnType();
  }
}
