package mockito.clone.mock;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mockito.clone.mock.DispatchHandlerResult.*;

public class StubHandler extends DispatchHandler implements IStubbable{
    private boolean stubbing = false;
    private IStubContext stubContext;
    private final Map<String, List<MethodStub<?>>> stubs = new HashMap<>();

    @Override
    public void startStubbing(IStubContext stubContext) {
        stubbing = true;
        this.stubContext = stubContext;
    }

    @Override
    public void addStub(Method method, MethodStub<?> handler) {
        List<MethodStub<?>> methodEntry = stubs.get(method.getName());
        if (methodEntry == null){
            methodEntry = new ArrayList<>();
            stubs.put(method.getName(), methodEntry);
        }

        methodEntry.add(handler);
    }

    @Override
    public DispatchHandlerResult handle(MethodInvocation invocation) throws Throwable {
        if (stubbing) {
            stubContext.registerInvocation(invocation);
            getInvocationHistory().redactLastInvocation(this);
            stubbing = false;
            return Dummy();
        }

        EffectfulStubBuilder.notifyInvocation(invocation);
        MethodStub<?> handler = findMatchingHandler(invocation);
        if (handler == null) return Pass();
        return Value(handler.handle(invocation));
    }

    @Override
    public void reset() {
        stubbing = false;
        stubContext = null;
        stubs.clear();
    }

    private MethodStub<?> findMatchingHandler(MethodInvocation inv) {
        List<MethodStub<?>> methodStubs = stubs.get(inv.method.getName());
        if (methodStubs == null) return null;
        for (MethodStub<?> handler : methodStubs) {
            if (handler.shouldHandle(inv)) return handler;
        }

        return null;
    }
}
