package org.dnacronym.hygene.ui.menu;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.bookmark.SimpleBookmarkStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
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
    private SimpleBookmarkStore simpleBookmarkStore;
    private SequenceVisualizer sequenceVisualizer;
    private GraphVisualizer graphVisualizer;


    @Override
    public void beforeEach() {
        final BooleanProperty bookmarkTableVisible = new SimpleBooleanProperty();
        final BooleanProperty bookmarkCreateVisible = new SimpleBooleanProperty();
        simpleBookmarkStore = mock(SimpleBookmarkStore.class);
        when(simpleBookmarkStore.getTableVisibleProperty()).thenReturn(bookmarkTableVisible);
        when(simpleBookmarkStore.getBookmarkCreateVisibleProperty()).thenReturn(bookmarkCreateVisible);

        final BooleanProperty sequenceVisible = new SimpleBooleanProperty();
        sequenceVisualizer = mock(SequenceVisualizer.class);
        when(sequenceVisualizer.getVisibleProperty()).thenReturn(sequenceVisible);

        final BooleanProperty nodePropertiesVisible = new SimpleBooleanProperty();
        graphVisualizer = mock(GraphVisualizer.class);
        when(graphVisualizer.getNodePropertiesVisibleProperty()).thenReturn(nodePropertiesVisible);

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

        viewMenuController.toggleNodePropertiesAction(mock(ActionEvent.class));

        assertThat(graphVisualizer.getNodePropertiesVisibleProperty().get()).isNotEqualTo(original);
    }
}
