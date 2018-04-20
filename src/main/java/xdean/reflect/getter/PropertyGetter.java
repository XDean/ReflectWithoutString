package xdean.reflect.getter;

import java.util.function.BiConsumer;
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
public interface PropertyGetter<T> {

  /**
   * Get name of property by invoke getter.
   */
  <O> String getName(Function<T, O> getter);

  /**
   * Get type of property by invoke getter.
   */
  <O> Class<?> getType(Function<T, O> getter);

  /**
   * Get name of property by invoke setter.
   */
  <O> String getName(BiConsumer<T, O> setter);

  /**
   * Get type of property by invoke setter.
   */
  <O> Class<?> getType(BiConsumer<T, O> setter);

  /**
   * More readable version of {@link #getName(Function)}.
   */
  default <O> String nameOf(Function<T, O> getter) {
    return getName(getter);
  }

  /**
   * More readable version of {@link #getType(Function)}.
   */
  default <O> Class<?> typeOf(Function<T, O> getter) {
    return getType(getter);
  }

  /**
   * More readable version of {@link #getName(BiConsumer)}.
   */
  default <O> String nameOf(BiConsumer<T, O> setter) {
    return getName(setter);
  }

  /**
   * More readable version of {@link #getType(BiConsumer)}.
   */
  default <O> Class<?> typeOf(BiConsumer<T, O> setter) {
    return getType(setter);
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
