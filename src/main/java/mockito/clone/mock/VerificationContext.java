package mockito.clone.mock;

public class VerificationContext {
    final CallCount callCount;

    public VerificationContext(CallCount count){
        callCount = count;
    }
}
