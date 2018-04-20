package xdean.reflect.getter.internal.util;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import sun.misc.Unsafe;

public abstract class UnsafeUtil {

  private static final Unsafe THE_UNSAFE = initUnsafe();

  public static Unsafe getUnsafe() {
    return THE_UNSAFE;
  }

  private static Unsafe initUnsafe() {
    try {
      return Unsafe.getUnsafe();
    } catch (SecurityException tryReflectionInstead) {
      try {
        return AccessController.doPrivileged(
            (PrivilegedExceptionAction<Unsafe>) () -> {
              Class<Unsafe> k = Unsafe.class;
              for (Field f : k.getDeclaredFields()) {
                f.setAccessible(true);
                Object x = f.get(null);
                if (k.isInstance(x)) {
                  return k.cast(x);
                }
              }
              throw new NoSuchFieldError("the Unsafe");
            });
      } catch (PrivilegedActionException e) {
        throw new RuntimeException("Could not initialize intrinsics", e.getCause());
      }
    }
  }
}
