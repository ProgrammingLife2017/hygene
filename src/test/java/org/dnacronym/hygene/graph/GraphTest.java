package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link Graph}.
 */
final class GraphTest {
    @Test
    void testGetByteOffset() {
        final Graph graph = createGraphWithNodes(NodeBuilder.start().withByteOffset(3).create().toArray());

        assertThat(graph.getByteOffset(0)).isEqualTo(3);
    }

    @Test
    void testGetLengthSmall() {
        final Graph graph = createGraphWithNodes(NodeBuilder.start().withSequenceLength(5).create().toArray());

        assertThat(graph.getLength(0)).isEqualTo(Graph.MINIMUM_SEQUENCE_LENGTH);
    }

    @Test
    void testGetLength() {
        final Graph graph = createGraphWithNodes(NodeBuilder.start().withSequenceLength(500).create().toArray());

        assertThat(graph.getLength(0)).isEqualTo(500);
    }

    @Test
    void testGetSequenceLengthSmall() {
        final Graph graph = createGraphWithNodes(NodeBuilder.start().withSequenceLength(5).create().toArray());

        assertThat(graph.getSequenceLength(0)).isEqualTo(5);
    }

    @Test
    void testGetSequenceLength() {
        final Graph graph = createGraphWithNodes(NodeBuilder.start().withSequenceLength(500).create().toArray());

        assertThat(graph.getSequenceLength(0)).isEqualTo(500);
    }

    @Test
    void testGetUnscaledXPosition() {
        final Graph graph = createGraphWithNodes(NodeBuilder.start().withUnscaledXPosition(33).create().toArray());

        assertThat(graph.getUnscaledXPosition(0)).isEqualTo(33);
    }

    @Test
    void testGetNodeArray() {
        final int[] nodeArray = NodeBuilder.start().withUnscaledXPosition(33).create().toArray();
        final Graph graph = createGraphWithNodes(nodeArray);

        assertThat(graph.getNodeArray(0)).isEqualTo(nodeArray);
    }

    @Test
    void testGetNodeArrayOfSecondNode() {
        final int[] nodeArray = NodeBuilder.start().withUnscaledXPosition(33).create().toArray();
        final Graph graph = new Graph(new int[][] {{}, nodeArray}, null);

        assertThat(graph.getNodeArray(1)).isEqualTo(nodeArray);
    }

    @Test
    void testGetNode() {
        final ArrayBasedNode node = NodeBuilder.start().create();
        final Graph graph = createGraphWithNodes(node.toArray());

        assertThat(ArrayBasedNode.fromGraph(graph, 0).toArray()).isEqualTo(node.toArray());
    }

    @Test
    void testGetRightNeighbourCount() {
        final int[] nodeA = NodeBuilder.start()
                .withOutgoingEdge(43, 0)
                .withOutgoingEdge(98, 0)
                .withIncomingEdge(47, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(nodeA);

        assertThat(graph.getNeighbourCount(0, SequenceDirection.RIGHT)).isEqualTo(2);
    }

    @Test
    void testGetLeftNeighbourCount() {
        final int[] node = NodeBuilder.start()
                .withOutgoingEdge(74, 0)
                .withOutgoingEdge(90, 0)
                .withIncomingEdge(45, 0)
                .withIncomingEdge(85, 0)
                .withIncomingEdge(30, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        assertThat(graph.getNeighbourCount(0, SequenceDirection.LEFT)).isEqualTo(3);
    }


    private Graph createGraphWithNodes(final int[]... nodes) {
        return new Graph(nodes, null);
    }
}
