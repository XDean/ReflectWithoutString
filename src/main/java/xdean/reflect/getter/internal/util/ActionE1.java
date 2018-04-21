package xdean.reflect.getter.internal.util;

@FunctionalInterface
public interface ActionE1<A, E extends Exception> {
  void call(A a) throws E;
}
