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

  String getName(Function<T, ?> invoke);

  <C> Class<? extends C> getType(Function<T, C> invoke);
}
