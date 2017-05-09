package org.dnacronym.hygene.ui.runnable;


import org.dnacronym.hygene.ui.UITest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

/**
 * Unit tests for the {@link DNADialogue} class.
 */
public class DNADialogueTest extends UITest {
    private Exception exception;


    @Override
    public final void beforeEach() {
        exception = new UIInitialisationException("test exception");
    }


    @Test
    public final void testNullException() {
        Throwable e = catchThrowable(() ->
                DNADialogue.getInstance().show(DNADialogue.HygeneDialogueType.ERROR, null, true));

        assertThat(e).isNull();
    }

    @Test
    public final void testShowErrorShowStackTrace() {
        Throwable e = catchThrowable(() ->
                DNADialogue.getInstance().show(DNADialogue.HygeneDialogueType.ERROR, exception, true));

        assertThat(e).isNull();
    }

    @Test
    public final void testShowWarningShowStackTrace() {
        Throwable e = catchThrowable(() ->
                DNADialogue.getInstance().show(DNADialogue.HygeneDialogueType.WARNING, exception, true));

        assertThat(e).isNull();
    }

    @Test
    public final void testShowError() {
        Throwable e = catchThrowable(() ->
                DNADialogue.getInstance().show(DNADialogue.HygeneDialogueType.ERROR, exception, false));

        assertThat(e).isNull();
    }

    @Test
    public final void testShowWarning() {
        Throwable e = catchThrowable(() ->
                DNADialogue.getInstance().show(DNADialogue.HygeneDialogueType.WARNING, exception, false));

        assertThat(e).isNull();
    }
}
