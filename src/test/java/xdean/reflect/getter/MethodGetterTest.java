package xdean.reflect.getter;

import static org.junit.Assert.*;

import org.junit.Test;

import lombok.Getter;
import lombok.Setter;

public class MethodGetterTest {
  @Test
  public void test() throws Exception {
    MethodPropGetter<A> mg = ReflectWithoutString.methodGetter(A.class);
    assertEquals("getA", mg.getMethodName(a -> a.getA()));
    assertEquals("getB", mg.getMethodName(a -> a.getB()));
    assertEquals("getC", mg.getMethodName(a -> a.getC()));
    assertEquals("init", mg.getMethodName(a -> a.init()));
    assertEquals("func", mg.getMethodName(a -> a.func(null)));

    assertEquals(int.class, mg.getMethodType(a -> a.getA()));
    assertEquals(String.class, mg.getMethodType(a -> a.getB()));
    assertEquals(Object[].class, mg.getMethodType(a -> a.getC()));
    assertEquals(void.class, mg.getMethodType(a -> a.init()));
    assertEquals(A.class, mg.getMethodType(a -> a.func(null)));
  }

  @Getter
  @Setter
  public static abstract class A {
    int a;
    String b;
    Object[] c;

    void init() {
    }

    abstract A func(A a);
  }
}
