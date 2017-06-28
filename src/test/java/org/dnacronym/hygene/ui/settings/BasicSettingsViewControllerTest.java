package org.dnacronym.hygene.ui.settings;

import com.google.inject.testing.fieldbinder.Bind;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.graph.GraphMovementCalculator;
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
 * Unit tests for {@link BasicSettingsViewController}s.
 */
final class BasicSettingsViewControllerTest extends UITestBase {
    private BasicSettingsViewController basicSettingsViewController;
    @Bind
    private GraphVisualizer graphVisualizer;
    @Bind
    private GraphMovementCalculator graphMovementCalculator;
    @Bind
    private Settings settings;
    private MouseEvent mouseEvent;
    private ActionEvent actionEvent;
    private ColorPicker colorPicker;
    private Slider slider;
    private ArgumentCaptor<Runnable> captor;


    @Override
    public void beforeEach() {
        graphVisualizer = mock(GraphVisualizer.class);
        graphMovementCalculator = mock(GraphMovementCalculator.class);
        settings = mock(Settings.class);
        createContextOfTest();

        basicSettingsViewController = new BasicSettingsViewController();
        injectMembers(basicSettingsViewController);

        final SimpleDoubleProperty height = new SimpleDoubleProperty();
        height.setValue(20);
        when(graphVisualizer.getNodeHeightProperty()).thenReturn(height);


        final SimpleDoubleProperty panning = new SimpleDoubleProperty(21);
        when(graphMovementCalculator.getPanningSensitivityProperty()).thenReturn(panning);

        final SimpleDoubleProperty zooming = new SimpleDoubleProperty(0.042);
        when(graphMovementCalculator.getZoomingSensitivityProperty()).thenReturn(zooming);

        final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.RED);
        when(graphVisualizer.getEdgeColorProperty()).thenReturn(color);

        colorPicker = mock(ColorPicker.class);
        when(colorPicker.getValue()).thenReturn(Color.YELLOW);

        slider = mock(Slider.class);
        when(slider.getValue()).thenReturn(42.42);

        mouseEvent = mock(MouseEvent.class);
        actionEvent = mock(ActionEvent.class);

        captor = ArgumentCaptor.forClass(Runnable.class);
    }


    @Test
    void testNodeHeightSliderDone() {
        when(mouseEvent.getSource()).thenReturn(slider);
        interact(() -> basicSettingsViewController.nodeHeightSliderDone(mouseEvent));
        verify(settings, times(1)).addRunnable(any(Runnable.class));
    }

    @Test
    void testNodeHeightUpdate() {
        assertThat(graphVisualizer.getNodeHeightProperty().getValue()).isEqualTo(20.0);

        when(mouseEvent.getSource()).thenReturn(slider);
        interact(() -> basicSettingsViewController.nodeHeightSliderDone(mouseEvent));

        verify(settings).addRunnable(captor.capture());
        final Runnable command = captor.getValue();
        command.run();

        assertThat(graphVisualizer.getNodeHeightProperty().getValue()).isEqualTo(42.42);
    }

    @Test
    void testEdgeColorUpdate() {
        assertThat(graphVisualizer.getEdgeColorProperty().getValue()).isEqualTo(Color.RED);

        when(actionEvent.getSource()).thenReturn(colorPicker);
        interact(() -> basicSettingsViewController.edgeColorDone(actionEvent));

        verify(settings).addRunnable(captor.capture());
        final Runnable command = captor.getValue();
        command.run();

        assertThat(graphVisualizer.getEdgeColorProperty().getValue()).isEqualTo(Color.YELLOW);
    }

    @Test
    void testPanningSensitivitySliderDone() {
        when(mouseEvent.getSource()).thenReturn(slider);
        interact(() -> basicSettingsViewController.zoomingSensitivitySliderDone(mouseEvent));
        verify(settings, times(1)).addRunnable(any(Runnable.class));
    }

    @Test
    void testZoomingSensitivitySliderDone() {
        when(mouseEvent.getSource()).thenReturn(slider);
        interact(() -> basicSettingsViewController.panningSensitivitySliderDone(mouseEvent));
        verify(settings, times(1)).addRunnable(any(Runnable.class));
    }

    @Test
    void testPanningSensitivitySliderUpdate() {
        assertThat(graphMovementCalculator.getPanningSensitivityProperty().getValue()).isEqualTo(21.0);

        when(mouseEvent.getSource()).thenReturn(slider);
        interact(() -> basicSettingsViewController.panningSensitivitySliderDone(mouseEvent));

        verify(settings).addRunnable(captor.capture());
        Runnable command = captor.getValue();
        command.run();

        assertThat(graphMovementCalculator.getPanningSensitivityProperty().getValue()).isEqualTo(42.42);
    }

    @Test
    void testZoomingSensitivitySliderUpdate() {
        assertThat(graphMovementCalculator.getZoomingSensitivityProperty().getValue()).isEqualTo(0.042);

        when(mouseEvent.getSource()).thenReturn(slider);
        basicSettingsViewController.zoomingSensitivitySliderDone(mouseEvent);

        verify(settings).addRunnable(captor.capture());
        Runnable command = captor.getValue();
        command.run();

        assertThat(graphMovementCalculator.getZoomingSensitivityProperty().getValue()).isEqualTo(42.42);
    }
}
