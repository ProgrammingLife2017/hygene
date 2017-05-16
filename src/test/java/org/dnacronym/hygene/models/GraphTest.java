package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@code SequenceGraph}s.
 */
class GraphTest {
    private Consumer<Integer> dummyConsumer = ignored -> {
    };


    @Test
    void testGetLineNumber() {
        final Graph graph = createGraphWithNodes(NodeBuilder.start().withLineNumber(3).create().toArray());

        assertThat(graph.getLineNumber(0)).isEqualTo(3);
    }

    @Test
    void testGetSequenceLength() {
        final Graph graph = createGraphWithNodes(NodeBuilder.start().withSequenceLength(5).create().toArray());

        assertThat(graph.getSequenceLength(0)).isEqualTo(5);
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
        final Graph graph = new Graph(new int[][]{{}, nodeArray}, null);

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
                .withOutgoingEdge(90, 0)
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
                .withOutgoingEdge(77, 0)
                .withIncomingEdge(60, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        final List<Integer> neighbours = new ArrayList<>();
        graph.visitNeighbours(0, SequenceDirection.LEFT, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(15, 67, 10, 60);
    }

    @Test
    void testVisitAllRightWithDuplicates() {
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
        final int[] nodeD = NodeBuilder.start().toArray();
        final int[] nodeE = NodeBuilder.start().toArray();
        final Graph graph = createGraphWithNodes(nodeA, nodeB, nodeC, nodeD, nodeE);

        final List<Integer> neighbours = new ArrayList<>();
        graph.visitAll(SequenceDirection.RIGHT, node -> false, neighbours::add);

        assertThat(neighbours).containsExactly(0, 1, 2, 4, 2, 3, 4, 4);
    }

    @Test
    void testVisitAllRightWithoutDuplicates() {
        final int[] nodeA = NodeBuilder.start()
                .withOutgoingEdge(1, 0)
                .withOutgoingEdge(2, 0)
                .toArray();
        final int[] nodeB = NodeBuilder.start()
                .withOutgoingEdge(3, 0)
                .withOutgoingEdge(4, 0)
                .toArray();
        final int[] nodeC = NodeBuilder.start()
                .withOutgoingEdge(4, 0)
                .toArray();
        final int[] nodeD = NodeBuilder.start()
                .withOutgoingEdge(4, 0)
                .toArray();
        final int[] nodeE = NodeBuilder.start().toArray();
        final Graph graph = createGraphWithNodes(nodeA, nodeB, nodeC, nodeD, nodeE);

        final List<Integer> neighbours = new ArrayList<>();
        graph.visitAll(SequenceDirection.RIGHT, neighbours::add);

        assertThat(neighbours).containsExactly(0, 1, 2, 3, 4);
    }

    // Tests that all neighbours are visited if we visit "while true".
    @Test
    void testVisitNeighboursWhileTrue() {
        final int[] node = NodeBuilder.start()
                .withOutgoingEdge(92, 0)
                .withOutgoingEdge(69, 0)
                .withOutgoingEdge(30, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        final List<Integer> neighbours = new ArrayList<>();
        graph.visitNeighboursWhile(0, SequenceDirection.RIGHT, ignored -> true, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(92, 69, 30);
    }

    // Tests that no neighbours are visited if we visit "while false".
    @Test
    void testVisitNeighboursWhileFalse() {
        final int[] node = NodeBuilder.start()
                .withOutgoingEdge(56, 0)
                .withOutgoingEdge(87, 0)
                .withOutgoingEdge(72, 0)
                .withOutgoingEdge(60, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        final List<Integer> neighbours = new ArrayList<>();
        graph.visitNeighboursWhile(0, SequenceDirection.RIGHT, ignored -> false, neighbours::add);

        assertThat(neighbours).isEmpty();
    }

    // Tests that neighbours are visited while the number of visited neighbours has a particular value.
    @Test
    void testVisitNeighboursWhileSize() {
        final int[] node = NodeBuilder.start()
                .withIncomingEdge(1, 0)
                .withIncomingEdge(2, 0)
                .withIncomingEdge(3, 0)
                .withIncomingEdge(4, 0)
                .withIncomingEdge(5, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        final List<Integer> neighbours = new ArrayList<>();
        graph.visitNeighboursWhile(0, SequenceDirection.LEFT, ignored -> neighbours.size() < 3, neighbours::add);

        assertThat(neighbours).hasSize(3);
    }

    // Tests that the exit action is performed on the expected node.
    @Test
    void testVisitNeighboursWhileCatchAction() {
        final int[] node = NodeBuilder.start()
                .withIncomingEdge(75, 0)
                .withIncomingEdge(25, 0)
                .withIncomingEdge(22, 0)
                .withIncomingEdge(50, 0)
                .withIncomingEdge(58, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        final int[] exitNeighbour = new int[1];
        graph.visitNeighboursWhile(0, SequenceDirection.LEFT,
                neighbour -> neighbour != 22,
                neighbour -> exitNeighbour[0] = neighbour,
                dummyConsumer
        );

        assertThat(exitNeighbour[0]).isEqualTo(22);
    }

    // Tests that all neighbours are visited if we visit "until false".
    @Test
    void testVisitNeighboursUntilFalse() {
        final int[] node = NodeBuilder.start()
                .withIncomingEdge(94, 0)
                .withIncomingEdge(13, 0)
                .withIncomingEdge(23, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        final List<Integer> neighbours = new ArrayList<>();
        graph.visitNeighboursUntil(0, SequenceDirection.LEFT, ignored -> false, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(94, 13, 23);
    }

    // Tests that all neighbours are visited if we visit "until true".
    @Test
    void testVisitNeighboursUntilTrue() {
        final int[] node = NodeBuilder.start()
                .withOutgoingEdge(65, 0)
                .withOutgoingEdge(79, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        final List<Integer> neighbours = new ArrayList<>();
        graph.visitNeighboursUntil(0, SequenceDirection.RIGHT, ignored -> true, neighbours::add);

        assertThat(neighbours).isEmpty();
    }

    // Tests that neighbours are visited until the number of visited neighbours exceeds a particular value.
    @Test
    void testVisitNeighboursUntilSize() {
        final int[] node = NodeBuilder.start()
                .withIncomingEdge(1, 0)
                .withIncomingEdge(2, 0)
                .withIncomingEdge(3, 0)
                .withIncomingEdge(4, 0)
                .withIncomingEdge(5, 0)
                .withIncomingEdge(6, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        final List<Integer> neighbours = new ArrayList<>();
        graph.visitNeighboursUntil(0, SequenceDirection.LEFT, ignored -> neighbours.size() > 4, neighbours::add);

        assertThat(neighbours).hasSize(5);
    }

    // Tests that the exit action is performed on the expected node.
    @Test
    void testVisitNeighbourUntilCatchAction() {
        final int[] node = NodeBuilder.start()
                .withOutgoingEdge(83, 0)
                .withIncomingEdge(98, 0)
                .withIncomingEdge(94, 0)
                .withIncomingEdge(35, 0)
                .toArray();
        final Graph graph = createGraphWithNodes(node);

        final int[] exitNeighbour = new int[1];
        graph.visitNeighboursUntil(0, SequenceDirection.LEFT,
                neighbour -> neighbour == 35,
                neighbour -> exitNeighbour[0] = neighbour,
                dummyConsumer
        );

        assertThat(exitNeighbour[0]).isEqualTo(35);
    }


    private Graph createGraphWithNodes(final int[]... nodes) {
        return new Graph(nodes, null);
    }
}
