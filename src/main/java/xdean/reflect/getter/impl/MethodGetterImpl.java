package xdean.reflect.getter.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import xdean.jex.util.lang.UnsafeUtil;
import xdean.jex.util.reflect.ReflectUtil;
import xdean.reflect.getter.Invocation;
import xdean.reflect.getter.MethodGetter;

/**
 *
 * @author XDean
 *
 * @param <T>
 */
public class MethodGetterImpl<T> implements MethodGetter<T>, MethodInterceptor {
  private static final String BIND_CALLBACK;
  static {
    try {
      BIND_CALLBACK = ReflectUtil.<Enhancer, Signature> getFieldValue(Enhancer.class, null, "BIND_CALLBACKS")
          .getName();
    } catch (NoSuchFieldException e) {
      throw new IllegalStateException("Can't find Enhancer.BIND_CALLBACK field.", e);
    }
  }

  private static final ThreadLocal<Invocation> LAST_INVOCATION = new ThreadLocal<Invocation>();

  public static Optional<Invocation> getInvocation() {
    return Optional.ofNullable(LAST_INVOCATION.get());
  }

  public static void putInvocation(Invocation invocation) {
    LAST_INVOCATION.set(invocation);
  }

  T mockT;

  @SuppressWarnings("unchecked")
  public MethodGetterImpl(Class<T> clz) {
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
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new Error("Never happen. Check code.", e);
    } catch (InstantiationException e) {
      throw new IllegalStateException(e);
    }
  }

  public Method get(Object fieldValue) {
    return getInvocation()
        .map(Invocation::getMethod)
        .orElseThrow(() -> new IllegalArgumentException());
  }

  public T getMockObject() {
    return mockT;
  }

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    putInvocation(new Invocation(obj, method, args));
    return null;
  }

  @Override
  public Method get(Function<T, ?> invoke) {
    return get(invoke.apply(getMockObject()));
  }
}
