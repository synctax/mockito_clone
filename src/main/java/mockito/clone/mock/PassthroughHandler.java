package mockito.clone.mock;

import static mockito.clone.mock.DispatchHandlerResult.Value;

public class PassthroughHandler<T> extends DispatchHandler{
    private final T concrete;

    public PassthroughHandler(T concrete) {
        this.concrete = concrete;
    }

    @Override
    public DispatchHandlerResult handle(MethodInvocation invocation) throws Throwable {
        return Value(invocation.method.invoke(concrete, invocation.args));
    }

    @Override
    public void reset() {}
}
