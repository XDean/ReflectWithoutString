package xdean.reflection.getter.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import xdean.jex.util.lang.UnsafeUtil;
import xdean.jex.util.reflect.ReflectUtil;
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
  private static final String BIND_CALLBACK;
  static {
    try {
      BIND_CALLBACK = ReflectUtil.<Enhancer, Signature> getFieldValue(Enhancer.class, null, "BIND_CALLBACKS")
          .getName();
    } catch (NoSuchFieldException e) {
      throw new IllegalStateException("Can't find Enhancer.BIND_CALLBACK field.", e);
    }
  }
  T mockT;

  @SuppressWarnings("unchecked")
  public ProxyMethodGetter(Class<T> clz) {
    try {
      Enhancer e = new Enhancer();
      e.setSuperclass(clz);
      e.setUseCache(false);
      e.setCallbackType(MethodInterceptor.class);
      Class<? extends T> createClass = e.createClass();
      Enhancer.registerCallbacks(createClass, new Callback[] { this });
      T object = (T) UnsafeUtil.getUnsafe().allocateInstance(createClass);
      Method bindMethod = createClass.getDeclaredMethod(BIND_CALLBACK, Object.class);
      bindMethod.setAccessible(true);
      bindMethod.invoke(null, object);
      mockT = object;
    } catch (RuntimeException | NoSuchMethodException | InstantiationException | IllegalAccessException
        | InvocationTargetException e) {
      // TODO
      e.printStackTrace();
    }
  }

  public Method get(Object fieldValue) {
    return InvocationContext.getLastInvocation()
        .map(Invocation::getMethod)
        .orElseThrow(() -> new IllegalArgumentException());
  }

  public T getMockObject() {
    return mockT;
  }

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    InvocationContext.putInvocation(new Invocation(obj, method, args));
    return null;
  }

  @Override
  public Method get(Function<T, ?> invoke) {
    return get(invoke.apply(getMockObject()));
  }
}
