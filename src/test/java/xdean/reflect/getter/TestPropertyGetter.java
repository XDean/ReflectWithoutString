package xdean.reflect.getter;

import static org.junit.Assert.assertEquals;
import static xdean.reflect.getter.internal.util.ExceptionUtil.uncheck;

import java.util.AbstractList;

import org.junit.Ignore;
import org.junit.Test;

import lombok.Getter;
import lombok.Setter;
import xdean.reflect.getter.impl.ProxyMethodGetter;
import xdean.reflect.getter.impl.UnsafeFieldGetter;

public class TestPropertyGetter {
  PngGetter fieldPg = UnsafeFieldGetter::new;

  PngGetter methodPg = ProxyMethodGetter::new;

  /********************** Field *********************/
  @Test
  public void testField() {
    testPropertyNameGetter(fieldPg);
  }

  @Ignore("Enable it to test efficiency")
  @Test
  public void testFieldConstructTime() {
    testConstructTime(fieldPg, 1_000_000);
  }

  @Ignore("Enable it to test efficiency")
  @Test(timeout = 1000)
  public void testFieldInvokeTime() {
    testInvokeTime(fieldPg, 1_000_000);
  }

  @Test
  public void testFieldBooleanOverflow() {
    FieldPropGetter<ManyPrimitive> actual = (FieldPropGetter<ManyPrimitive>) fieldPg.get(ManyPrimitive.class);
    assertEquals("b1", actual.getPropName(t -> t.b1));
    assertEquals("b2", actual.getPropName(t -> t.b2));
    assertEquals("b3", actual.getPropName(t -> t.b3));
    assertEquals("c1", actual.getPropName(t -> t.c1));
    assertEquals("d1", actual.getPropName(t -> t.d1));
    assertEquals("f1", actual.getPropName(t -> t.f1));
    assertEquals("i1", actual.getPropName(t -> t.i1));
    assertEquals("s1", actual.getPropName(t -> t.s1));
    assertEquals("l1", actual.getPropName(t -> t.l1));
    assertEquals("bt1", actual.getPropName(t -> t.bt1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInterface() {
    fieldPg.get(Inter.class);
  }

  /********************* Method *********************/
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

  @Test
  public void testSetter() {
    MethodPropGetter<Primitive> mg = new ProxyMethodGetter<>(Primitive.class);
    assertEquals("b", mg.getPropName(Primitive::setB));
    assertEquals("c", mg.getPropName(Primitive::setC));
    assertEquals("d", mg.getPropName(Primitive::setD));
    assertEquals("f", mg.getPropName(Primitive::setF));
    assertEquals("i", mg.getPropName(Primitive::setI));
    assertEquals("l", mg.getPropName(Primitive::setL));
    assertEquals("o", mg.getPropName(Primitive::setO));
    assertEquals("s", mg.getPropName(Primitive::setS));
    assertEquals("bool", mg.getPropName(Primitive::setBool));
  }

  /********************* Test ************************/
  private void testConstructTime(PngGetter pg, long times) {
    for (int i = 0; i < times; i++) {
      pg.get(Object.class);
    }
  }

  private void testInvokeTime(PngGetter pg, long times) {
    PropertyGetter<ForTime> png = pg.get(ForTime.class);
    for (int i = 0; i < times; i++) {
      png.getPropName(ForTime::getO);
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
    assertEquals("pub", png.getPropName(a -> a.getPub()));
    assertEquals("pro", png.getPropName(a -> a.getPro()));
    assertEquals("fri", png.getPropName(a -> a.getFri()));
    assertEquals("pri", png.getPropName(a -> a.getPri()));
  }

  private void testExtend(PngGetter pg) {
    PropertyGetter<Parent> ppng = pg.get(Parent.class);
    assertEquals("parent", ppng.getPropName(a -> a.getParent()));

    PropertyGetter<Son> spng = pg.get(Son.class);
    assertEquals("parent", spng.getPropName(a -> a.getParent()));
    assertEquals("son", spng.getPropName(a -> a.getSon()));
  }

  private void testPrimitive(PngGetter pg) {
    PropertyGetter<Primitive> png = pg.get(Primitive.class);
    assertEquals("b", png.getPropName(a -> a.getB()));
    assertEquals("c", png.getPropName(a -> a.getC()));
    assertEquals("bool", png.getPropName(a -> a.isBool()));
    assertEquals("i", png.getPropName(a -> a.getI()));
    assertEquals("s", png.getPropName(a -> a.getS()));
    assertEquals("l", png.getPropName(a -> a.getL()));
    assertEquals("f", png.getPropName(a -> a.getF()));
    assertEquals("d", png.getPropName(a -> a.getD()));

    assertEquals(byte.class, png.getPropType(a -> a.getB()));
    assertEquals(char.class, png.getPropType(a -> a.getC()));
    assertEquals(boolean.class, png.getPropType(a -> a.isBool()));
    assertEquals(int.class, png.getPropType(a -> a.getI()));
    assertEquals(short.class, png.getPropType(a -> a.getS()));
    assertEquals(long.class, png.getPropType(a -> a.getL()));
    assertEquals(float.class, png.getPropType(a -> a.getF()));
    assertEquals(double.class, png.getPropType(a -> a.getD()));
  }

  private void testArray(PngGetter pg) {
    PropertyGetter<Array> png = pg.get(Array.class);
    assertEquals("is", png.getPropName(a -> a.getIs()));
    assertEquals("os", png.getPropName(a -> a.getOs()));

    assertEquals(int[].class, png.getPropType(a -> a.getIs()));
    assertEquals(Object[].class, png.getPropType(a -> a.getOs()));
  }

  private void testClass(PngGetter pg) {
    PropertyGetter<Klass> png = pg.get(Klass.class);
    assertEquals("klass", png.getPropName(a -> a.getKlass()));
    assertEquals(Class.class, png.getPropType(a -> a.getKlass()));
  }

  private void testAbsClass(PngGetter pg) {
    PropertyGetter<AbsClass> png = pg.get(AbsClass.class);
    assertEquals("o", png.getPropName(a -> a.getO()));
    assertEquals("i", png.getPropName(a -> a.getI()));

    assertEquals(Object.class, png.getPropType(a -> a.getO()));
    assertEquals(int.class, png.getPropType(a -> a.getI()));

  }

  private void testAbsField(PngGetter pg) {
    PropertyGetter<AbsField> png = pg.get(AbsField.class);
    assertEquals("absList", png.getPropName(a -> a.getAbsList()));
    assertEquals("clone", png.getPropName(a -> a.getClone()));

    assertEquals(AbstractList.class, png.getPropType(a -> a.getAbsList()));
    assertEquals(Cloneable.class, png.getPropType(a -> a.getClone()));
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
  @Setter
  static class Primitive {
    byte b;
    char c;
    boolean bool;
    int i;
    short s;
    long l;
    double d;
    float f;
    Object o;
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

  static class ManyPrimitive {
    boolean b1, b2, b3, b4;
    int i1, i2;
    float f1, f2;
    long l1, l2;
    byte bt1, bt2;
    double d1, d2;
    short s1, s2;
    char c1, c2;
  }

  @FunctionalInterface
  static interface PngGetter {
    <T> PropertyGetter<T> getSafe(Class<T> t) throws Exception;

    default <T> PropertyGetter<T> get(Class<T> t) {
      return uncheck(() -> getSafe(t));
    }
  }
}
