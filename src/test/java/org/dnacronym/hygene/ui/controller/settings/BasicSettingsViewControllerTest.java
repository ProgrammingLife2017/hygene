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
 * Unit tests for {@link BasicSettingsViewController}s.
 */
final class BasicSettingsViewControllerTest extends UITest {
    private BasicSettingsViewController basicSettingsViewController;
    private GraphVisualizer graphVisualizer;
    private Settings settings;


    @Override
    public void beforeEach() {
        basicSettingsViewController = new BasicSettingsViewController();

        graphVisualizer = mock(GraphVisualizer.class);
        settings = mock(Settings.class);
        basicSettingsViewController.setGraphVisualizer(graphVisualizer);
        basicSettingsViewController.setSettings(settings);
    }


    @Test
    void testNodeHeightSliderDone() {
        interact(() -> basicSettingsViewController.nodeHeightSliderDone());
        verify(settings, times(1)).addCallable(any(Runnable.class));
    }

    @Test
    void testEdgeColorDone() {
        interact(() -> basicSettingsViewController.edgeColorDone());
        verify(settings, times(1)).addCallable(any(Runnable.class));
    }
}
