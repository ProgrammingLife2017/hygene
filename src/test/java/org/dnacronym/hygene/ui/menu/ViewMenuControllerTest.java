package org.dnacronym.hygene.ui.menu;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ViewMenuController}.
 */
final class ViewMenuControllerTest extends UITestBase {
    private ViewMenuController viewMenuController;
    private SequenceVisualizer sequenceVisualizer;


    @Override
    public void beforeEach() {
        final BooleanProperty sequenceVisible = new SimpleBooleanProperty();
        sequenceVisualizer = mock(SequenceVisualizer.class);
        when(sequenceVisualizer.getVisibleProperty()).thenReturn(sequenceVisible);

        viewMenuController = new ViewMenuController();
        viewMenuController.setSequenceVisualizer(sequenceVisualizer);
    }


    @Test
    void testToggleSequence() {
        final boolean original = sequenceVisualizer.getVisibleProperty().get();

        viewMenuController.toggleSequenceVisualizerAction(mock(ActionEvent.class));

        assertThat(sequenceVisualizer.getVisibleProperty().get()).isNotEqualTo(original);
    }
}
