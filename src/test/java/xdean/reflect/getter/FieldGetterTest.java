package xdean.reflect.getter;

import static org.junit.Assert.*;

import org.junit.Test;

import lombok.Getter;

public class FieldGetterTest {
  @Test
  public void testGetFeild() throws Exception {
    FieldPropGetter<A> fg = ReflectWithoutString.fieldGetter(A.class);
    assertEquals("a", fg.getFieldName(a -> a.a));
    assertEquals("b", fg.getFieldName(a -> a.b));
    assertEquals("c", fg.getFieldName(a -> a.c));
    assertEquals(int.class, fg.getFieldType(a -> a.a));
    assertEquals(String.class, fg.getFieldType(a -> a.b));
    assertEquals(Object[].class, fg.getFieldType(a -> a.c));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetterNameFail() throws Exception {
    ReflectWithoutString.fieldGetter(A.class).<Integer> getPropName((a, o) -> a.a = o);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetterTypeFail() throws Exception {
    ReflectWithoutString.fieldGetter(A.class).<Integer> getPropType((a, o) -> a.a = o);
  }

  @Getter
  public static class A {
    int a;
    String b;
    Object[] c;
  }
}
