package org.dnacronym.hygene.ui.store;

import org.dnacronym.hygene.ui.store.Bookmark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test suite for the {@link Bookmark} class.
 */
final class BookmarkTest {
    private static final int NODE_ID = 23;
    private static final int BASE_OFFSET = 254;
    private static final int RADIUS = 15;
    private static final String DESCRIPTION = "Just another bookmark";

    private Bookmark bookmark;


    @BeforeEach
    void setUp() {
        bookmark = new Bookmark(NODE_ID, BASE_OFFSET, RADIUS, DESCRIPTION);
    }


    @Test
    void getNodeId() {
        assertThat(bookmark.getNodeIdProperty().get()).isEqualTo(NODE_ID);
    }

    @Test
    void getBaseOffset() {
        assertThat(bookmark.getBaseOffsetProperty().get()).isEqualTo(BASE_OFFSET);
    }

    @Test
    void getRadius() {
        assertThat(bookmark.getRadiusProperty().get()).isEqualTo(RADIUS);
    }

    @Test
    void getDescription() {
        assertThat(bookmark.getDescriptionProperty().get()).isEqualTo(DESCRIPTION);
    }
}
