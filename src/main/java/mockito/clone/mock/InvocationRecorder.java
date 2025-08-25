package mockito.clone.mock;

import java.lang.reflect.Method;
import java.util.*;

public class InvocationRecorder implements IInvocationHistory{
    private final LinkedList<MethodInvocation> invocations = new LinkedList<>();

    @Override
    public void recordInvocation(MethodInvocation invocation)
    {invocations.add(invocation);}

    @Override
    public void redactLastInvocation(Object object) {
        Iterator<MethodInvocation> it = invocations.descendingIterator();
        while (it.hasNext()) {
            if (it.next().object == object) {
                it.remove();
                return;
            }
        }
    }

    @Override
    public void redactAllObjectInvocations(Object object) {
        Iterator<MethodInvocation> it = invocations.iterator();
        while (it.hasNext()) {
            if (it.next().object == object) {
                it.remove();
            }
        }
    }

    @Override
    public List<MethodInvocation> getInvocationsOfMethod(Object object, Method method) {
        return getInvocationsOfMethodInRange(0, -1, object, method);
    }

    @Override
    public List<MethodInvocation> getInvocationsOfMethodInRange(int start, int end, Object object, Method method) {
        List<MethodInvocation> out = new ArrayList<>();
        int newEnd = end == -1 ? invocations.size() : end;
        for (MethodInvocation invocation : invocations.subList(start, newEnd)) {
            if (invocation.object == object && invocation.method == method)
                out.add(invocation);
        }
        return out;
    }
}
