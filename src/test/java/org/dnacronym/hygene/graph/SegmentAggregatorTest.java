package org.dnacronym.hygene.graph;

import org.dnacronym.hygene.graph.edge.AggregateEdge;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.node.AggregateSegment;
import org.dnacronym.hygene.graph.node.FillNode;
import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.node.Segment;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link SegmentAggregator}.
 */
class SegmentAggregatorTest {
    /**
     * Tests that aggregation fails if the node has no neighbours.
     */
    @Test
    void testAggregateNoNeighbours() {
        assertThat(SegmentAggregator.aggregate(new Segment(14, 48, 51))).isEmpty();
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

        assertThat(SegmentAggregator.aggregate(nodeA)).isEmpty();
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

        assertThat(SegmentAggregator.aggregate(nodeA)).isEmpty();
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

        assertThat(SegmentAggregator.aggregate(nodeA)).isEmpty();
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

        assertThat(SegmentAggregator.aggregate(nodeA)).isEmpty();
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

        assertThat(SegmentAggregator.aggregate(nodeA)).isEmpty();
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

        assertThat(SegmentAggregator.aggregate(nodeA)).isEmpty();
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

        assertThat(SegmentAggregator.aggregate(nodeA)).isEmpty();
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

        assertThat(SegmentAggregator.aggregate(nodeA)).isEmpty();
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

        final AggregateSegment aggregateSegment = SegmentAggregator.aggregate(nodeA)
                .orElseThrow(() -> new AssertionError("AggregateSegment is null."));
        assertThat(aggregateSegment).isNotNull();
        assertThat(aggregateSegment.getSegments()).containsExactly((Segment) nodeB, (Segment) nodeC);
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

        final AggregateSegment aggregateSegment = SegmentAggregator.aggregate(nodeA)
                .orElseThrow(() -> new AssertionError("AggregateSegment is null."));
        final AggregateEdge toAggregateSegment = (AggregateEdge) aggregateSegment.getIncomingEdges().iterator().next();
        final AggregateEdge fromAggregateSegment = (AggregateEdge) aggregateSegment.getOutgoingEdges()
                .iterator().next();

        assertThat(nodeA.getOutgoingEdges()).containsExactly(toAggregateSegment);
        assertThat(toAggregateSegment.getEdges()).containsExactly(
                nodeB.getIncomingEdges().iterator().next(),
                nodeC.getIncomingEdges().iterator().next()
        );
        assertThat(fromAggregateSegment.getEdges()).containsExactly(
                nodeB.getOutgoingEdges().iterator().next(),
                nodeC.getOutgoingEdges().iterator().next()
        );
        assertThat(nodeD.getIncomingEdges()).containsExactly(fromAggregateSegment);
    }


    /**
     * Tests that nothing happens if an empty collection is aggregated.
     */
    @Test
    void testAggregateSubgraphEmpty() {
        final Subgraph subgraph = new Subgraph();

        SegmentAggregator.aggregate(subgraph);

        assertThat(subgraph.getNodes()).isEmpty();
    }

    /**
     * Tests that nothing happens if a collection with a single node is aggregated.
     */
    @Test
    void testAggregateSubgraphSingleNode() {
        final Subgraph subgraph = new Subgraph();
        final Node node = new Segment(90, 98, 55);
        subgraph.add(node);

        SegmentAggregator.aggregate(subgraph);

        assertThat(subgraph.getNodes()).containsExactly(node);
    }

    /**
     * Tests that a single aggregation takes place when the simplest aggregable collection of nodes is given.
     */
    @Test
    void testAggregateSubgraphOneSuccess() {
        final Subgraph subgraph = new Subgraph();
        final Segment segmentA = new Segment(4, 13, 24);
        final Segment segmentB = new Segment(43, 97, 1);
        final Segment segmentC = new Segment(58, 44, 1);
        final Segment segmentD = new Segment(19, 57, 48);
        linkNodes(segmentA, segmentB);
        linkNodes(segmentA, segmentC);
        linkNodes(segmentB, segmentD);
        linkNodes(segmentC, segmentD);
        subgraph.add(segmentA);
        subgraph.add(segmentB);
        subgraph.add(segmentC);
        subgraph.add(segmentD);

        SegmentAggregator.aggregate(subgraph);

        assertThat(segmentA.getOutgoingEdges()).hasSize(1);
        assertThat(segmentD.getIncomingEdges()).hasSize(1);

        final AggregateSegment aggregateSegment = (AggregateSegment) segmentA.getOutgoingEdges().iterator()
                .next().getTo();
        assertThat(aggregateSegment.getSegments()).containsExactlyInAnyOrder(segmentB, segmentC);

        assertThat(subgraph.getNodes()).containsExactlyInAnyOrder(segmentA, aggregateSegment, segmentD);
    }

    @Test
    void testAggregateSubgraphTwoSuccesses() {
        final Subgraph subgraph = new Subgraph();
        final Segment segmentA = new Segment(93, 36, 43);
        final Segment segmentB = new Segment(60, 28, 1);
        final Segment segmentC = new Segment(95, 85, 1);
        final Segment segmentD = new Segment(44, 69, 12);
        final Segment segmentE = new Segment(73, 46, 1);
        final Segment segmentF = new Segment(98, 73, 1);
        final Segment segmentG = new Segment(59, 20, 65);
        linkNodes(segmentA, segmentB);
        linkNodes(segmentA, segmentC);
        linkNodes(segmentB, segmentD);
        linkNodes(segmentC, segmentD);
        linkNodes(segmentD, segmentE);
        linkNodes(segmentD, segmentF);
        linkNodes(segmentE, segmentG);
        linkNodes(segmentF, segmentG);
        subgraph.add(segmentA);
        subgraph.add(segmentB);
        subgraph.add(segmentC);
        subgraph.add(segmentD);
        subgraph.add(segmentE);
        subgraph.add(segmentF);
        subgraph.add(segmentG);

        SegmentAggregator.aggregate(subgraph);

        assertThat(segmentA.getOutgoingEdges()).hasSize(1);
        assertThat(segmentD.getIncomingEdges()).hasSize(1);

        assertThat(segmentD.getOutgoingEdges()).hasSize(1);
        assertThat(segmentG.getIncomingEdges()).hasSize(1);

        final AggregateSegment aggregateSegment1 = (AggregateSegment) segmentA.getOutgoingEdges()
                .iterator().next().getTo();
        assertThat(aggregateSegment1.getSegments()).containsExactlyInAnyOrder(segmentB, segmentC);

        final AggregateSegment aggregateSegment2 = (AggregateSegment) segmentD.getOutgoingEdges()
                .iterator().next().getTo();
        assertThat(aggregateSegment2.getSegments()).containsExactlyInAnyOrder(segmentE, segmentF);

        assertThat(subgraph.getNodes()).containsExactlyInAnyOrder(segmentA, aggregateSegment1, segmentD,
                aggregateSegment2, segmentG);
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
    void testAggregateSubgraphOneSuccessManyFailures() {
        final Subgraph subgraph = new Subgraph();
        final Segment segmentA = new Segment(4, 76, 86);
        final Segment segmentB = new Segment(73, 12, 4);
        final Segment segmentC = new Segment(36, 49, 1);
        final Segment segmentD = new Segment(18, 64, 1);
        final Segment segmentE = new Segment(12, 88, 89);
        final Segment segmentF = new Segment(88, 100, 71);
        final Segment segmentG = new Segment(89, 87, 1);
        final Segment segmentH = new Segment(58, 61, 1);
        final Segment segmentI = new Segment(15, 34, 15);
        final Segment segmentJ = new Segment(51, 54, 71);
        final Segment segmentK = new Segment(8, 40, 99);
        linkNodes(segmentA, segmentB);
        linkNodes(segmentB, segmentC);
        linkNodes(segmentB, segmentD);
        linkNodes(segmentC, segmentE);
        linkNodes(segmentD, segmentE);
        linkNodes(segmentD, segmentF);
        linkNodes(segmentE, segmentG);
        linkNodes(segmentE, segmentH);
        linkNodes(segmentF, segmentJ);
        linkNodes(segmentG, segmentI);
        linkNodes(segmentH, segmentI);
        linkNodes(segmentI, segmentJ);
        subgraph.add(segmentA);
        subgraph.add(segmentB);
        subgraph.add(segmentC);
        subgraph.add(segmentD);
        subgraph.add(segmentE);
        subgraph.add(segmentF);
        subgraph.add(segmentG);
        subgraph.add(segmentH);
        subgraph.add(segmentI);
        subgraph.add(segmentJ);
        subgraph.add(segmentK);

        SegmentAggregator.aggregate(subgraph);

        assertThat(segmentB.getOutgoingEdges()).hasSize(2);
        assertThat(segmentE.getIncomingEdges()).hasSize(2);
        assertThat(segmentE.getOutgoingEdges()).hasSize(1);
        assertThat(segmentI.getIncomingEdges()).hasSize(1);

        final AggregateSegment aggregateSegment = (AggregateSegment) segmentE.getOutgoingEdges().iterator()
                .next().getTo();
        assertThat(subgraph.getNodes()).containsExactlyInAnyOrder(segmentA, segmentB, segmentC, segmentD, segmentE,
                segmentF, aggregateSegment, segmentI, segmentJ, segmentK);
        assertThat(aggregateSegment.getSegments()).containsExactlyInAnyOrder(segmentG, segmentH);
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
