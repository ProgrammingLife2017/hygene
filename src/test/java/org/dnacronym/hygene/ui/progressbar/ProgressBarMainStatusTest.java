package org.dnacronym.hygene.ui.progressbar;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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
        final Task<Void> task = mock(Task.class);
        when(task.progressProperty()).thenReturn(new SimpleDoubleProperty(0.6));

        progressBarMainStatus.activateProgressBar(task);

        assertThat(progressBarMainStatus.getProgressProperty().get()).isEqualTo(0.6);
    }

    @Test
    void testTaskStatus() {
        progressBarMainStatus.updateProgressText("test");

        assertThat(progressBarMainStatus.getStatusProperty().get()).isEqualTo("test");
    }
}
