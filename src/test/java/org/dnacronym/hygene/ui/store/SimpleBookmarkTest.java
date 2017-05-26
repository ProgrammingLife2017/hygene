package org.dnacronym.hygene.ui.store;

import javafx.beans.property.IntegerProperty;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Test suite for the {@link SimpleBookmark} class.
 */
final class SimpleBookmarkTest {
    private Bookmark bookmark;
    private GraphVisualizer graphVisualizer;
    private IntegerProperty centerNodeIdProperty;
    private Runnable onClick;

    private SimpleBookmark simpleBookmark;


    @BeforeEach
    void beforeEach() throws ParseException {
        bookmark = new Bookmark(10, 1, 500, "1234");

        // ensure tests get a certain node with certain metadata
        graphVisualizer = mock(GraphVisualizer.class);
        centerNodeIdProperty = mock(IntegerProperty.class);
        when(graphVisualizer.getCenterNodeIdProperty()).thenReturn(centerNodeIdProperty);

        onClick = mock(Runnable.class);
        simpleBookmark = new SimpleBookmark(bookmark, onClick);
    }


    @Test
    void testGetBookmark() {
        assertThat(simpleBookmark.getBookmark()).isEqualTo(bookmark);
    }

    @Test
    void testGetNodeId() {
        assertThat(simpleBookmark.getNodeIdProperty().get()).isEqualTo(10);
    }

    @Test
    void testGetRadius() {
        assertThat(simpleBookmark.getRadiusProperty().get()).isEqualTo(500);
    }

    @Test
    void testGetBaseOffset() {
        assertThat(simpleBookmark.getBaseOffsetProperty().get()).isEqualTo(1);
    }

    @Test
    void testGetDescription() {
        assertThat(simpleBookmark.getDescriptionProperty().get()).isEqualTo("1234");
    }

    @Test
    void testClickOn() {
        simpleBookmark.getOnClick().run();

        verify(onClick, times(1)).run();
    }
}
