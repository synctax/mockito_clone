package mockito.clone.mock;

import java.lang.reflect.Method;
import java.util.List;

public interface IInvocationHistory {

    void recordInvocation(MethodInvocation invocation);
    void redactLastInvocation(Object object);
    void redactAllObjectInvocations(Object object);

    // index -1 -> last recorded invocation
    List<MethodInvocation> getInvocationsOfMethodInRange(int start, int end, Object object, Method method);
    List<MethodInvocation> getInvocationsOfMethod(Object object, Method method);
}
