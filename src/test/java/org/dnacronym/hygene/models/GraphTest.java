package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@code SequenceGraph}s.
 */
class GraphTest {
    @Test
    void testGetLineNumber() {
        final Graph graph = createGraphWithNode(NodeBuilder.start().withLineNumber(3).create().toArray());

        assertThat(graph.getLineNumber(0)).isEqualTo(3);
    }

    @Test
    void testGetNodeColor() {
        final Graph graph = createGraphWithNode(NodeBuilder.start().withColor(NodeColor.BLACK).create().toArray());

        assertThat(graph.getColor(0)).isEqualTo(NodeColor.BLACK);
    }

    @Test
    void testGetUnscaledXPosition() {
        final Graph graph = createGraphWithNode(NodeBuilder.start().withUnscaledXPosition(33).create().toArray());

        assertThat(graph.getUnscaledXPosition(0)).isEqualTo(33);
    }

    @Test
    void testGetUnscaledYPosition() {
        final Graph graph = createGraphWithNode(NodeBuilder.start().withUnscaledYPosition(44).create().toArray());

        assertThat(graph.getUnscaledYPosition(0)).isEqualTo(44);
    }

    @Test
    void testGetNodeArray() {
        final int[] nodeArray = NodeBuilder.start().withUnscaledXPosition(33).create().toArray();
        final Graph graph = createGraphWithNode(nodeArray);

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
        Node node = NodeBuilder.start().create();
        final Graph graph = createGraphWithNode(node.toArray());

        assertThat(graph.getNode(0).toArray()).isEqualTo(node.toArray());
    }


    private Graph createGraphWithNode(final int[] node) {
        return new Graph(new int[][] {node}, null);
    }
}
