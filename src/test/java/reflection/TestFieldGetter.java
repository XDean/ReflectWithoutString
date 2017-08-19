package reflection;

import static org.junit.Assert.assertEquals;
import static xdean.jex.util.task.TaskUtil.uncheck;

import java.util.AbstractList;

import lombok.Getter;
import net.sf.cglib.core.DebuggingClassWriter;

import org.junit.BeforeClass;
import org.junit.Test;

import xdean.reflection.getter.PropertyGetter;
import xdean.reflection.getter.impl.ProxyMethodGetter;
import xdean.reflection.getter.impl.UnsafeFieldGetter;

public class TestFieldGetter {
  @SuppressWarnings("unchecked")
  PngGetter unsafePg = UnsafeFieldGetter::new;

  @SuppressWarnings("unchecked")
  PngGetter proxyPg = ProxyMethodGetter::new;

  @BeforeClass
  public static void setup() {
    System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\coding\\java\\cglib\\classFile");
  }

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

  @Test
  public void testProxy() {
    testPropertyNameGetter(proxyPg);
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
    PropertyGetter<AccessLevel> png = pg.get(AccessLevel.class);
    assertEquals("pub", png.getName(a -> a.getPub()));
    assertEquals("pro", png.getName(a -> a.getPro()));
    assertEquals("fri", png.getName(a -> a.getFri()));
    assertEquals("pri", png.getName(a -> a.getPri()));
  }

  private void testExtend(PngGetter pg) {
    PropertyGetter<Parent> ppng = pg.get(Parent.class);
    assertEquals("parent", ppng.getName(a -> a.getParent()));

    PropertyGetter<Son> spng = pg.get(Son.class);
    assertEquals("parent", spng.getName(a -> a.getParent()));
    assertEquals("son", spng.getName(a -> a.getSon()));
  }

  private void testPrimitive(PngGetter pg) {
    PropertyGetter<Primitive> png = pg.get(Primitive.class);
    assertEquals("b", png.getName(a -> a.getB()));
    assertEquals("c", png.getName(a -> a.getC()));
    assertEquals("bool", png.getName(a -> a.isBool()));
    assertEquals("i", png.getName(a -> a.getI()));
    assertEquals("s", png.getName(a -> a.getS()));
    assertEquals("l", png.getName(a -> a.getL()));
    assertEquals("f", png.getName(a -> a.getF()));
    assertEquals("d", png.getName(a -> a.getD()));

    assertEquals(byte.class, png.getType(a -> a.getB()));
    assertEquals(char.class, png.getType(a -> a.getC()));
    assertEquals(boolean.class, png.getType(a -> a.isBool()));
    assertEquals(int.class, png.getType(a -> a.getI()));
    assertEquals(short.class, png.getType(a -> a.getS()));
    assertEquals(long.class, png.getType(a -> a.getL()));
    assertEquals(float.class, png.getType(a -> a.getF()));
    assertEquals(double.class, png.getType(a -> a.getD()));
  }

  private void testArray(PngGetter pg) {
    PropertyGetter<Array> png = pg.get(Array.class);
    assertEquals("is", png.getName(a -> a.getIs()));
    assertEquals("os", png.getName(a -> a.getOs()));

    assertEquals(int[].class, png.getType(a -> a.getIs()));
    assertEquals(Object[].class, png.getType(a -> a.getOs()));
  }

  private void testClass(PngGetter pg) {
    PropertyGetter<Klass> png = pg.get(Klass.class);
    assertEquals("klass", png.getName(a -> a.getKlass()));

    assertEquals(Class.class, png.getType(a -> a.getKlass()));
  }

  private void testAbs(PngGetter pg) {
    PropertyGetter<Abs> png = pg.get(Abs.class);
    assertEquals("absList", png.getName(a -> a.getAbsList()));
    assertEquals("clone", png.getName(a -> a.getClone()));

    assertEquals(AbstractList.class, png.getType(a -> a.getAbsList()));
    assertEquals(Cloneable.class, png.getType(a -> a.getClone()));
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
    <T> PropertyGetter<T> getSafe(Class<T> t) throws Exception;

    default <T> PropertyGetter<T> get(Class<T> t) {
      return uncheck(() -> getSafe(t));
    }
  }
}
