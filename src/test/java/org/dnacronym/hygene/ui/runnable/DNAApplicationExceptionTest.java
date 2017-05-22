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
}
