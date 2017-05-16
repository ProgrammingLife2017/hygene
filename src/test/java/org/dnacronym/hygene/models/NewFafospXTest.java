package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for FAFOSP-X.
 */
class NewFafospXTest {
    @Test
    void testNoNeighboursLength() {
        final int[] source = NodeBuilder.start()
                .withOutgoingEdge(1, 0)
                .toArray();
        final int[] node1 = NodeBuilder.start()
                .withSequenceLength(6)
                .withIncomingEdge(0, 0)
                .withOutgoingEdge(2, 1)
                .toArray();
        final int[] sink = NodeBuilder.start()
                .withIncomingEdge(1, 1)
                .toArray();

        final Graph graph = createGraphWithNodes(source, node1, sink);

        assertThat(graph.getUnscaledXPosition(1)).isEqualTo(1 + 6);
    }

    @Test
    void testOneNeighbour() {
        final int[] source = NodeBuilder.start()
                .withOutgoingEdge(1, 0)
                .toArray();
        final int[] node1 = NodeBuilder.start()
                .withSequenceLength(7)
                .withIncomingEdge(0, 0)
                .withOutgoingEdge(2, 1)
                .toArray();
        final int[] node2 = NodeBuilder.start()
                .withSequenceLength(4)
                .withIncomingEdge(1, 1)
                .withOutgoingEdge(3, 2)
                .toArray();
        final int[] sink = NodeBuilder.start()
                .withIncomingEdge(2, 2)
                .toArray();

        final Graph graph = createGraphWithNodes(source, node1, node2, sink);

        assertThat(graph.getUnscaledXPosition(1)).isEqualTo(1 + 7);
        assertThat(graph.getUnscaledXPosition(2)).isEqualTo(1 + 11 + 1);
    }

    @Test
    void testTwoNeighbours() {
        final int[] source = NodeBuilder.start()
                .withOutgoingEdge(1, 0)
                .withOutgoingEdge(2, 1)
                .toArray();
        final int[] nodeA = NodeBuilder.start()
                .withSequenceLength(3)
                .withIncomingEdge(0, 0)
                .withOutgoingEdge(3, 2)
                .toArray();
        final int[] nodeB = NodeBuilder.start()
                .withSequenceLength(13)
                .withIncomingEdge(0, 1)
                .withOutgoingEdge(2, 3)
                .toArray();
        final int[] nodeC = NodeBuilder.start()
                .withSequenceLength(4)
                .withIncomingEdge(1, 2)
                .withIncomingEdge(2, 3)
                .withOutgoingEdge(4, 4)
                .toArray();
        final int[] sink = NodeBuilder.start()
                .withIncomingEdge(3, 4)
                .toArray();

        final Graph graph = createGraphWithNodes(source, nodeA, nodeB, nodeC, sink);

        assertThat(graph.getUnscaledXPosition(1)).isEqualTo(1 + 3);
        assertThat(graph.getUnscaledXPosition(2)).isEqualTo(1 + 13);
        assertThat(graph.getUnscaledXPosition(3)).isEqualTo(1 + 17 + 1);
    }

    @Test
    void testChainOfThree() {
        final int[] source = NodeBuilder.start()
                .withOutgoingEdge(1, 0)
                .toArray();
        final int[] node1 = NodeBuilder.start()
                .withSequenceLength(9)
                .withIncomingEdge(0, 0)
                .withOutgoingEdge(2, 1)
                .toArray();
        final int[] node2 = NodeBuilder.start()
                .withSequenceLength(19)
                .withIncomingEdge(1, 1)
                .withOutgoingEdge(3, 2)
                .toArray();
        final int[] node3 = NodeBuilder.start()
                .withSequenceLength(5)
                .withIncomingEdge(2, 2)
                .withOutgoingEdge(4, 3)
                .toArray();
        final int[] sink = NodeBuilder.start()
                .withIncomingEdge(3, 3)
                .toArray();

        final Graph graph = createGraphWithNodes(source, node1, node2, node3, sink);

        assertThat(graph.getUnscaledXPosition(1)).isEqualTo(1 + 9);
        assertThat(graph.getUnscaledXPosition(2)).isEqualTo(1 + 28 + 1);
        assertThat(graph.getUnscaledXPosition(3)).isEqualTo(1 + 33 + 2);
    }

    /**
     * Verifies correct behaviour when a node is visited twice in a depth-first order.
     */
    @Test
    void testDiamondShape() {
        final int[] source = NodeBuilder.start()
                .withOutgoingEdge(1, 0)
                .toArray();
        final int[] node1 = NodeBuilder.start()
                .withSequenceLength(7)
                .withIncomingEdge(0, 0)
                .withOutgoingEdge(2, 1)
                .withOutgoingEdge(3, 2)
                .toArray();
        final int[] node2 = NodeBuilder.start()
                .withSequenceLength(5)
                .withIncomingEdge(1, 1)
                .withOutgoingEdge(4, 3)
                .toArray();
        final int[] node3 = NodeBuilder.start()
                .withSequenceLength(14)
                .withIncomingEdge(1, 2)
                .withOutgoingEdge(4, 4)
                .toArray();
        final int[] node4 = NodeBuilder.start()
                .withSequenceLength(12)
                .withIncomingEdge(2, 3)
                .withIncomingEdge(3, 4)
                .withOutgoingEdge(5, 5)
                .toArray();
        final int[] sink = NodeBuilder.start()
                .withIncomingEdge(4, 5)
                .toArray();

        final Graph graph = createGraphWithNodes(source, node1, node2, node3, node4, sink);

        assertThat(graph.getUnscaledXPosition(1)).isEqualTo(1 + 7);
        assertThat(graph.getUnscaledXPosition(2)).isEqualTo(1 + 12 + 1);
        assertThat(graph.getUnscaledXPosition(3)).isEqualTo(1 + 21 + 1);
        assertThat(graph.getUnscaledXPosition(4)).isEqualTo(1 + 33 + 2);
    }

    /**
     * Verifies correct behaviour when a node is visited twice in a breadth-first order.
     */
    @Test
    void testBreadthFirstVisitTwice() {
        final int[] source = NodeBuilder.start()
                .withOutgoingEdge(1, 0)
                .toArray();
        final int[] node1 = NodeBuilder.start()
                .withSequenceLength(14)
                .withIncomingEdge(0, 0)
                .withOutgoingEdge(2, 1)
                .withOutgoingEdge(3, 2)
                .toArray();
        final int[] node2 = NodeBuilder.start()
                .withSequenceLength(15)
                .withIncomingEdge(1, 1)
                .withOutgoingEdge(3, 3)
                .toArray();
        final int[] node3 = NodeBuilder.start()
                .withSequenceLength(8)
                .withIncomingEdge(1, 2)
                .withIncomingEdge(2, 3)
                .withOutgoingEdge(4, 4)
                .toArray();
        final int[] sink = NodeBuilder.start()
                .withIncomingEdge(3, 4)
                .toArray();

        final Graph graph = createGraphWithNodes(source, node1, node2, node3, sink);

        assertThat(graph.getUnscaledXPosition(1)).isEqualTo(1 + 14);
        assertThat(graph.getUnscaledXPosition(2)).isEqualTo(1 + 29 + 1);
        assertThat(graph.getUnscaledXPosition(3)).isEqualTo(1 + 37 + 2);
    }

    @Test
    void testInsertionBubble() {
        final int[] source = NodeBuilder.start()
                .withOutgoingEdge(1, 0)
                .toArray();
        final int[] node1 = NodeBuilder.start()
                .withSequenceLength(3)
                .withIncomingEdge(0, 0)
                .withOutgoingEdge(2, 1)
                .withOutgoingEdge(3, 2)
                .toArray();
        final int[] node2 = NodeBuilder.start()
                .withSequenceLength(5)
                .withIncomingEdge(1, 1)
                .withIncomingEdge(3, 3)
                .withOutgoingEdge(4, 4)
                .toArray();
        final int[] node3 = NodeBuilder.start()
                .withSequenceLength(7)
                .withIncomingEdge(1, 2)
                .withOutgoingEdge(2, 3)
                .toArray();
        final int[] sink = NodeBuilder.start()
                .withIncomingEdge(2, 4)
                .toArray();

        final Graph graph = createGraphWithNodes(source, node1, node2, node3, sink);

        assertThat(graph.getUnscaledXPosition(1)).isEqualTo(1 + 3);
        assertThat(graph.getUnscaledXPosition(2)).isEqualTo(1 + 15 + 2);
        assertThat(graph.getUnscaledXPosition(3)).isEqualTo(1 + 10 + 1);
    }


    private Graph createGraphWithNodes(final int[]... nodes) {
        return new Graph(nodes, null);
    }
}
