package org.dnacronym.hygene.ui.controller.settings;

import javafx.event.ActionEvent;
import org.dnacronym.hygene.ui.UITest;
import org.dnacronym.hygene.ui.store.Settings;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for {@link LoggingSettingsViewController}s.
 */
final class LoggingSettingsViewControllerTest extends UITest {
    private LoggingSettingsViewController loggingSettingsViewController;
    private GraphVisualizer graphVisualizer;
    private Settings settings;


    @Override
    public void beforeEach() {
        loggingSettingsViewController = new LoggingSettingsViewController();

        graphVisualizer = mock(GraphVisualizer.class);
        settings = mock(Settings.class);
        loggingSettingsViewController.setGraphVisualizer(graphVisualizer);
        loggingSettingsViewController.setSettings(settings);
    }

    @Test
    void testChangeLogLevelEvent() {
        ActionEvent event = new ActionEvent();
        interact(() -> loggingSettingsViewController.onLogLevelChanged(event));
        verify(settings, times(1)).addRunnable(any(Runnable.class));
    }

}
