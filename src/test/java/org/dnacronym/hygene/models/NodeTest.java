package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class NodeTest {
    @Test
    void testGetId() {
        final Node node = NodeBuilder.start().withNodeId(5).create();

        assertThat(node.getId()).isEqualTo(5);
    }

    @Test
    void testGetLineNumber() {
        final Node node = NodeBuilder.start().withLineNumber(4).create();

        assertThat(node.getLineNumber()).isEqualTo(4);
    }

    @Test
    void testGetColor() {
        final Node node = NodeBuilder.start().withColor(NodeColor.GREEN).create();

        assertThat(node.getColor()).isEqualTo(NodeColor.GREEN);
    }

    @Test
    void testGetUnscaledXPosition() {
        final Node node = NodeBuilder.start().withUnscaledXPosition(5).create();

        assertThat(node.getUnscaledXPosition()).isEqualTo(5);
    }

    @Test
    void testGetUnscaledYPosition() {
        final Node node = NodeBuilder.start().withUnscaledYPosition(6).create();

        assertThat(node.getUnscaledYPosition()).isEqualTo(6);
    }

    @Test
    void testToArray() {
        final Node node = NodeBuilder.start()
                .withNodeId(42)
                .withLineNumber(1)
                .withColor(NodeColor.BLACK)
                .withUnscaledXPosition(3)
                .withUnscaledYPosition(4)
                .withOutgoingEdge(1, 30)
                .withOutgoingEdge(2, 40)
                .withIncomingEdge(1, 30)
                .create();

        assertThat(node.toArray()).isEqualTo(new int[]{
                1, 4, 3, 4, 2, 1, 30, 2, 40, 1, 30
        });
    }

    @Test
    void testGetNumberOfOutgoingEdges() {
        final Node node = NodeBuilder.start().withOutgoingEdge(1, 30).create();

        assertThat(node.getNumberOfOutgoingEdges()).isEqualTo(1);
    }

    @Test
    void testGetNumberOfIncomingEdges() {
        final Node node = NodeBuilder.start().withIncomingEdge(1, 30).withIncomingEdge(2, 40).create();

        assertThat(node.getNumberOfIncomingEdges()).isEqualTo(2);
    }

    /**
     * Create a node with two outgoing edges, verify that the number of outgoing edges is computed
     * correctly (done without using the edge data structure, but based on the node array) and
     * that the set of outgoing edges contains the newly constructed edges.
     */
    @Test
    void testGetOutgoingEdges() {
        final Node node = NodeBuilder.start()
                .withNodeId(10)
                .withOutgoingEdge(20, 3)
                .withOutgoingEdge(30, 4)
                .create();

        assertThat(node.getNumberOfOutgoingEdges()).isEqualTo(2);
        assertThat(node.getOutgoingEdges()).containsOnly(
                new Edge(10, 20, 3),
                new Edge(10, 30, 4)
        );
    }

    /**
     * Create a node with three incoming edges, verify that the number of incoming edges is computed
     * correctly (done without using the edge data structure, but based on the node array) and
     * that the set of incoming edges contains the newly constructed edges.
     */
    @Test
    void testGetIncomingEdges() {
        final Node node = NodeBuilder.start()
                .withNodeId(10)
                .withIncomingEdge(20, 3)
                .withIncomingEdge(30, 4)
                .withIncomingEdge(40, 5)
                .create();

        assertThat(node.getNumberOfIncomingEdges()).isEqualTo(3);
        assertThat(node.getIncomingEdges()).containsOnly(
                new Edge(20, 10, 3),
                new Edge(30, 10, 4),
                new Edge(40, 10, 5)
        );
    }
}
