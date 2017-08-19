package xdean.reflection.getter;

import java.util.function.Function;

public interface PropertyGetter<T> {
  String getName(Function<T, ?> invoke);

  <C> Class<? extends C> getType(Function<T, C> invoke);
}
