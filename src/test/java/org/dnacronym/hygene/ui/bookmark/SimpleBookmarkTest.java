package org.dnacronym.hygene.ui.bookmark;

import org.dnacronym.hygene.graph.bookmark.Bookmark;
import org.dnacronym.hygene.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Test suite for the {@link SimpleBookmark} class.
 */
final class SimpleBookmarkTest {
    private Bookmark bookmark;
    private Runnable onClick;

    private SimpleBookmark simpleBookmark;


    @BeforeEach
    void beforeEach() throws ParseException {
        bookmark = new Bookmark(10, 1, 500, "1234");

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
