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
 * Unit tests of {@link ProgressBarMainStatus}.
 */
final class ProgressBarMainStatusTest {
    private ProgressBarMainStatus progressBarMainStatus;


    @BeforeEach
    void beforeEach() {
        progressBarMainStatus = new ProgressBarMainStatus();
    }


    @Test
    void testInitialProgress() {
        assertThat(progressBarMainStatus.getProgressProperty().get()).isEqualTo(0.0);
    }

    @Test
    void testInitialStatus() {
        assertThat(progressBarMainStatus.getStatusProperty().get()).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testTaskProgress() {
        final Consumer<ProgressUpdater> consumer = mock(Consumer.class);

        progressBarMainStatus.monitorTask(consumer);

        verify(consumer).accept(any());
    }
}
