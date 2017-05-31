package org.dnacronym.hygene.ui.bookmark;

import javafx.beans.property.IntegerProperty;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link SimpleBookmarkStore}s.
 */
final class SimpleBookmarkStoreTest {
    private SimpleBookmarkStore simpleBookmarkStore;
    private IntegerProperty centerNodeIdProperty;
    private IntegerProperty hopsProperty;

    private Bookmark bookmark;
    private GraphVisualizer graphVisualizer;


    @BeforeEach
    void beforeEach() throws ParseException {
        bookmark = new Bookmark(0, 1, 32, "1234");

        final GraphStore graphStore = new GraphStore();

        graphVisualizer = mock(GraphVisualizer.class);
        centerNodeIdProperty = mock(IntegerProperty.class);
        hopsProperty = mock(IntegerProperty.class);
        when(graphVisualizer.getCenterNodeIdProperty()).thenReturn(centerNodeIdProperty);
        when(graphVisualizer.getHopsProperty()).thenReturn(hopsProperty);

        simpleBookmarkStore = new SimpleBookmarkStore(graphStore, graphVisualizer);
    }


    /*
     * Visibility.
     */

    @Test
    void testOriginalVisibilityTrue() {
        assertThat(simpleBookmarkStore.getTableVisibleProperty().get()).isTrue();
    }


    /*
     * Get set bookmarks.
     */

    @Test
    void testGetBookmarks() {
        simpleBookmarkStore.addBookmark(bookmark);

        final SimpleBookmark simpleBookmark = simpleBookmarkStore.getSimpleBookmarks().get(0);

        assertThat(simpleBookmark.getBookmark()).isEqualTo(bookmark);
    }

    @Test
    void testSetBookmarks() throws ParseException {
        final Bookmark bookmark2 = new Bookmark(1, 20, 20, "asdf");
        simpleBookmarkStore.addBookmarks(Arrays.asList(bookmark, bookmark2));

        assertThat(simpleBookmarkStore.getSimpleBookmarks()).hasSize(2);
        assertThat(simpleBookmarkStore.getSimpleBookmarks().get(0))
                .isNotEqualTo(simpleBookmarkStore.getSimpleBookmarks().get(1));
    }

    @Test
    void testSetOnClick() {
        simpleBookmarkStore.addBookmark(bookmark);

        final SimpleBookmark simpleBookmark = simpleBookmarkStore.getSimpleBookmarks().get(0);
        simpleBookmark.getOnClick().run();

        verify(graphVisualizer, times(1)).setSelectedNode(0);
        verify(centerNodeIdProperty, times(1)).set(0);
        verify(hopsProperty, times(1)).set(32);
    }
}
