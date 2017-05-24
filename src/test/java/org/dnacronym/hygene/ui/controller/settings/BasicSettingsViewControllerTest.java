package org.dnacronym.hygene.ui.controller.settings;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
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
        SimpleDoubleProperty height = new SimpleDoubleProperty();
        height.setValue(20);
        when(graphVisualizer.getNodeHeightProperty()).thenReturn(height);

        ObjectProperty<Color> color = new SimpleObjectProperty<>();
        color.set(Color.RED);
        when(graphVisualizer.getEdgeColorProperty()).thenReturn(color);

        settings = mock(Settings.class);
        basicSettingsViewController.setGraphVisualizer(graphVisualizer);
        basicSettingsViewController.setSettings(settings);
        basicSettingsViewController.setNodeHeight(new Slider());
        basicSettingsViewController.setEdgeColor(new ColorPicker());
    }


    @Test
    void testNodeHeightSliderDone() {
        interact(() -> basicSettingsViewController.nodeHeightSliderDone());
        verify(settings, times(1)).addRunnable(any(Runnable.class));
    }

    @Test
    void testEdgeColorDone() {
        interact(() -> basicSettingsViewController.edgeColorDone());
        verify(settings, times(1)).addRunnable(any(Runnable.class));
    }

    @Test
    void testInitializationOfNodeHeight() {
        basicSettingsViewController.initialize(null, null);
        assertThat(basicSettingsViewController.getNodeHeight().getValue()).isEqualTo(20.0);
    }

    @Test
    void testInitializationOfEdgeColor() {
        basicSettingsViewController.initialize(null, null);
        assertThat(basicSettingsViewController.getEdgeColor().getValue()).isEqualTo(Color.RED);
    }
}
