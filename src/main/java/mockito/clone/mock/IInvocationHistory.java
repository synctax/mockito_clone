package mockito.clone.mock;

import java.util.List;

public interface IInvocationHistory {
    void redactLastInvocation();
    void redactNextInvocation();
    List<MethodInvocation> getInvocations(String method);
}
