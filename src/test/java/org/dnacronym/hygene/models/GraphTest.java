package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@code SequenceGraph}s.
 */
class GraphTest {
    @Test
    void testGetLineNumber() {
        final Graph graph = createGraphWithNodes(NodeBuilder.start().withLineNumber(3).create().toArray());

        assertThat(graph.getLineNumber(0)).isEqualTo(3);
    }

    @Test
    void testGetNodeColor() {
        final Graph graph = createGraphWithNodes(NodeBuilder.start().withColor(NodeColor.BLACK).create().toArray());

        assertThat(graph.getColor(0)).isEqualTo(NodeColor.BLACK);
    }

    @Test
    void testGetUnscaledXPosition() {
        final Graph graph = createGraphWithNodes(NodeBuilder.start().withUnscaledXPosition(33).create().toArray());

        assertThat(graph.getUnscaledXPosition(0)).isEqualTo(33);
    }

    @Test
    void testGetUnscaledYPosition() {
        final Graph graph = createGraphWithNodes(NodeBuilder.start().withUnscaledYPosition(44).create().toArray());

        assertThat(graph.getUnscaledYPosition(0)).isEqualTo(44);
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
        Node node = NodeBuilder.start().create();
        final Graph graph = createGraphWithNodes(node.toArray());

        assertThat(graph.getNode(0).toArray()).isEqualTo(node.toArray());
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
                .withIncomingEdge(45, 0)
                .withIncomingEdge(85, 0)
                .withIncomingEdge(30, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        assertThat(graph.getNeighbourCount(0, SequenceDirection.LEFT)).isEqualTo(3);
    }

    @Test
    void testVisitRightNeighbours() {
        final int[] node = NodeBuilder.start()
                .withOutgoingEdge(69, 0)
                .withOutgoingEdge(99, 0)
                .withOutgoingEdge(72, 0)
                .withOutgoingEdge(54, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        final List<Integer> neighbours = new ArrayList<>();
        graph.visitNeighbours(0, SequenceDirection.RIGHT, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(69, 99, 72, 54);
    }

    @Test
    void testVisitLeftNeighbours() {
        final int[] node = NodeBuilder.start()
                .withIncomingEdge(15, 0)
                .withIncomingEdge(67, 0)
                .withIncomingEdge(10, 0)
                .withIncomingEdge(60, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        final List<Integer> neighbours = new ArrayList<>();
        graph.visitNeighbours(0, SequenceDirection.LEFT, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(15, 67, 10, 60);
    }

    @Test
    void testVisitAllRight() {
        final int[] nodeA = NodeBuilder.start()
                .withOutgoingEdge(1, 0)
                .withOutgoingEdge(2, 0)
                .withOutgoingEdge(4, 0)
                .toArray();
        final int[] nodeB = NodeBuilder.start()
                .withOutgoingEdge(2, 0)
                .withOutgoingEdge(3, 0)
                .toArray();
        final int[] nodeC = NodeBuilder.start()
                .withOutgoingEdge(4, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(nodeA, nodeB, nodeC);

        final List<Integer> neighbours = new ArrayList<>();
        graph.visitAll(SequenceDirection.LEFT, ignored -> false, neighbours::add);

        assertThat(neighbours).containsExactly(0, 1, 2, 4, 2, 3, 4);
    }


    private Graph createGraphWithNodes(final int[]... nodes) {
        return new Graph(nodes, null);
    }
}
