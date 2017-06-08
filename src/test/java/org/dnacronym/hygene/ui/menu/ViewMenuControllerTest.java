package org.dnacronym.hygene.ui.menu;

import javafx.event.ActionEvent;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.bookmark.SimpleBookmarkStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Unit tests of {@link ViewMenuController}.
 */
final class ViewMenuControllerTest extends UITestBase {
    private ViewMenuController viewMenuController;
    private SimpleBookmarkStore simpleBookmarkStore;
    private SequenceVisualizer sequenceVisualizer;
    private GraphVisualizer graphVisualizer;


    @Override
    public void beforeEach() {
        simpleBookmarkStore = mock(SimpleBookmarkStore.class);
        sequenceVisualizer = mock(SequenceVisualizer.class);
        graphVisualizer = mock(GraphVisualizer.class);

        viewMenuController = new ViewMenuController();
        viewMenuController.setSimpleBookmarkStore(simpleBookmarkStore);
        viewMenuController.setSequenceVisualizer(sequenceVisualizer);
        viewMenuController.setGraphVisualiser(graphVisualizer);
    }


    @Test
    void testToggleSequence() {
        final boolean original = sequenceVisualizer.getVisibleProperty().get();

        viewMenuController.toggleSequenceVisualizerAction(mock(ActionEvent.class));

        assertThat(sequenceVisualizer.getVisibleProperty().get()).isNotEqualTo(original);
    }

    @Test
    void testToggleBookmarkCreate() {
        final boolean original = simpleBookmarkStore.getBookmarkCreateVisibleProperty().get();

        viewMenuController.toggleBookmarkCreateAction(mock(ActionEvent.class));

        assertThat(simpleBookmarkStore.getBookmarkCreateVisibleProperty().get()).isNotEqualTo(original);
    }

    @Test
    void testToggleBookmarkTable() {
        final boolean original = simpleBookmarkStore.getTableVisibleProperty().get();

        viewMenuController.toggleBookmarkTableAction(mock(ActionEvent.class));

        assertThat(simpleBookmarkStore.getTableVisibleProperty().get()).isNotEqualTo(original);
    }

    @Test
    void testToggleNodeProperties() {
        final boolean original = graphVisualizer.getNodePropertiesVisibleProperty().get();

        viewMenuController.toggleSequenceVisualizerAction(mock(ActionEvent.class));

        assertThat(graphVisualizer.getNodePropertiesVisibleProperty().get()).isNotEqualTo(original);

    }
}
