import mockito.clone.Collaborator;
import mockito.clone.UnderTest;
import mockito.clone.mock.ArgumentCaptor;
import mockito.clone.mock.InOrder;

import static mockito.clone.mock.ArgumentMatcher.*;
import static mockito.clone.mock.CallCount.atLeastOnce;
import static mockito.clone.mock.CallCount.times;
import static mockito.clone.mock.EffectfulStubBuilder.when;
import static mockito.clone.mock.EffectlessStubBuilder.doReturn;
import static mockito.clone.mock.MockFactory.*;
import static mockito.clone.mock.VerificationContextFactory.verify;

public class MockTest {
    public static void main(String[] args) {
        canSetReturn();
        argumentMatchers();
        argumentCaptors();
        thenThrow();
        resetWorks();
        simpleVerify();
        simpleSpy();
        returnChaining();
        effectlessStubbing();
        orderedVerification();
    }

    static private void canSetReturn() {
        Collaborator collaborator = mock(Collaborator.class);
        when(collaborator.getNumberFromSeed(1)).thenReturn(1);
        when(collaborator.getNumberFromSeed(2)).thenReturn(1);

        UnderTest underTest = new UnderTest(collaborator);
        int output = underTest.doTheThing(1,2);

        Assert.equal(output, 2);
    }

    private static void argumentMatchers() {
        Collaborator collaborator = mock(Collaborator.class);
        when(collaborator.getNumberFromSeed(anyInt())).thenReturn(1);

        UnderTest underTest = new UnderTest(collaborator);

        int output = underTest.doTheThing(1,2);
        Assert.equal(output, 2);
    }

    private static void argumentCaptors() {
        ArgumentCaptor<Integer> a = new ArgumentCaptor<>(int.class);

        Collaborator collaborator = mock(Collaborator.class);
        when(collaborator.getNumberFromSeed(a.capture())).thenReturn(2);

        UnderTest underTest = new UnderTest(collaborator);
        int output = underTest.doTheThing(1,2);

        Assert.equal(output, 4);
        Assert.equal(a.get(0), 1);
        Assert.equal(a.get(1), 2);
    }

    public static void thenThrow() {
        String expMessage = "Hello";
        Collaborator collaborator = mock(Collaborator.class);
        when(collaborator.getNumberFromSeed(anyInt())).thenThrow(new RuntimeException(expMessage));

        UnderTest underTest = new UnderTest(collaborator);

        try {
            underTest.doTheThing(1,2);
        }catch (RuntimeException e) {
            Assert.equal(e.getMessage(), expMessage);
            return;
        }

        throw new AssertionError("Exception not thrown");
    }

    public static void resetWorks() {
        Collaborator collaborator = mock(Collaborator.class);
        when(collaborator.getNumberFromSeed(anyInt())).thenReturn(1);

        UnderTest underTest = new UnderTest(collaborator);
        int output = underTest.doTheThing(1,2);

        Assert.equal(output, 2);

        reset(collaborator);
        when(collaborator.getNumberFromSeed(anyInt())).thenReturn(2);

        output = underTest.doTheThing(1,2);
        Assert.equal(output, 4);
    }

    public static void simpleVerify() {
        Collaborator collaborator = mock(Collaborator.class);
        when(collaborator.getNumberFromSeed(anyInt())).thenReturn(1);

        UnderTest underTest = new UnderTest(collaborator);

        int output = underTest.doTheThing(1,2);

        verify(collaborator, atLeastOnce()).getNumberFromSeed(1);
        Assert.equal(output, 2);
    }

    public static void  simpleSpy() {
        Collaborator collaboratorSpy = spy(new Collaborator());
        when(collaboratorSpy.getNumberFromSeed(1)).thenReturn(1);

        UnderTest underTest = new UnderTest(collaboratorSpy);

        int output = underTest.doTheThing(1,2);

        verify(collaboratorSpy, times(2)).sideEffect(anyInt());
        Assert.equal(output, 5);
    }

    public static void returnChaining() {
        Collaborator collaborator = mock(Collaborator.class);
        when(collaborator.getNumberFromSeed(anyInt()))
                .thenReturn(1)
                .thenReturn(2);

        UnderTest underTest = new UnderTest(collaborator);

        int output = underTest.doTheThing(1,2);

        verify(collaborator, atLeastOnce()).getNumberFromSeed(1);
        verify(collaborator, atLeastOnce()).getNumberFromSeed(2);
        Assert.equal(output, 3);

    }

    public static void  effectlessStubbing() {
        Collaborator collaboratorSpy = spy(new Collaborator());
        doReturn(1)
                .thenReturn(2)
                .when(collaboratorSpy).getNumberFromSeed(anyInt());

        UnderTest underTest = new UnderTest(collaboratorSpy);

        int output = underTest.doTheThing(1,2);

        verify(collaboratorSpy, times(2)).sideEffect(anyInt());
        Assert.equal(output, 3);
    }

    public static void orderedVerification() {
        Collaborator collaborator = mock(Collaborator.class);
        when(collaborator.getNumberFromSeed(anyInt()))
                .thenReturn(3)
                .thenReturn(4);

        UnderTest underTest = new UnderTest(collaborator);

        int output = underTest.doTheThing(1,2);
        InOrder ordered = new InOrder();

        ordered.verify(collaborator, atLeastOnce()).getNumberFromSeed(1);
        ordered.verify(collaborator, atLeastOnce()).getNumberFromSeed(2);
        ordered.verify(collaborator, times(1)).sideEffect(3);
        ordered.verify(collaborator, times(1)).sideEffect(4);
        Assert.equal(output, 7);
    }
}