package xdean.reflection;

import java.util.Optional;

import net.sf.cglib.proxy.MethodInterceptor;

public class InvocationContext {
  private static final ThreadLocal<Context> CONTEXT = new ThreadLocal<Context>() {
    @Override
    protected Context initialValue() {
      return new Context();
    };
  };

  public static Optional<Invocation> getLastInvocation() {
    return Optional.ofNullable(CONTEXT.get().getLastInvocation());
  }

  public static void putInvocation(Invocation invocation) {
    CONTEXT.get().setLastInvocation(invocation);
  }

  public static MethodInterceptor getInvocationInterceptor() {
    return (MethodInterceptor) ((obj, method, args, proxy) -> {
      putInvocation(new Invocation(obj, method, args));
      return proxy.invokeSuper(obj, args);
    });
  }

  private static class Context {
    private Invocation lastInvocation;

    public Invocation getLastInvocation() {
      return lastInvocation;
    }

    public void setLastInvocation(Invocation lastInvocation) {
      this.lastInvocation = lastInvocation;
    }
  }
}
