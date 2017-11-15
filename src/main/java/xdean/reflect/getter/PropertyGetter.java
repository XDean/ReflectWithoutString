package xdean.reflect.getter;

import java.util.function.Function;

import xdean.jex.internal.codecov.CodecovIgnore;

/**
 * Get property information by invocation.
 *
 * <pre>
 * <code>
 * class Bean {
 *   TheType beanProp;
 *
 *   public TheType getBeanProp() {
 *     return beanProp;
 *   }
 * }
 *
 * PropertyGetter&#60;Bean&#62; pg = ...;
 * //name="beanProp"
 * String name = pg.getName(b -&#62; b.getBeanProp());
 * //clz = TheType.class, even if beanProp's actual type is subclass of TheType
 * Class&#60;?&#62; clz =  pg.getType(b -&#62; b.getBeanProp());
 * </code>
 * </pre>
 *
 * @author XDean
 *
 * @param <T>
 */
@CodecovIgnore
public interface PropertyGetter<T> {

  /**
   * Get name of property related in the invocation.
   *
   * @param invoke
   * @return
   */
  String getName(Function<T, ?> invoke);

  /**
   * Get type of property related in the invocation.
   *
   * @param invoke
   * @return
   */
  Class<?> getType(Function<T, ?> invoke);

  /**
   * More readable version of {@link #getName(Function)}.
   *
   * @param invoke
   * @return
   */
  default String nameOf(Function<T, ?> invoke) {
    return getName(invoke);
  }

  /**
   * More readable version of {@link #getType(Function)}.
   *
   * @param invoke
   * @return
   */
  default Class<?> typeOf(Function<T, ?> invoke) {
    return getType(invoke);
  }

  /**
   * Whether the direct field invoke is supported. If return true, you can use:
   *
   * <pre>
   * <code>
   *  PropertyGetter pg = ...;
   *  pg.getName(b -&#62; b.beanProp);
   * </code>
   * </pre>
   *
   * @return
   */
  default boolean supportFieldInvoke() {
    return false;
  }
}
