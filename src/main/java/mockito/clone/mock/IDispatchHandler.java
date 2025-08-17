package mockito.clone.mock;

public interface IDispatchHandler {
    void setInvocationHistory(IInvocationHistory history);
    DispatchHandlerResult handle(MethodInvocation invocation) throws Throwable;
    void reset();
}
