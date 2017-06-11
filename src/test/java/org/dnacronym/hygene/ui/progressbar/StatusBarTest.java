package org.dnacronym.hygene.ui.progressbar;

import org.dnacronym.hygene.parser.ProgressUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * Unit tests of {@link StatusBar}.
 */
final class StatusBarTest {
    private StatusBar statusBar;


    @BeforeEach
    void beforeEach() {
        statusBar = new StatusBar();
    }


    @Test
    void testInitialProgress() {
        assertThat(statusBar.getProgressProperty().get()).isEqualTo(0.0);
    }

    @Test
    void testInitialStatus() {
        assertThat(statusBar.getStatusProperty().get()).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testTaskProgress() {
        final Consumer<ProgressUpdater> consumer = mock(Consumer.class);

        statusBar.monitorTask(consumer);

        verify(consumer).accept(any());
    }
}
