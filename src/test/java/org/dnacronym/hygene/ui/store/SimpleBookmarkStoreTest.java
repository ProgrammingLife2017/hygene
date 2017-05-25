package org.dnacronym.hygene.ui.store;

import javafx.beans.property.SimpleObjectProperty;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link SimpleBookmarkStore}s.
 */
final class SimpleBookmarkStoreTest {
    private SimpleBookmarkStore simpleBookmarkStore;
    private Bookmark bookmark;
    private GraphVisualizer graphVisualizer;


    @BeforeEach
    void beforeEach() throws ParseException {
        bookmark = mock(Bookmark.class);

        // mock bookmark properties that determine what is displayed in the ui
        when(bookmark.getNodeId()).thenReturn(0);
        when(bookmark.getDescription()).thenReturn("1234");
        when(bookmark.getBaseOffset()).thenReturn(1);

        final GraphStore graphStore = mock(GraphStore.class);
        when(graphStore.getGfaFileProperty()).thenReturn(new SimpleObjectProperty<>());

        graphVisualizer = mock(GraphVisualizer.class);
        simpleBookmarkStore = new SimpleBookmarkStore(graphStore, graphVisualizer);

        simpleBookmarkStore.addBookmark(bookmark);
    }


    @Test
    void testGetBookmarks() {
        final SimpleBookmark simpleBookmark = simpleBookmarkStore.getBookmarks().get(0);

        assertThat(simpleBookmark.getNodeIdProperty().get()).isEqualTo(0);
        assertThat(simpleBookmark.getDescriptionProperty().get()).isEqualTo("1234");
        assertThat(simpleBookmark.getBaseOffsetProperty().get()).isEqualTo(1);
    }
}
