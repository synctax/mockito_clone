package mockito.clone.mock;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.*;

public class DispatchChainInterceptor implements IDispatchChainInterceptor{
    private final List<IDispatchHandler> handlers = new ArrayList<>();

    private final ChronologicalMethodInvocations invocations = new ChronologicalMethodInvocations();
    private boolean shouldRedactNext = false;

    @Override
    public void redactLastInvocation() {invocations.removeLast();}
    @Override
    public void redactNextInvocation() {shouldRedactNext = true;}
    @Override
    public List<MethodInvocation> getInvocations(String method) {return invocations.get(method);}

    @Override
    public <T extends IDispatchHandler> T getHandler(Class<T> type) {
        for (IDispatchHandler handler : handlers) {
            if (type.isInstance(handler)) return type.cast(handler);
        }
        return null;
    }

    @Override
    public <T extends IDispatchHandler> void addHandler(T concrete) {
        concrete.setInvocationHistory(this);
        handlers.add(concrete);
    }

    @Override
    public void reset() {
        invocations.clear();
        shouldRedactNext = false;

        for (IDispatchHandler handler : handlers) {
            handler.reset();
        }
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        ArgumentMatcher[] argumentMatchers = ArgumentMatcherCollector.popArgumentMatchers();
        if (argumentMatchers.length == 0)
            argumentMatchers = ArgumentMatcher.generateFromConcrete(args);
        else if (argumentMatchers.length < args.length)
            throw new RuntimeException("CANNOT MIX MATCHERS AND LITERALS");

        MethodInvocation invocation = new MethodInvocation.Builder()
            .args(args)
            .method(method)
            .object(obj)
            .proxy(proxy)
            .matchers(argumentMatchers)
            .build();

        if (!shouldRedactNext) invocations.add(method.getName(), invocation);
        shouldRedactNext = false;

        Object dummyReturn = DummyDataProvider.getDummyValue(method.getReturnType());
        for (IDispatchHandler handler : handlers) {
            DispatchHandlerResult result = handler.handle(invocation);
            switch (result.getType()){
                case VALUE:
                    return result.getValue();
                case DUMMY:
                    return dummyReturn;
                case PASS:
                default:
            }
        }

        return dummyReturn;
    }
}

class ChronologicalMethodInvocations {
    private final Map<String, List<MethodInvocation>> data = new HashMap<>();
    private final Stack<String> addStack = new Stack<>();

    public void add(String method, MethodInvocation invocation) {
        List<MethodInvocation> invocations = data.get(method);
        if (invocations == null) {
            invocations = new ArrayList<>();
            data.put(method, invocations);
        }

        invocations.add(invocation);
        addStack.push(method);
    }

    public List<MethodInvocation> get(String method) {
        return data.get(method);
    }

    public void removeLast() {
        if (addStack.isEmpty()) return;
        String methodToRemove = addStack.pop();

        List<?> invocations = data.get(methodToRemove);
        invocations.remove(invocations.size()-1);
    }

    public void clear() {
        data.clear();
        addStack.clear();
    }
}
