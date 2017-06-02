package org.dnacronym.hygene.ui.bookmark;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link SimpleBookmarkStore}s.
 */
final class SimpleBookmarkStoreTest {
    private SimpleBookmarkStore simpleBookmarkStore;
    private IntegerProperty centerNodeIdProperty;
    private IntegerProperty radiusProperty;

    private Bookmark bookmark;
    private GraphVisualizer graphVisualizer;
    private GraphDimensionsCalculator graphDimensionsCalculator;
    private SequenceVisualizer sequenceVisualizer;


    @BeforeEach
    void beforeEach() throws ParseException {
        bookmark = new Bookmark(0, 5, 32, "1234");

        final GraphStore graphStore = new GraphStore();

        graphVisualizer = mock(GraphVisualizer.class);
        sequenceVisualizer = mock(SequenceVisualizer.class);
        graphDimensionsCalculator = mock(GraphDimensionsCalculator.class);

        centerNodeIdProperty = new SimpleIntegerProperty(-1);
        radiusProperty = new SimpleIntegerProperty(-1);
        when(graphDimensionsCalculator.getCenterNodeIdProperty()).thenReturn(centerNodeIdProperty);
        when(graphDimensionsCalculator.getRadiusProperty()).thenReturn(radiusProperty);

        simpleBookmarkStore = new SimpleBookmarkStore(
                graphStore, graphVisualizer, graphDimensionsCalculator, sequenceVisualizer);
    }


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

        verify(graphVisualizer).setSelectedNode(0);
        assertThat(centerNodeIdProperty.get()).isEqualTo(0);
        assertThat(radiusProperty.get()).isEqualTo(32);
        verify(sequenceVisualizer).setOffset(5);
    }
}
