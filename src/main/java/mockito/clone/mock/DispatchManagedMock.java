package mockito.clone.mock;

public class DispatchManagedMock implements IDispatchedManagedMock{
    private final IDispatchChainInterceptor manager;

    public DispatchManagedMock(IDispatchChainInterceptor manager) {
        this.manager = manager;
    }

    @Override
    public IDispatchChainInterceptor getManager() {
        return manager;
    }
}
