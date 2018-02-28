package xdean.reflect.getter.util;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface FuncE0<R, E extends Exception> extends Callable<R> {
  @Override
  R call() throws E;
}
