package xdean.reflect.getter.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.Function;

import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import xdean.jex.util.lang.UnsafeUtil;
import xdean.jex.util.reflect.ReflectUtil;
import xdean.reflect.getter.MethodGetter;

/**
 * Based on cglib.<br>
 * Don't support FINAL class and method.<br>
 * More expensive to construct than FieldGetterImpl.(Because need generate byte code.)
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

  private static final ThreadLocal<Method> LAST_METHOD = new ThreadLocal<Method>();

  public static Optional<Method> getMethod() {
    return Optional.ofNullable(LAST_METHOD.get());
  }

  public static void putMethod(Method invocation) {
    LAST_METHOD.set(invocation);
  }

  public static void main(String[] args) {
    ProxyMethodGetter<Object> mg = new ProxyMethodGetter<>(Object.class);
    System.out.println(mg.get(o -> o.getClass()));
  }

  private T mockT;

  @SuppressWarnings("unchecked")
  public ProxyMethodGetter(Class<T> clz) throws IllegalStateException {
    try {
      if (Modifier.isFinal(clz.getModifiers())) {
        throw new IllegalArgumentException("Can't mock final class.");
      }
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

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    putMethod(method);
    return null;
  }

  @Override
  public Method get(Function<T, ?> invoke) {
    return get(invoke.apply(getMockObject()));
  }

  public T getMockObject() {
    return mockT;
  }

  public Method get(Object invoke) {
    return getMethod().orElseThrow(() -> new IllegalArgumentException("No method invoked."));
  }

}
