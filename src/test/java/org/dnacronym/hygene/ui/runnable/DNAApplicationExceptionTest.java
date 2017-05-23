package org.dnacronym.hygene.ui.runnable;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;


/**
 * Tests to test that an {@link Hygene} throw exceptions when not initialized by the JavaFX framework.
 */
public final class DNAApplicationExceptionTest {
    @Test
    public void testGetInstance() {
        Hygene.setInstance(null);

        final Throwable e = catchThrowable(Hygene::getInstance);

        assertThat(e).isInstanceOf(UIInitialisationException.class);
    }
}
