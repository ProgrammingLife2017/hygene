package org.dnacronym.hygene.ui.store;

import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.models.NodeMetadata;
import org.dnacronym.hygene.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Test suite for the {@link SimpleBookmark} class.
 */
final class SimpleBookmarkTest {
    private Bookmark bookmark;
    private Graph graph;
    private Node node;
    private NodeMetadata nodeMetadata;

    private SimpleBookmark simpleBookmark;


    @BeforeEach
    void setUp() throws ParseException {
        bookmark = mock(Bookmark.class);

        // mock bookmark properties that determine what is displayed in the ui
        when(bookmark.getNodeId()).thenReturn(0);
        when(bookmark.getDescription()).thenReturn("1234");
        when(bookmark.getBaseOffset()).thenReturn(1);

        // ensure tests get a certain node with certain metadata
        graph = mock(Graph.class);
        node = mock(Node.class);
        nodeMetadata = mock(NodeMetadata.class);
        when(nodeMetadata.getSequence()).thenReturn("abcd");
        when(node.retrieveMetadata()).thenReturn(nodeMetadata);
        when(graph.getNode(0)).thenReturn(node);

        simpleBookmark = new SimpleBookmark(bookmark, graph);
    }


    @Test
    void getBookmark() {
        assertThat(simpleBookmark.getBookmark()).isEqualTo(bookmark);
    }

    @Test
    void getBase() {
        assertThat(simpleBookmark.getBaseProperty().get()).isEqualTo("b");
    }

    @Test
    void getDescription() {
        assertThat(simpleBookmark.getDescriptionProperty().get()).isEqualTo("1234");
    }
}
