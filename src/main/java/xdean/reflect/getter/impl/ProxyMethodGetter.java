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
 * More expensive than UnsafeFieldGetter to construct.(Because need generate byte code.)
 *
 * @author XDean
 *
 * @param <T>
 */
public class ProxyMethodGetter<T> implements MethodGetter<T> {
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

  public static Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {
    putMethod(method);
    return null;
  }

  private T mockT;

  /**
   *
   * @param clz
   * @throws IllegalStateException If construct the mock object failed.
   */
  @SuppressWarnings("unchecked")
  public ProxyMethodGetter(Class<T> clz) throws IllegalStateException {
    try {
      if (Modifier.isFinal(clz.getModifiers())) {
        throw new IllegalArgumentException("Can't mock final class.");
      }
      Enhancer e = new Enhancer();
      e.setSuperclass(clz);
      e.setUseCache(true);
      e.setCallbackType(MethodInterceptor.class);
      Class<? extends T> createClass = e.createClass();
      Enhancer.registerCallbacks(createClass, new Callback[] { (MethodInterceptor) ProxyMethodGetter::intercept });
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

  /**
   * Get the mocked object. You can perform invocation on it directly. <br>
   * For example:
   *
   * <pre>
   * <code>
   * FieldGetterImpl fgi = new FieldGetterImpl(SomeClass.class);
   * fgi.getName(o -> o.prop);
   * // is same as
   * SomeClass sc = fgi.getMockObject();
   * fgi.getName(sc.prop);
   * </code>
   * </pre>
   *
   * @return
   */
  public T getMockObject() {
    return mockT;
  }

  @Override
  public Method get(Function<T, ?> invoke) {
    return get(invoke.apply(getMockObject()));
  }

  /**
   * Get Method by an invocation result
   *
   * @param o a invocation result of the mock object
   * @return
   * @see #getMockObject()
   */
  public Method get(Object invoke) {
    return getMethod().orElseThrow(() -> new IllegalArgumentException("No method invoked."));
  }

  /**
   * Get Method name by an invocation result
   *
   * @param o an invocation result of the mock object
   * @return
   * @see #getMockObject()
   */
  public String getName(Object o) {
    return get(o).getName();
  }

  /**
   * Get Method type by an invocation result
   *
   * @param o an invocation result of the mock object
   * @return
   * @see #getMockObject()
   */
  public Class<?> getType(Object o) {
    return get(o).getReturnType();
  }

  /**
   * More readable version of {@link #getName(String)}
   *
   * @param o an invocation result of the mock object
   * @see #getMockObject()
   * @return
   */
  public String nameOf(Object o) {
    return getName(o);
  }

  /**
   * More readable version of {@link #getName(String)}
   *
   * @param o an invocation result of the mock object
   * @see #getMockObject()
   * @return
   */
  public Class<?> typeOf(Object o) {
    return getType(o);
  }
}
