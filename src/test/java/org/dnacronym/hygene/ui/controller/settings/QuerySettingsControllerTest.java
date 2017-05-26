package org.dnacronym.hygene.ui.controller.settings;

import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import org.dnacronym.hygene.ui.UITest;
import org.dnacronym.hygene.ui.store.Settings;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link QuerySettingsController}.
 */
final class QuerySettingsControllerTest extends UITest {
    private QuerySettingsController querySettingsController;
    private GraphVisualizer graphVisualizer;
    private IntegerProperty centerNodeIdProperty;
    private IntegerProperty hopsProperty;
    private Settings settings;


    @Override
    public void beforeEach() {
        settings = mock(Settings.class);
        graphVisualizer = mock(GraphVisualizer.class);
        centerNodeIdProperty = mock(IntegerProperty.class);
        hopsProperty = mock(IntegerProperty.class);

        when(graphVisualizer.getCenterNodeIdProperty()).thenReturn(centerNodeIdProperty);
        when(graphVisualizer.getHopsProperty()).thenReturn(hopsProperty);

        querySettingsController = new QuerySettingsController();
        querySettingsController.setGraphVisualizer(graphVisualizer);
        querySettingsController.setSettings(settings);
    }


    @Test
    void testSetNodeId() {
        final TextField source = new TextField("1001");
        final ActionEvent actionEvent = mock(ActionEvent.class);
        when(actionEvent.getSource()).thenReturn(source);

        interact(() -> querySettingsController.setNodeId(actionEvent));

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(settings).addRunnable(captor.capture());
        Runnable command = captor.getValue();
        command.run();

        verify(centerNodeIdProperty, times(1)).set(1001);
    }

    @Test
    void testSetNodeIdSantizeText() {
        final TextField source = new TextField("0p0q1");
        final ActionEvent actionEvent = mock(ActionEvent.class);
        when(actionEvent.getSource()).thenReturn(source);

        interact(() -> querySettingsController.setNodeId(actionEvent));

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(settings).addRunnable(captor.capture());
        Runnable command = captor.getValue();
        command.run();

        verify(centerNodeIdProperty, times(1)).set(1);
    }

    @Test
    void testSetRange() {
        final TextField source = new TextField("99");
        final ActionEvent actionEvent = mock(ActionEvent.class);
        when(actionEvent.getSource()).thenReturn(source);

        interact(() -> querySettingsController.setRange(actionEvent));

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(settings).addRunnable(captor.capture());
        Runnable command = captor.getValue();
        command.run();

        verify(hopsProperty, times(1)).set(99);
    }

    @Test
    void testSetRangeSanitizeText() {
        final TextField source = new TextField("9ppp19");
        final ActionEvent actionEvent = mock(ActionEvent.class);
        when(actionEvent.getSource()).thenReturn(source);

        interact(() -> querySettingsController.setRange(actionEvent));

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(settings).addRunnable(captor.capture());
        Runnable command = captor.getValue();
        command.run();

        verify(hopsProperty, times(1)).set(919);
    }
}
