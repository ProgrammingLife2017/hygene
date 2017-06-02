package org.dnacronym.hygene.ui.node;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import org.dnacronym.hygene.ui.UITestBase;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link SequenceController}.
 */
final class SequenceControllerTest extends UITestBase {
    private SequenceController sequenceController;
    private SequenceVisualizer sequenceVisualizer;
    private StringProperty sequenceProperty;


    @Override
    public void beforeEach() {
        sequenceController = new SequenceController();

        sequenceVisualizer = mock(SequenceVisualizer.class);
        sequenceProperty = new SimpleStringProperty("sequence");
        when(sequenceVisualizer.getSequenceProperty()).thenReturn(sequenceProperty);

        sequenceController.setSequenceVisualizer(sequenceVisualizer);
    }


    @Test
    void testIncrementSmallAmount() {
        interact(() -> sequenceController.incrementSmallAction(mock(ActionEvent.class)));

        verify(sequenceVisualizer).incrementOffset(1);
    }

    @Test
    void testIncrementLargeAmount() {
        final IntegerProperty onscreenBase = new SimpleIntegerProperty(10);
        when(sequenceVisualizer.getOnScreenBasesCountProperty()).thenReturn(onscreenBase);
        interact(() -> sequenceController.incrementLargeAction(mock(ActionEvent.class)));

        verify(sequenceVisualizer).incrementOffset(onscreenBase.get());
    }

    @Test
    void testDecrementSmallAmount() {
        interact(() -> sequenceController.decrementSmallAction(mock(ActionEvent.class)));

        verify(sequenceVisualizer).decrementOffset(1);
    }

    @Test
    void testDecrementLargeAmount() {
        final IntegerProperty onscreenBase = new SimpleIntegerProperty(10);
        when(sequenceVisualizer.getOnScreenBasesCountProperty()).thenReturn(onscreenBase);
        interact(() -> sequenceController.decrementLargeAction(mock(ActionEvent.class)));

        verify(sequenceVisualizer).decrementOffset(onscreenBase.get());
    }

    @Test
    void testGoToStartAction() {
        interact(() -> sequenceController.goToStartAction(mock(ActionEvent.class)));

        verify(sequenceVisualizer).setOffset(0);
    }

    @Test
    void testGoToEndAction() {
        interact(() -> sequenceController.goToEndAction(mock(ActionEvent.class)));

        verify(sequenceVisualizer).setOffset(sequenceProperty.get().length() - 1);
    }
}
