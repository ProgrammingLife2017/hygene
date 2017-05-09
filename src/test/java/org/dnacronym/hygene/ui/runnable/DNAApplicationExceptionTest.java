package org.dnacronym.hygene.ui.runnable;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

/**
 * Tests to test that an {@link DNAApplication} throw exceptions when not initialized by the JavaFX framework.
 */
public class DNAApplicationExceptionTest {
    @Test
    public final void testGetInstance() {
        DNAApplication.setInstance(null);
        Throwable e = catchThrowable(DNAApplication::getInstance);

        assertThat(e).isInstanceOf(UIInitialisationException.class);
    }

    @Test
    public final void testGetGraphStore() {
        DNAApplication.setInstance(new DNAApplication());
        Throwable e = catchThrowable(() -> DNAApplication.getInstance().getGraphStore());

        assertThat(e).isInstanceOf(UIInitialisationException.class);
    }

    @Test
    public final void testGetPrimaryStage() {
        DNAApplication.setInstance(new DNAApplication());
        Throwable e = catchThrowable(() -> DNAApplication.getInstance().getGraphStore());

        assertThat(e).isInstanceOf(UIInitialisationException.class);
    }
}
