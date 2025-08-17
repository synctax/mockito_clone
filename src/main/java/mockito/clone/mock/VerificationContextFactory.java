package mockito.clone.mock;

public class VerificationContextFactory {
    public static <T> T verify(T mock, CallCount count) {
        VerificationContext context = new VerificationContext(count);
        IVerifiable verifiable = MockFactory.getVerifiable(mock);
        verifiable.startVerifying(context);
        return mock;
    }
}
