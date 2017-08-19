package xdean.reflection.getter.impl;

import java.lang.reflect.Method;
import java.util.function.Function;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import xdean.reflection.Invocation;
import xdean.reflection.InvocationContext;
import xdean.reflection.getter.MethodGetter;

/**
 * 350ms for 1M times construct<br>
 * 115ms for 1M times getName
 *
 * @author XDean
 *
 * @param <T>
 */
public class ProxyMethodGetter<T> implements MethodGetter<T>, MethodInterceptor {
  T t;

  @SuppressWarnings("unchecked")
  public ProxyMethodGetter(Class<T> clz) {
    Enhancer en = new Enhancer();
    en.setSuperclass(clz);
    en.setCallback(this);
    t = (T) en.create(new Class[] { Object.class, Object.class }, new Object[] { null, null });
  }

  public Method get(Object fieldValue) {
    return InvocationContext.getLastInvocation()
        .map(Invocation::getMethod)
        .orElseThrow(() -> new IllegalArgumentException());
  }

  public T get() {
    return t;
  }

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    InvocationContext.putInvocation(new Invocation(obj, method, args));
    return null;
  }

  @Override
  public Method get(Function<T, ?> invoke) {
    return get(invoke.apply(get()));
  }
}
