package org.dnacronym.hygene.ui.runnable;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

/**
 * Tests to test that an {@link Hygene} throw exceptions when not initialized by the JavaFX framework.
 */
public class DNAApplicationExceptionTest {
    @Test
    public final void testGetInstance() {
        Hygene.setInstance(null);
        Throwable e = catchThrowable(Hygene::getInstance);

        assertThat(e).isInstanceOf(UIInitialisationException.class);
    }

    @Test
    public final void testGetGraphStore() {
        Hygene.setInstance(new Hygene());
        Throwable e = catchThrowable(() -> Hygene.getInstance().getGraphStore());

        assertThat(e).isInstanceOf(UIInitialisationException.class);
    }

    @Test
    public final void testGetPrimaryStage() {
        Hygene.setInstance(new Hygene());
        Throwable e = catchThrowable(() -> Hygene.getInstance().getGraphStore());

        assertThat(e).isInstanceOf(UIInitialisationException.class);
    }
}
