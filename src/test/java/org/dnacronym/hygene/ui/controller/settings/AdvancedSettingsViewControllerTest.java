package org.dnacronym.hygene.ui.controller.settings;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckBox;
import org.dnacronym.hygene.ui.UITest;
import org.dnacronym.hygene.ui.store.Settings;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        SimpleBooleanProperty displayLaneBorder = new SimpleBooleanProperty();
        displayLaneBorder.setValue(false);
        when(graphVisualizer.getDisplayBordersProperty()).thenReturn(displayLaneBorder);

        settings = mock(Settings.class);
        advancedSettingsViewController.setGraphVisualizer(graphVisualizer);
        advancedSettingsViewController.setSettings(settings);
        CheckBox cb = new CheckBox();
        cb.setSelected(true);
        advancedSettingsViewController.setDisplayLaneBorders(cb);
    }


    @Test
    void testShowBorders() {
        interact(() -> advancedSettingsViewController.showLaneBordersClicked());
        verify(settings, times(1)).addRunnable(any(Runnable.class));
    }

    @Test
    void testInitDisplayLaneBorders() {
        advancedSettingsViewController.initialize(null, null);
        assertThat(advancedSettingsViewController.getDisplayLaneBorders().isSelected()).isFalse();
    }
}
