package org.dnacronym.hygene.ui.runnable;

import org.dnacronym.hygene.ui.AbstractUITest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


/**
 * Unit tests for {@link Hygene}.
 */
final class HygeneTest extends AbstractUITest {
    @Test
    void testGetApplication() throws UIInitialisationException {
        assertThat(Hygene.getInstance()).isEqualTo(getApplication());
    }

    @Test
    void testGetPrimaryStage() throws UIInitialisationException {
        assertThat(Hygene.getInstance().getPrimaryStage()).isEqualTo(getPrimaryStage());
    }

    @Test
    void testGetGraphStore() throws UIInitialisationException {
        assertThat(Hygene.getInstance().getGraphStore()).isNotNull();
    }

    @Test
    void testGetSimpleBookmarkStore() throws UIInitialisationException {
        assertThat(Hygene.getInstance().getSimpleBookmarkStore()).isNotNull();
    }
}
