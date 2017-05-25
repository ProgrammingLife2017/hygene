package org.dnacronym.hygene.ui.controller.settings;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.dnacronym.hygene.ui.UITest;
import org.dnacronym.hygene.ui.store.Settings;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
    private MouseEvent mouseEvent;
    private ActionEvent actionEvent;
    private ColorPicker colorPickerMock;
    private Slider sliderMock;
    private ArgumentCaptor<Runnable> captor;


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

        colorPickerMock = mock(ColorPicker.class);
        when(colorPickerMock.getValue()).thenReturn(Color.YELLOW);

        sliderMock = mock(Slider.class);
        when(sliderMock.getValue()).thenReturn(42.42);

        mouseEvent = mock(MouseEvent.class);
        actionEvent = mock(ActionEvent.class);

        captor = ArgumentCaptor.forClass(Runnable.class);
    }


    @Test
    void testNodeHeightSliderDone() {
        when(mouseEvent.getSource()).thenReturn(sliderMock);
        interact(() -> basicSettingsViewController.nodeHeightSliderDone(mouseEvent));
        verify(settings, times(1)).addRunnable(any(Runnable.class));
    }

    @Test
    void testEdgeColorDone() {
        when(actionEvent.getSource()).thenReturn(colorPickerMock);
        interact(() -> basicSettingsViewController.edgeColorDone(actionEvent));
        verify(settings, times(1)).addRunnable(any(Runnable.class));
    }

    @Test
    void testNodeHeightUpdate() {
        assertThat(graphVisualizer.getNodeHeightProperty().getValue()).isEqualTo(20.0);

        when(mouseEvent.getSource()).thenReturn(sliderMock);
        interact(() -> basicSettingsViewController.nodeHeightSliderDone(mouseEvent));

        verify(settings).addRunnable(captor.capture());
        Runnable command = captor.getValue();
        command.run();

        assertThat(graphVisualizer.getNodeHeightProperty().getValue()).isEqualTo(42.42);
    }

    @Test
    void testEdgeColorUpdate() {
        assertThat(graphVisualizer.getEdgeColorProperty().getValue()).isEqualTo(Color.RED);

        when(actionEvent.getSource()).thenReturn(colorPickerMock);
        interact(() -> basicSettingsViewController.edgeColorDone(actionEvent));

        verify(settings).addRunnable(captor.capture());
        Runnable command = captor.getValue();
        command.run();

        assertThat(graphVisualizer.getEdgeColorProperty().getValue()).isEqualTo(Color.YELLOW);
    }
}
