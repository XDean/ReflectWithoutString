package reflection;

import static org.junit.Assert.assertEquals;
import static xdean.jex.util.task.TaskUtil.uncheck;

import java.util.AbstractList;

import lombok.Getter;

import org.junit.Test;

import xdean.reflection.getter.FieldGetter;
import xdean.reflection.getter.impl.UnsafeFieldGetter;

public class TestFieldGetter {
  @SuppressWarnings("unchecked")
  PngGetter unsafePg = UnsafeFieldGetter::new;

  @Test
  public void testTimeUse() {
    // for (int i = 0; i < 1000000; i++) {
    // TimeUtil.seriesTimeThen(() -> new ProxyPropertyNameGetter<>(Pair.class), EmptyFunction.biconsumer());
    // }
    // ProxyPropertyNameGetter<B> png = new ProxyPropertyNameGetter<>(B.class);
    // for (int i = 0; i < 1000000; i++) {
    // TimeUtil.seriesTimeThen(() -> png.getName(B::getPrimitive), EmptyFunction.biconsumer());
    // }
  }

  @Test
  public void testUnsafe() {
    testPropertyNameGetter(unsafePg);
  }

  @Test(expected = IllegalStateException.class)
  public void testUnsafeBooleanOverflow() {
    unsafePg.get(ThreeBoolean.class);
  }

  private void testPropertyNameGetter(PngGetter pg) {
    testAccessLevel(pg);
    testExtend(pg);
    testPrimitive(pg);
    testArray(pg);
    testClass(pg);
    testAbs(pg);
  }

  private void testAccessLevel(PngGetter pg) {
    FieldGetter<AccessLevel> png = pg.get(AccessLevel.class);
    assertEquals("pub", png.getName(a -> a.pub));
    assertEquals("pro", png.getName(a -> a.pro));
    assertEquals("fri", png.getName(a -> a.fri));
    assertEquals("pri", png.getName(a -> a.pri));
  }

  private void testExtend(PngGetter pg) {
    FieldGetter<Parent> ppng = pg.get(Parent.class);
    assertEquals("parent", ppng.getName(a -> a.parent));

    FieldGetter<Son> spng = pg.get(Son.class);
    assertEquals("parent", spng.getName(a -> a.parent));
    assertEquals("son", spng.getName(a -> a.son));
  }

  private void testPrimitive(PngGetter pg) {
    FieldGetter<Primitive> png = pg.get(Primitive.class);
    assertEquals("b", png.getName(a -> a.b));
    assertEquals("c", png.getName(a -> a.c));
    assertEquals("bool", png.getName(a -> a.bool));
    assertEquals("i", png.getName(a -> a.i));
    assertEquals("s", png.getName(a -> a.s));
    assertEquals("l", png.getName(a -> a.l));
    assertEquals("f", png.getName(a -> a.f));
    assertEquals("d", png.getName(a -> a.d));

    assertEquals(byte.class, png.getType(a -> a.b));
    assertEquals(char.class, png.getType(a -> a.c));
    assertEquals(boolean.class, png.getType(a -> a.bool));
    assertEquals(int.class, png.getType(a -> a.i));
    assertEquals(short.class, png.getType(a -> a.s));
    assertEquals(long.class, png.getType(a -> a.l));
    assertEquals(float.class, png.getType(a -> a.f));
    assertEquals(double.class, png.getType(a -> a.d));
  }

  private void testArray(PngGetter pg) {
    FieldGetter<Array> png = pg.get(Array.class);
    assertEquals("is", png.getName(a -> a.is));
    assertEquals("os", png.getName(a -> a.os));

    assertEquals(int[].class, png.getType(a -> a.is));
    assertEquals(Object[].class, png.getType(a -> a.os));
  }

  private void testClass(PngGetter pg) {
    FieldGetter<Klass> png = pg.get(Klass.class);
    assertEquals("klass", png.getName(a -> a.klass));

    assertEquals(Class.class, png.getType(a -> a.klass));
  }

  private void testAbs(PngGetter pg) {
    FieldGetter<Abs> png = pg.get(Abs.class);
    assertEquals("absList", png.getName(a -> a.absList));
    assertEquals("clone", png.getName(a -> a.clone));

    assertEquals(AbstractList.class, png.getType(a -> a.absList));
    assertEquals(Cloneable.class, png.getType(a -> a.clone));
  }

  @Getter
  static class AccessLevel {
    public Object pub;
    protected Object pro;
    Object fri;
    private Object pri;

  }

  @Getter
  static class Parent {
    Object parent;
  }

  @Getter
  static class Son extends Parent {
    Object son;
  }

  @Getter
  static class Primitive {
    byte b;
    char c;
    boolean bool;
    int i;
    short s;
    long l;
    float f;
    double d;
  }

  @Getter
  static class Array {
    int[] is;
    Object[] os;
  }

  @Getter
  static class Klass {
    Class<?> klass;
  }

  @Getter
  static class Abs {
    AbstractList<?> absList;
    Cloneable clone;
  }

  static class ThreeBoolean {
    boolean a, b, c;
  }

  @FunctionalInterface
  static interface PngGetter {
    <T> FieldGetter<T> getSafe(Class<T> t) throws Exception;

    default <T> FieldGetter<T> get(Class<T> t) {
      return uncheck(() -> getSafe(t));
    }
  }
}
