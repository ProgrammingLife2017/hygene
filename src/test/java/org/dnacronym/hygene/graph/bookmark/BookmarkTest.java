package org.dnacronym.hygene.graph.bookmark;

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
        assertThat(bookmark.getNodeId()).isEqualTo(NODE_ID);
    }

    @Test
    void getBaseOffset() {
        assertThat(bookmark.getBaseOffset()).isEqualTo(BASE_OFFSET);
    }

    @Test
    void getRadius() {
        assertThat(bookmark.getRadius()).isEqualTo(RADIUS);
    }

    @Test
    void getDescription() {
        assertThat(bookmark.getDescription()).isEqualTo(DESCRIPTION);
    }
}
