package xdean.reflection.property;

import java.util.function.Function;

public interface PropertyNameGetter<T> {

  String getName(Object fieldValue);

  default String getName(Function<T, ?> fieldGetter) {
    return getName(fieldGetter.apply(get()));
  }

  T get();

  default boolean canHandleField() {
    return false;
  }
}
