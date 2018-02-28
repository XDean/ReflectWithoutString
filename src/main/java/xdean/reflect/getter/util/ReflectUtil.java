package xdean.reflect.getter.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectUtil {

  /**
   * Get field value by name
   *
   * @param clz
   * @param t
   * @param fieldName
   * @return
   * @throws NoSuchFieldException
   */
  @SuppressWarnings("unchecked")
  public static <T, O> O getFieldValue(Class<T> clz, T t, String fieldName) throws NoSuchFieldException {
    Field field = clz.getDeclaredField(fieldName);
    field.setAccessible(true);
    try {
      return (O) field.get(t);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Get all fields
   *
   * @param clz
   * @param includeStatic include static fields or not
   * @return
   */
  public static Field[] getAllFields(Class<?> clz, boolean includeStatic) {
    return Arrays.stream(getAllFields(clz))
        .filter(f -> includeStatic || !Modifier.isStatic(f.getModifiers()))
        .toArray(Field[]::new);
  }

  /**
   * Get all fields
   *
   * @param clz
   * @return
   */
  public static Field[] getAllFields(Class<?> clz) {
    List<Field> list = new ArrayList<>();
    do {
      list.addAll(Arrays.asList(clz.getDeclaredFields()));
    } while ((clz = clz.getSuperclass()) != null);
    return list.toArray(new Field[list.size()]);
  }
}
