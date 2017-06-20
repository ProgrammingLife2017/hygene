package org.dnacronym.hygene.graph.node;

import org.dnacronym.hygene.graph.metadata.NodeMetadata;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;


/**
 * Unit tests for {@link AggregateNode}.
 */
class AggregateNodeTest {
    @Test
    void testEmptyCollection() {
        assertThrows(IllegalArgumentException.class, () -> new AggregateNode(new ArrayList<>()));
    }

    @Test
    void testSingleNodeLength() {
        final Collection<Node> nodes = new ArrayList<>();
        final Node node = new Segment(2, 75, 49);
        nodes.add(node);

        final AggregateNode aggregateNode = new AggregateNode(nodes);

        assertThat(aggregateNode.getLength()).isEqualTo(node.getLength());
    }

    @Test
    void testMultipleNodeLength() {
        final Collection<Node> nodes = new ArrayList<>();
        final Node nodeA = new Segment(96, 2, 64);
        final Node nodeB = new Segment(100, 63, 25);
        nodes.add(nodeA);
        nodes.add(nodeB);

        final AggregateNode aggregateNode = new AggregateNode(nodes);

        assertThat(aggregateNode.getLength()).isEqualTo(nodeB.getLength());
    }

    @Test
    void testGetNodes() {
        final Collection<Node> nodes = new ArrayList<>();
        final Node nodeA = new Segment(52, 9, 27);
        final Node nodeB = new Segment(98, 75, 88);
        final Node nodeC = new Segment(59, 73, 61);
        nodes.add(nodeA);
        nodes.add(nodeB);
        nodes.add(nodeC);

        final AggregateNode aggregateNode = new AggregateNode(nodes);

        assertThat(aggregateNode.getNodes()).isNotEqualTo(nodes);
        assertThat(aggregateNode.getNodes()).containsExactlyElementsOf(nodes);
    }

    /**
     * Tests that {@link AggregateNode#getNodes()} returns the nodes in the collection during construction, even when
     * nodes are later added to that collection.
     */
    @Test
    void testGetNodesAddLater() {
        final Collection<Node> nodes = new ArrayList<>();
        final Node nodeA = new Segment(85, 81, 66);
        nodes.add(nodeA);

        final AggregateNode aggregateNode = new AggregateNode(nodes);
        final Node nodeB = new Segment(29, 73, 64);
        nodes.add(nodeB);

        assertThat(aggregateNode.getNodes()).isNotEqualTo(nodes);
        assertThat(aggregateNode.getNodes()).containsExactly(nodeA);
    }

    @Test
    void testThatMetadataIsNotSupported() {
        final AggregateNode node = new AggregateNode(Arrays.asList(new Segment(0, 0, 0)));

        final Throwable getMetadataException = catchThrowable(() -> node.getMetadata());
        assertThat(getMetadataException).isInstanceOf(UnsupportedOperationException.class);

        final Throwable setMetadataException = catchThrowable(() -> node.setMetadata(mock(NodeMetadata.class)));
        assertThat(setMetadataException).isInstanceOf(UnsupportedOperationException.class);

        assertThat(node.hasMetadata()).isFalse();
    }
}
