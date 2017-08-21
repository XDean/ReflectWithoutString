package xdean.reflect.getter;

import static org.junit.Assert.assertEquals;
import static xdean.jex.util.lang.ExceptionUtil.uncheck;

import java.util.AbstractList;

import lombok.Getter;

import org.junit.Ignore;
import org.junit.Test;

import xdean.reflect.getter.PropertyGetter;
import xdean.reflect.getter.impl.FieldGetterImpl;
import xdean.reflect.getter.impl.MethodGetterImpl;

public class TestPropertyGetter {
  @SuppressWarnings("unchecked")
  PngGetter fieldPg = FieldGetterImpl::new;

  @SuppressWarnings("unchecked")
  PngGetter methodPg = MethodGetterImpl::new;

  /******************** Field **********************/
  @Test
  public void testField() {
    testPropertyNameGetter(fieldPg);
  }

  @Ignore("Enable it to test efficiency")
  @Test
  public void testFieldConstructTime() {
    testConstructTime(fieldPg, 1_000);
  }

  @Ignore("Enable it to test efficiency")
  @Test(timeout = 1000)
  public void testFieldInvokeTime() {
    testInvokeTime(fieldPg, 1_000_000);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFieldBooleanOverflow() {
    fieldPg.get(ThreeBoolean.class);
  }

  @Test
  public void testMethod() {
    testPropertyNameGetter(methodPg);
  }

  @Ignore("Enable it to test efficiency")
  @Test
  public void testMethodConstructTime() {
    testConstructTime(methodPg, 1_000);
  }

  @Ignore("Enable it to test efficiency")
  @Test(timeout = 1000)
  public void testMethodInvokeTime() {
    testInvokeTime(methodPg, 1_000_000);
  }

  private void testConstructTime(PngGetter pg, long times) {
    for (int i = 0; i < times; i++) {
      pg.get(Object.class);
    }
  }

  private void testInvokeTime(PngGetter pg, long times) {
    PropertyGetter<ForTime> png = pg.get(ForTime.class);
    for (int i = 0; i < times; i++) {
      png.getName(ForTime::getO);
    }
  }

  private void testPropertyNameGetter(PngGetter pg) {
    testAccessLevel(pg);
    testExtend(pg);
    testPrimitive(pg);
    testArray(pg);
    testClass(pg);
    testAbsClass(pg);
    testAbsField(pg);
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

  private void testAbsClass(PngGetter pg) {
    PropertyGetter<AbsClass> png = pg.get(AbsClass.class);
    assertEquals("o", png.getName(a -> a.getO()));
    assertEquals("i", png.getName(a -> a.getI()));

    assertEquals(Object.class, png.getType(a -> a.getO()));
    assertEquals(int.class, png.getType(a -> a.getI()));

  }

  private void testAbsField(PngGetter pg) {
    PropertyGetter<AbsField> png = pg.get(AbsField.class);
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

  static interface Inter {
    Object getObject();
  }

  @Getter
  static abstract class AbsClass {
    Object o;
    int i;
  }

  @Getter
  static class AbsField {
    AbstractList<?> absList;
    Cloneable clone;
  }

  @Getter
  static class ForTime {
    int i;
    Object o;
    Class<?> clz;
    AbstractList<?> al;
    Cloneable c;
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
