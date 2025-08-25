package mockito.clone.mock;

import java.util.List;

import static mockito.clone.mock.DispatchHandlerResult.Dummy;
import static mockito.clone.mock.DispatchHandlerResult.Pass;

public class VerificationHandler extends DispatchHandler implements IVerifiable{
    private boolean verifying = false;
    private VerificationContext verificationContext;

    @Override
    public void startVerifying(VerificationContext context) {
        verifying = true;
        verificationContext = context;
    }
    @Override
    public void reset() {
        verifying = false;
        verificationContext = null;
    }

    @Override
    public DispatchHandlerResult handle(MethodInvocation invocation) {
        if (!verifying) return Pass();
        verifying = false;
        getInvocationHistory().redactLastInvocation(invocation.object);

        List<MethodInvocation> invocations = getInvocationHistory().getInvocationsOfMethod(invocation.object, invocation.method);

        int count = 0;
        for (MethodInvocation inv : invocations)
            if (ArgumentMatcher.allMatch(invocation.matchers, inv.args)) ++count;

        if (verificationContext.callCount.accepts(count))
            return Dummy();
        throw new RuntimeException("VERIFICATION FAILED FOR "+ invocation.method.getName());
    }

}
