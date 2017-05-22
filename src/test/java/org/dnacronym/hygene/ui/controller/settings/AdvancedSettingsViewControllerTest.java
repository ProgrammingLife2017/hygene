package org.dnacronym.hygene.ui.controller.settings;


import org.dnacronym.hygene.ui.UITest;
import org.dnacronym.hygene.ui.store.Settings;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AdvancedSettingsViewController}s.
 */
final class AdvancedSettingsViewControllerTest extends UITest {
    private AdvancedSettingsViewController advancedSettingsViewController;
    private GraphVisualizer graphVisualizer;
    private Settings settings;


    @Override
    public void beforeEach() {
        advancedSettingsViewController = new AdvancedSettingsViewController();

        graphVisualizer = mock(GraphVisualizer.class);
        settings = mock(Settings.class);
        advancedSettingsViewController.setGraphVisualizer(graphVisualizer);
        advancedSettingsViewController.setSettings(settings);
    }


    @Test
    void testShowBorders() {
        interact(() -> advancedSettingsViewController.showLaneBordersClicked());
        verify(settings, times(1)).addRunnable(any(Runnable.class));
    }
}
