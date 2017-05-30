package org.dnacronym.hygene.ui.settings;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link QuerySettingsController}.
 */
final class QuerySettingsControllerTest extends UITestBase {
    private QuerySettingsController querySettingsController;
    private GraphDimensionsCalculator graphDimensionsCalculator;
    private IntegerProperty centerNodeIdProperty;
    private IntegerProperty radiusProperty;
    private Settings settings;


    @Override
    public void beforeEach() {
        settings = mock(Settings.class);
        graphDimensionsCalculator = mock(GraphDimensionsCalculator.class);
        centerNodeIdProperty = new SimpleIntegerProperty(-1);
        radiusProperty = new SimpleIntegerProperty(-1);

        when(graphDimensionsCalculator.getCenterNodeIdProperty()).thenReturn(centerNodeIdProperty);
        when(graphDimensionsCalculator.getRadiusProperty()).thenReturn(radiusProperty);

        querySettingsController = new QuerySettingsController();
        querySettingsController.setGraphDimensionsCalculator(graphDimensionsCalculator);
        querySettingsController.setSettings(settings);
    }


    @Test
    void testSetNodeId() {
        final TextField source = new TextField("1001");
        final KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getSource()).thenReturn(source);

        interact(() -> querySettingsController.setNodeId(keyEvent));

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(settings).addRunnable(captor.capture());
        final Runnable command = captor.getValue();
        command.run();

        assertThat(centerNodeIdProperty.get()).isEqualTo(1001);
    }

    @Test
    void testSetNodeIdSantizeText() {
        final TextField source = new TextField("0p0q1");
        final KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getSource()).thenReturn(source);

        interact(() -> querySettingsController.setNodeId(keyEvent));

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(settings).addRunnable(captor.capture());
        final Runnable command = captor.getValue();
        command.run();

        assertThat(centerNodeIdProperty.get()).isEqualTo(1);
    }

    @Test
    void testSetRange() {
        final TextField source = new TextField("99");
        final KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getSource()).thenReturn(source);

        interact(() -> querySettingsController.setRadius(keyEvent));

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(settings).addRunnable(captor.capture());
        final Runnable command = captor.getValue();
        command.run();

        assertThat(radiusProperty.get()).isEqualTo(99);
    }

    @Test
    void testSetRangeSanitizeText() {
        final TextField source = new TextField("9ppp19");
        final KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getSource()).thenReturn(source);

        interact(() -> querySettingsController.setRadius(keyEvent));

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(settings).addRunnable(captor.capture());
        final Runnable command = captor.getValue();
        command.run();

        assertThat(radiusProperty.get()).isEqualTo(919);
    }
}
