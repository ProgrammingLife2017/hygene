package org.dnacronym.hygene.ui.setting;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import org.dnacronym.hygene.ui.UITest;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
    private CheckBox checkBox;
    private ActionEvent mouseEvent;


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

        checkBox = new CheckBox();
        checkBox.setSelected(true);

        mouseEvent = mock(ActionEvent.class);
    }


    @Test
    void testShowBorders() {
        when(mouseEvent.getSource()).thenReturn(checkBox);
        interact(() -> advancedSettingsViewController.showLaneBordersClicked(mouseEvent));
        verify(settings, times(1)).addRunnable(any(Runnable.class));
    }

    @Test
    void testShowBorderRunnable() {
        assertThat(checkBox.isSelected()).isTrue();
        assertThat(graphVisualizer.getDisplayBordersProperty().getValue()).isFalse();

        when(mouseEvent.getSource()).thenReturn(checkBox);
        interact(() -> advancedSettingsViewController.showLaneBordersClicked(mouseEvent));

        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(settings).addRunnable(captor.capture());
        Runnable command = captor.getValue();
        command.run();

        assertThat(graphVisualizer.getDisplayBordersProperty().getValue()).isTrue();
    }
}
