package org.dnacronym.hygene.graph;

import org.dnacronym.hygene.core.UnsignedInteger;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link ArrayBasedNode}.
 */
final class ArrayBasedNodeTest {
    @Test
    void testGetId() {
        final ArrayBasedNode node = NodeBuilder.start().withNodeId(5).create();

        assertThat(node.getId()).isEqualTo(5);
    }

    @Test
    void testGetByteOffset() {
        final ArrayBasedNode node = NodeBuilder.start().withByteOffset(4).create();

        assertThat(node.getByteOffset()).isEqualTo(4);
    }

    @Test
    void testGetSequenceLength() {
        final ArrayBasedNode node = NodeBuilder.start().withSequenceLength(3).create();

        assertThat(node.getSequenceLength()).isEqualTo(3);
    }

    @Test
    void testGetUnscaledXPosition() {
        final ArrayBasedNode node = NodeBuilder.start().withUnscaledXPosition(5).create();

        assertThat(node.getUnscaledXPosition()).isEqualTo(5);
    }

    @Test
    void testToArray() {
        final ArrayBasedNode node = NodeBuilder.start()
                .withNodeId(42)
                .withByteOffset(1)
                .withSequenceLength(5)
                .withUnscaledXPosition(3)
                .withOutgoingEdge(1, 30)
                .withOutgoingEdge(2, 40)
                .withIncomingEdge(1, 30)
                .create();

        assertThat(node.toArray()).isEqualTo(new int[] {
                UnsignedInteger.fromLong(1), 5, 3, 2, 1, 30, 2, 40, 1, 30
        });
    }

    @Test
    void testGetNumberOfOutgoingEdges() {
        final ArrayBasedNode node = NodeBuilder.start().withOutgoingEdge(1, 30).create();

        assertThat(node.getNumberOfOutgoingEdges()).isEqualTo(1);
    }

    @Test
    void testGetNumberOfIncomingEdges() {
        final ArrayBasedNode node = NodeBuilder.start().withIncomingEdge(1, 30).withIncomingEdge(2, 40).create();

        assertThat(node.getNumberOfIncomingEdges()).isEqualTo(2);
    }

    /**
     * Create a node with two outgoing edges, verify that the number of outgoing edges is computed
     * correctly (done without using the edge data structure, but based on the node array) and
     * that the set of outgoing edges contains the newly constructed edges.
     */
    @Test
    void testGetOutgoingEdges() {
        final ArrayBasedNode node = NodeBuilder.start()
                .withNodeId(10)
                .withOutgoingEdge(20, 3)
                .withOutgoingEdge(30, 4)
                .create();

        assertThat(node.getNumberOfOutgoingEdges()).isEqualTo(2);
        assertThat(node.getOutgoingEdges()).containsOnly(
                new ArrayBasedEdge(10, 20, 3, null),
                new ArrayBasedEdge(10, 30, 4, null)
        );
    }

    /**
     * Create a node with three incoming edges, verify that the number of incoming edges is computed
     * correctly (done without using the edge data structure, but based on the node array) and
     * that the set of incoming edges contains the newly constructed edges.
     */
    @Test
    void testGetIncomingEdges() {
        final ArrayBasedNode node = NodeBuilder.start()
                .withNodeId(10)
                .withIncomingEdge(20, 3)
                .withIncomingEdge(30, 4)
                .withIncomingEdge(40, 5)
                .create();

        assertThat(node.getNumberOfIncomingEdges()).isEqualTo(3);
        assertThat(node.getIncomingEdges()).containsOnly(
                new ArrayBasedEdge(20, 10, 3, null),
                new ArrayBasedEdge(30, 10, 4, null),
                new ArrayBasedEdge(40, 10, 5, null)
        );
    }
}
