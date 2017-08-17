package reflection;

import static org.junit.Assert.assertEquals;
import lombok.Getter;

import org.junit.Test;

import xdean.reflection.property.PropertyNameGetter;
import xdean.reflection.property.UnsafePropertyNameGetter;

public class TestPropertyNameGetter {
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
  public void test() throws InstantiationException {
    UnsafePropertyNameGetter<B> png = new UnsafePropertyNameGetter<>(B.class);
    testPropertyNameGetter(png);
  }

  private void testPropertyNameGetter(PropertyNameGetter<B> png) {
    assertEquals("parentPrivate", png.getName(A::getParentPrivate));
    assertEquals("parentPublic", png.getName(A::getParentPublic));
    assertEquals("parentPrimitive", png.getName(A::getParentPrimitive));
    assertEquals("privateObject", png.getName(B::getPrivateObject));
    assertEquals("publicObject", png.getName(B::getPublicObject));
  }

  @Getter
  static class A {
    private Object parentPrivate;
    public TestPropertyNameGetter parentPublic;
    int parentPrimitive;
  }

  @Getter
  static class B extends A {
    private Object privateObject;
    public TestPropertyNameGetter publicObject;
    int primitive;
  }

}
