package xdean.reflect.getter;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public interface MethodGetter<T> {
  Method getMethod(Consumer<T> invoke);
}
