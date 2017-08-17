package xdean.reflection.property;

import static xdean.jex.util.task.TaskUtil.uncheck;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import sun.misc.Unsafe;
import xdean.jex.util.lang.PrimitiveTypeUtil;
import xdean.jex.util.lang.UnsafeUtil;
import xdean.jex.util.log.Logable;
import xdean.jex.util.reflect.ReflectUtil;

public class UnsafePropertyNameGetter<T> implements PropertyNameGetter<T>, Logable {

  private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

  private T t;
  private Map<Long, String> names = new HashMap<>();

  @SuppressWarnings("unchecked")
  public UnsafePropertyNameGetter(Class<T> clz) throws InstantiationException {
    t = uncheck(() -> (T) UNSAFE.allocateInstance(clz));
    Field[] fields = ReflectUtil.getAllFields(clz, false);
    for (Field field : fields) {
      long offset = UNSAFE.objectFieldOffset(field);
      Class<?> type = field.getType();
      if (PrimitiveTypeUtil.isPrimitive(type)) {
        if (PrimitiveTypeUtil.sizeOf(type) >= 4) {

        } else {
          log().warn("Can't generate {}'s name getter, since its a primitive type with size less than 4 bytes.", field);
        }
      } else {
        Object ret = UNSAFE.allocateInstance(type);
        long add = UnsafeUtil.addressOf(ret);
        UNSAFE.putObject(t, offset, ret);
        names.put(add, field.getName());
      }
    }
  }

  @Override
  public String getName(Object object) {
    long add = UnsafeUtil.addressOf(object);
    return names.get(add);
  }

  @Override
  public T get() {
    return t;
  }

  @Override
  public boolean canHandleField() {
    return true;
  }
}
