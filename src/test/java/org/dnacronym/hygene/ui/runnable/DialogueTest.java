package org.dnacronym.hygene.ui.runnable;

import org.dnacronym.hygene.ui.UITest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;


/**
 * Unit tests for the {@link Dialogue} class.
 * <p>
 * Due to the nature of ui {@link Dialogue}, this class consists mostly of black box tests.
 */
public class DialogueTest extends UITest {
    private Exception exception;


    @Override
    public final void beforeEach() {
        exception = new UIInitialisationException("test exception");
    }


    @Test
    public final void testNoNullPointerException() {
        Throwable e = catchThrowable(() -> (new Dialogue(Dialogue.DialogueType.ERROR, exception)).show());

        assertThat(e).isNull();
    }

    @Test
    public final void testShowError() {
        Throwable e = catchThrowable(() -> (new Dialogue(Dialogue.DialogueType.ERROR, exception)).show());

        assertThat(e).isNull();
    }

    @Test
    public final void testShowWarning() {
        Throwable e = catchThrowable(() -> (new Dialogue(Dialogue.DialogueType.WARNING, exception)).show());

        assertThat(e).isNull();
    }
}
