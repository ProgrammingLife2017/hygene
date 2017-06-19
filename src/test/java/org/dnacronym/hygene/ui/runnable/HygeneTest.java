package org.dnacronym.hygene.ui.runnable;

import org.dnacronym.hygene.ui.UITestBase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


/**
 * Unit tests for {@link Hygene}.
 */
final class HygeneTest extends UITestBase {
    @Test
    void testGetApplication() throws UIInitialisationException {
        assertThat(Hygene.getInstance()).isEqualTo(getApplication());
    }

    @Test
    void testGetPrimaryStage() throws UIInitialisationException {
        assertThat(Hygene.getInstance().getPrimaryStage()).isEqualTo(getPrimaryStage());
    }

}
