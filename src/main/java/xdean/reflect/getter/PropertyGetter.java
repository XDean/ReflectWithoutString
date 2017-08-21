package xdean.reflect.getter;

import java.util.function.Function;

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
 * PropertyGetter<Bean> pg = ...;
 * //name="beanProp"
 * String name = pg.getName(b -> b.getBeanProp());
 * //clz = TheType.class, even if beanProp's actual type is subclass of TheType
 * Class<? extends TheType> clz =  pg.getType(b->b.getBeanProp());
 * </code>
 * </pre>
 *
 * @author XDean
 *
 * @param <T>
 */
public interface PropertyGetter<T> {

  /**
   * Get name of property related in the invocation.
   *
   * @param invoke
   * @return
   */
  String getName(Function<T, ?> invoke);

  <C> Class<? super C> getType(Function<T, C> invoke);

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
  default <C> Class<? super C> typeOf(Function<T, C> invoke) {
    return getType(invoke);
  }

  /**
   * Whether the direct field invoke is supported. If return true, you can use:
   *
   * <pre>
   * <code>
   *  PropertyGetter pg = ...;
   *  pg.getName(b -> b.beanProp);
   * </code>
   * </pre>
   *
   * @return
   */
  default boolean supportFieldInvoke() {
    return false;
  }
}
