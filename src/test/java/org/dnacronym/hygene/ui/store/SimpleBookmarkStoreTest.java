package org.dnacronym.hygene.ui.store;

import javafx.beans.property.SimpleObjectProperty;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.models.NodeMetadata;
import org.dnacronym.hygene.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SimpleBookmarkStore}s.
 */
final class SimpleBookmarkStoreTest {
    private SimpleBookmarkStore simpleBookmarkStore;
    private Bookmark bookmark;


    @BeforeEach
    void beforeEach() throws ParseException {
        bookmark = mock(Bookmark.class);

        // mock bookmark properties that determine what is displayed in the ui
        when(bookmark.getNodeId()).thenReturn(0);
        when(bookmark.getDescription()).thenReturn("1234");
        when(bookmark.getBaseOffset()).thenReturn(1);

        // ensure tests get a certain node with certain metadata
        final Graph graph = mock(Graph.class);
        final Node node = mock(Node.class);
        final NodeMetadata nodeMetadata = mock(NodeMetadata.class);
        when(nodeMetadata.getSequence()).thenReturn("abcd");
        when(node.retrieveMetadata()).thenReturn(nodeMetadata);
        when(graph.getNode(0)).thenReturn(node);

        final GraphStore graphStore = mock(GraphStore.class);
        when(graphStore.getGfaFileProperty()).thenReturn(new SimpleObjectProperty<>());
        simpleBookmarkStore = new SimpleBookmarkStore(graphStore);

        bookmark = mock(Bookmark.class);
        simpleBookmarkStore.addBookmark(bookmark, graph);
    }


    @Test
    void testGetBookmarks() {
        final SimpleBookmark simpleBookmark = simpleBookmarkStore.getBookmarks().get(0);
        assertThat(simpleBookmark.getBookmark()).isEqualTo(bookmark);
    }
}
