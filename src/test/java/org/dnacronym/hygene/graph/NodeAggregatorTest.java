package org.dnacronym.hygene.graph;

import org.dnacronym.hygene.graph.edge.AggregateEdge;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.node.AggregateNode;
import org.dnacronym.hygene.graph.node.FillNode;
import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.node.Segment;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link NodeAggregator}.
 */
class NodeAggregatorTest {
    /**
     * Tests that aggregation fails if the node has no neighbours.
     */
    @Test
    void testAggregateNoNeighbours() {
        assertThat(NodeAggregator.aggregate(new Segment(14, 48, 51))).isNull();
    }

    /**
     * Tests that aggregation fails if the node has only one neighbour.
     */
    @Test
    void testAggregateOneNeighbour() {
        final Node nodeA = new Segment(53, 81, 42);
        final Node nodeB = new Segment(19, 4, 1);
        final Node nodeC = new Segment(27, 3, 61);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeB, nodeC);

        assertThat(NodeAggregator.aggregate(nodeA)).isNull();
    }

    /**
     * Tests that aggregation fails if the node has three neighbours.
     */
    @Test
    void testAggregateThreeNeighbours() {
        final Node nodeA = new Segment(1, 92, 10);
        final Node nodeB = new Segment(21, 59, 1);
        final Node nodeC = new Segment(27, 86, 1);
        final Node nodeD = new Segment(35, 97, 1);
        final Node nodeE = new Segment(42, 76, 21);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeA, nodeC);
        linkNodes(nodeA, nodeD);
        linkNodes(nodeB, nodeE);
        linkNodes(nodeC, nodeE);
        linkNodes(nodeD, nodeE);

        assertThat(NodeAggregator.aggregate(nodeA)).isNull();
    }

    /**
     * Tests that aggregation fails if one of the node's neighbours is not a segment.
     */
    @Test
    void testAggregateNeighboursAreNotSegments() {
        final Node nodeA = new Segment(4, 83, 96);
        final Node nodeB = new FillNode();
        final Node nodeC = new Segment(38, 79, 1);
        final Node nodeD = new Segment(53, 95, 43);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeA, nodeC);
        linkNodes(nodeB, nodeD);
        linkNodes(nodeC, nodeD);

        assertThat(NodeAggregator.aggregate(nodeA)).isNull();
    }

    /**
     * Tests that aggregation fails if one of the node's neighbours has a sequence length that is not 1.
     */
    @Test
    void testAggregateNeighboursHaveInvalidSequenceLength() {
        final Node nodeA = new Segment(75, 9, 93);
        final Node nodeB = new Segment(98, 14, 1);
        final Node nodeC = new Segment(93, 21, 50);
        final Node nodeD = new Segment(45, 77, 85);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeA, nodeC);
        linkNodes(nodeB, nodeD);
        linkNodes(nodeC, nodeD);

        assertThat(NodeAggregator.aggregate(nodeA)).isNull();
    }

    /**
     * Tests that aggregation fails if one of the node's neighbours has no neighbours.
     */
    @Test
    void testAggregateNeighbourHasNoNeighbours() {
        final Node nodeA = new Segment(57, 37, 11);
        final Node nodeB = new Segment(65, 85, 1);
        final Node nodeC = new Segment(89, 5, 1);
        final Node nodeD = new Segment(7, 100, 90);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeA, nodeC);
        linkNodes(nodeC, nodeD);

        assertThat(NodeAggregator.aggregate(nodeA)).isNull();
    }

    /**
     * Tests that aggregation fails if one of the node's neighbours has more than one neighbour.
     */
    @Test
    void testAggregateNeighbourHasMultipleNeighbours() {
        final Node nodeA = new Segment(91, 91, 68);
        final Node nodeB = new Segment(100, 62, 1);
        final Node nodeC = new Segment(85, 44, 1);
        final Node nodeD = new Segment(24, 28, 81);
        final Node nodeE = new Segment(64, 73, 35);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeA, nodeC);
        linkNodes(nodeB, nodeD);
        linkNodes(nodeC, nodeD);
        linkNodes(nodeC, nodeE);

        assertThat(NodeAggregator.aggregate(nodeA)).isNull();
    }

    /**
     * Tests that aggregation fails if the node's neighbours do not share their neighbour.
     */
    @Test
    void testAggregateNeighboursHaveDifferentNeighbour() {
        final Node nodeA = new Segment(13, 23, 20);
        final Node nodeB = new Segment(75, 60, 1);
        final Node nodeC = new Segment(54, 21, 1);
        final Node nodeD = new Segment(24, 44, 48);
        final Node nodeE = new Segment(66, 8, 100);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeA, nodeC);
        linkNodes(nodeB, nodeD);
        linkNodes(nodeC, nodeE);

        assertThat(NodeAggregator.aggregate(nodeA)).isNull();
    }

    /**
     * Tests that aggregation fails if the neighbour of the node's neighbours has a neighbour that is not one of the
     * original node's neighbours.
     * <p><pre>
     * . . . B . . .
     * . . / . \ . .
     * . A . D - E .
     * . . \ . / . .
     * . . . C . . .
     * </pre>
     */
    @Test
    void testAggregateNeighbourNeighbourHasOtherIncomingNeighbour() {
        final Node nodeA = new Segment(74, 55, 35);
        final Node nodeB = new Segment(87, 53, 1);
        final Node nodeC = new Segment(58, 55, 1);
        final Node nodeD = new Segment(67, 42, 1);
        final Node nodeE = new Segment(47, 90, 60);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeA, nodeC);
        linkNodes(nodeB, nodeE);
        linkNodes(nodeC, nodeE);
        linkNodes(nodeD, nodeE);

        assertThat(NodeAggregator.aggregate(nodeA)).isNull();
    }

    /**
     * Tests that an aggregated node contains the correct nodes.
     */
    @Test
    void testAggregateSuccessContainment() {
        final Node nodeA = new Segment(4, 13, 24);
        final Node nodeB = new Segment(43, 97, 1);
        final Node nodeC = new Segment(58, 44, 1);
        final Node nodeD = new Segment(19, 57, 48);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeA, nodeC);
        linkNodes(nodeB, nodeD);
        linkNodes(nodeC, nodeD);

        final AggregateNode aggregateNode = NodeAggregator.aggregate(nodeA);
        assertThat(aggregateNode).isNotNull();
        assertThat(aggregateNode.getNodes()).containsExactly(nodeB, nodeC);
    }

    /**
     * Tests that an aggregated node is correctly linked to the other nodes.
     */
    @Test
    void testAggregateSuccessEdges() {
        final Node nodeA = new Segment(93, 16, 46);
        final Node nodeB = new Segment(47, 94, 1);
        final Node nodeC = new Segment(60, 80, 1);
        final Node nodeD = new Segment(63, 59, 64);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeA, nodeC);
        linkNodes(nodeB, nodeD);
        linkNodes(nodeC, nodeD);

        final AggregateNode aggregateNode = NodeAggregator.aggregate(nodeA);
        final AggregateEdge toAggregateNode = (AggregateEdge) aggregateNode.getIncomingEdges().iterator().next();
        final AggregateEdge fromAggregateNode = (AggregateEdge) aggregateNode.getOutgoingEdges().iterator().next();

        assertThat(nodeA.getOutgoingEdges()).containsExactly(toAggregateNode);
        assertThat(toAggregateNode.getEdges()).containsExactly(
                nodeB.getIncomingEdges().iterator().next(),
                nodeC.getIncomingEdges().iterator().next()
        );
        assertThat(fromAggregateNode.getEdges()).containsExactly(
                nodeB.getOutgoingEdges().iterator().next(),
                nodeC.getOutgoingEdges().iterator().next()
        );
        assertThat(nodeD.getIncomingEdges()).containsExactly(fromAggregateNode);
    }


    /**
     * Tests that nothing happens if an empty collection is aggregated.
     */
    @Test
    void testAggregateCollectionEmpty() {
        final List<Node> nodes = new ArrayList<>();

        NodeAggregator.aggregate(nodes);

        assertThat(nodes).isEmpty();
    }

    /**
     * Tests that nothing happens if a collection with a single node is aggregated.
     */
    @Test
    void testAggregateCollectionSingleNode() {
        final List<Node> nodes = new ArrayList<>();
        final Node node = new Segment(90, 98, 55);
        nodes.add(node);

        NodeAggregator.aggregate(nodes);

        assertThat(nodes).containsExactly(node);
    }

    /**
     * Tests that a single aggregation takes place when the simplest aggregable collection of nodes is given.
     */
    @Test
    void testAggregateCollectionOneSuccess() {
        final List<Node> nodes = new ArrayList<>();
        final Node nodeA = new Segment(4, 13, 24);
        final Node nodeB = new Segment(43, 97, 1);
        final Node nodeC = new Segment(58, 44, 1);
        final Node nodeD = new Segment(19, 57, 48);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeA, nodeC);
        linkNodes(nodeB, nodeD);
        linkNodes(nodeC, nodeD);
        nodes.add(nodeA);
        nodes.add(nodeB);
        nodes.add(nodeC);
        nodes.add(nodeD);

        NodeAggregator.aggregate(nodes);

        assertThat(nodeA.getOutgoingEdges()).hasSize(1);
        assertThat(nodeD.getIncomingEdges()).hasSize(1);

        final AggregateNode aggregateNode = (AggregateNode) nodeA.getOutgoingEdges().iterator().next().getTo();
        assertThat(aggregateNode.getNodes()).containsExactlyInAnyOrder(nodeB, nodeC);

        assertThat(nodes).containsExactlyInAnyOrder(nodeA, aggregateNode, nodeD);
    }

    @Test
    void testAggregateCollectionTwoSuccesses() {
        final List<Node> nodes = new ArrayList<>();
        final Node nodeA = new Segment(93, 36, 43);
        final Node nodeB = new Segment(60, 28, 1);
        final Node nodeC = new Segment(95, 85, 1);
        final Node nodeD = new Segment(44, 69, 12);
        final Node nodeE = new Segment(73, 46, 1);
        final Node nodeF = new Segment(98, 73, 1);
        final Node nodeG = new Segment(59, 20, 65);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeA, nodeC);
        linkNodes(nodeB, nodeD);
        linkNodes(nodeC, nodeD);
        linkNodes(nodeD, nodeE);
        linkNodes(nodeD, nodeF);
        linkNodes(nodeE, nodeG);
        linkNodes(nodeF, nodeG);
        nodes.add(nodeA);
        nodes.add(nodeB);
        nodes.add(nodeC);
        nodes.add(nodeD);
        nodes.add(nodeE);
        nodes.add(nodeF);
        nodes.add(nodeG);

        NodeAggregator.aggregate(nodes);

        assertThat(nodeA.getOutgoingEdges()).hasSize(1);
        assertThat(nodeD.getIncomingEdges()).hasSize(1);

        assertThat(nodeD.getOutgoingEdges()).hasSize(1);
        assertThat(nodeG.getIncomingEdges()).hasSize(1);

        final AggregateNode aggregateNode1 = (AggregateNode) nodeA.getOutgoingEdges().iterator().next().getTo();
        assertThat(aggregateNode1.getNodes()).containsExactlyInAnyOrder(nodeB, nodeC);

        final AggregateNode aggregateNode2 = (AggregateNode) nodeD.getOutgoingEdges().iterator().next().getTo();
        assertThat(aggregateNode2.getNodes()).containsExactlyInAnyOrder(nodeE, nodeF);

        assertThat(nodes).containsExactlyInAnyOrder(nodeA, aggregateNode1, nodeD, aggregateNode2, nodeG);
    }

    /**
     * Tests that only a single aggregation takes place in a relatively complex graph.
     * <p><pre>
     * . . . . . . . . . . . . . . .
     * . . . . . C . . . G . . . . .
     * . . . . / . \ . / . \ . . . .
     * . A - B . . . E . . . I . . .
     * . . . . \ . / . \ . / . \ . .
     * . . . . . D . . . H . . . J .
     * . . . . . . \ . . . . . / . .
     * . . . . . . . F - - - - . . .
     * . . . . . . . . . . . . . . .
     * </pre>
     */
    @Test
    void testAggregateCollectionOneSuccessManyFailures() {
        final List<Node> nodes = new ArrayList<>();
        final Node nodeA = new Segment(4, 76, 86);
        final Node nodeB = new Segment(73, 12, 4);
        final Node nodeC = new Segment(36, 49, 1);
        final Node nodeD = new Segment(18, 64, 1);
        final Node nodeE = new Segment(12, 88, 89);
        final Node nodeF = new Segment(88, 100, 71);
        final Node nodeG = new Segment(89, 87, 1);
        final Node nodeH = new Segment(58, 61, 1);
        final Node nodeI = new Segment(15, 34, 15);
        final Node nodeJ = new Segment(51, 54, 71);
        final Node nodeK = new Segment(8, 40, 99);
        linkNodes(nodeA, nodeB);
        linkNodes(nodeB, nodeC);
        linkNodes(nodeB, nodeD);
        linkNodes(nodeC, nodeE);
        linkNodes(nodeD, nodeE);
        linkNodes(nodeD, nodeF);
        linkNodes(nodeE, nodeG);
        linkNodes(nodeE, nodeH);
        linkNodes(nodeF, nodeJ);
        linkNodes(nodeG, nodeI);
        linkNodes(nodeH, nodeI);
        linkNodes(nodeI, nodeJ);
        nodes.add(nodeA);
        nodes.add(nodeB);
        nodes.add(nodeC);
        nodes.add(nodeD);
        nodes.add(nodeE);
        nodes.add(nodeF);
        nodes.add(nodeG);
        nodes.add(nodeH);
        nodes.add(nodeI);
        nodes.add(nodeJ);
        nodes.add(nodeK);

        NodeAggregator.aggregate(nodes);

        assertThat(nodeB.getOutgoingEdges()).hasSize(2);
        assertThat(nodeE.getIncomingEdges()).hasSize(2);
        assertThat(nodeE.getOutgoingEdges()).hasSize(1);
        assertThat(nodeI.getIncomingEdges()).hasSize(1);

        final AggregateNode aggregateNode = (AggregateNode) nodeE.getOutgoingEdges().iterator().next().getTo();
        assertThat(nodes).containsExactlyInAnyOrder(nodeA, nodeB, nodeC, nodeD, nodeE, nodeF, aggregateNode,
                nodeI, nodeJ, nodeK);
        assertThat(aggregateNode.getNodes()).containsExactlyInAnyOrder(nodeG, nodeH);
    }


    /**
     * Connects two nodes with an edge.
     *
     * @param from the node from which the edge departs
     * @param to   the node at which the edge arrives
     */
    private void linkNodes(final Node from, final Node to) {
        final Edge edge = new Edge(from, to);
        from.getOutgoingEdges().add(edge);
        to.getIncomingEdges().add(edge);
    }
}
