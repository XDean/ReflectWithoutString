package xdean.reflection.property;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import xdean.reflection.Invocation;
import xdean.reflection.InvocationContext;

/**
 * 350ms for 1M times construct<br>
 * 115ms for 1M times getName
 *
 * @author XDean
 *
 * @param <T>
 */
public class ProxyPropertyNameGetter<T> implements PropertyNameGetter<T>, MethodInterceptor {
  T t;

  @SuppressWarnings("unchecked")
  public ProxyPropertyNameGetter(Class<T> clz) {
    Enhancer en = new Enhancer();
    en.setSuperclass(clz);
    en.setCallback(this);
    t = (T) en.create(new Class[] { Object.class, Object.class }, new Object[] { null, null });
  }

  @Override
  public String getName(Object fieldValue) {
    return InvocationContext.getLastInvocation()
        .map(i -> i.getMethod().getName())
        .orElseThrow(() -> new IllegalArgumentException());
  }

  @Override
  public T get() {
    return t;
  }

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    InvocationContext.putInvocation(new Invocation(obj, method, args));
    return null;
  }
}
