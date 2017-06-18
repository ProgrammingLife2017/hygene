package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.edge.DummyEdge;
import org.dnacronym.hygene.graph.node.DummyNode;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.edge.Link;
import org.dnacronym.hygene.graph.node.LayoutableNode;
import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.graph.Subgraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link FafospLayerer}.
 */
final class FafospLayererTest {
    private Subgraph subgraph;
    private FafospLayerer layerer;


    @BeforeEach
    void beforeEach() {
        subgraph = new Subgraph();
        layerer = new FafospLayerer();
    }


    @Test
    void testEmptySubgraph() {
        final LayoutableNode[][] layout = layerer.layer(subgraph);

        assertThat(layout).isEmpty();
    }


    /*
     * Layer allocation
     */

    /**
     * Tests that the number of layers is zero if the only node is at position 0.
     */
    @Test
    void testSizeOneWithOneNode() {
        final Node node = new Segment(84, 34, 36);
        addAtPosition(node, 0);

        final LayoutableNode[][] layout = layerer.layer(subgraph);

        assertThat(layout).hasSize(2);
        assertThat(layout[0]).isEmpty();
        assertThat(layout[1]).containsExactlyInAnyOrder(node);
    }

    /**
     * Tests that the number of layers is zero if all nodes are at position 0.
     */
    @Test
    void testSizeOneWithMultipleNodes() {
        final Node nodeA = new Segment(100, 2, 37);
        final Node nodeB = new Segment(23, 10, 30);
        addAtPosition(nodeA, 0);
        addAtPosition(nodeB, 0);

        final LayoutableNode[][] layout = layerer.layer(subgraph);

        assertThat(layout).hasSize(2);
        assertThat(layout[0]).isEmpty();
        assertThat(layout[1]).containsExactlyInAnyOrder(nodeA, nodeB);
    }

    /**
     * Tests that the number of layers is two if the only node is in the second column.
     */
    @Test
    void testSizeTwoWithOneNode() {
        final Node node = new Segment(95, 44, 1065);
        addAtPosition(node, 69);

        final LayoutableNode[][] layout = layerer.layer(subgraph);

        assertThat(layout).hasSize(2);
        assertThat(layout[0]).isEmpty();
        assertThat(layout[1]).containsExactlyInAnyOrder(node);
    }

    /**
     * Tests that the number of layers is two if all nodes are in the second column.
     */
    @Test
    void testSizeTwoWithMultipleNodes() {
        final Node nodeA = new Segment(30, 62, 1089);
        final Node nodeB = new Segment(96, 61, 1077);
        addAtPosition(nodeA, 76);
        addAtPosition(nodeB, 57);

        final LayoutableNode[][] layout = layerer.layer(subgraph);

        assertThat(layout).hasSize(2);
        assertThat(layout[0]).isEmpty();
        assertThat(layout[1]).containsExactlyInAnyOrder(nodeA, nodeB);
    }

    /**
     * Tests that the number of layers is two if one node is in the second column.
     */
    @Test
    void testSizeTwoWithMultipleDifferentNodes() {
        final Node nodeA = new Segment(41, 26, 90);
        final Node nodeB = new Segment(70, 71, 58);
        final Node nodeC = new Segment(90, 90, 1056);
        addAtPosition(nodeA, 0);
        addAtPosition(nodeB, 0);
        addAtPosition(nodeC, 72);

        final LayoutableNode[][] layout = layerer.layer(subgraph);

        assertThat(layout).hasSize(3);
        assertThat(layout[0]).isEmpty();
        assertThat(layout[1]).containsExactlyInAnyOrder(nodeA, nodeB);
        assertThat(layout[2]).containsExactlyInAnyOrder(nodeC);
    }

    /**
     * Tests that two layers are allocated if a node spans two layers.
     */
    @Test
    void testSizeNodeSpan() {
        final Node node = new Segment(39, 76, 1599);
        addAtPosition(node, 0);

        final LayoutableNode[][] layout = layerer.layer(subgraph);

        assertThat(layout).hasSize(3);
        assertThat(layout[0]).isEmpty();
        assertThat(layout[1]).containsExactlyInAnyOrder(node);
        assertThat(layout[2]).containsExactlyInAnyOrder(node);
    }


    /*
     * Dummy nodes
     */

    /**
     * Tests that an inserted dummy node connects the old nodes with two dummy edges.
     */
    @Test
    void testSimpleDummyEdge() {
        final Node nodeA = new Segment(66, 72, 32);
        final Node nodeB = new Segment(2, 97, 93);
        addEdge(nodeA, nodeB);
        addAtPosition(nodeA, 0);
        addAtPosition(nodeB, 2000);

        layerer.layer(subgraph);
        final Node dummy = getNextDummy(nodeA);

        assertThat(nodeA.getOutgoingEdges()).hasSize(1);
        assertThat(nodeB.getIncomingEdges()).hasSize(1);

        assertThatNodeIsDummy(dummy);
        assertThat(getNextDummy(dummy)).isEqualTo(nodeB);
    }

    /**
     * Tests that two nodes that are far apart are connected by multiple dummy nodes and edges.
     */
    @Test
    void testLongDummyEdge() {
        final Node nodeA = new Segment(29, 75, 27);
        final Node nodeB = new Segment(23, 52, 4);
        addEdge(nodeA, nodeB);
        addAtPosition(nodeA, 0);
        addAtPosition(nodeB, 5000);

        layerer.layer(subgraph);
        final Node dummyA = getNextDummy(nodeA);
        final Node dummyB = getNextDummy(dummyA);
        final Node dummyC = getNextDummy(dummyB);
        final Node dummyD = getNextDummy(dummyC);

        assertThat(nodeA.getOutgoingEdges()).hasSize(1);
        assertThat(nodeB.getIncomingEdges()).hasSize(1);

        assertThatNodeIsDummy(dummyA);
        assertThatNodeIsDummy(dummyB);
        assertThatNodeIsDummy(dummyC);
        assertThatNodeIsDummy(dummyD);
        assertThat(getNextDummy(dummyD)).isEqualTo(nodeB);
    }

    @Test
    void testMultipleDummyEdge() {
        final Node nodeA = new Segment(78, 46, 46);
        final Node nodeB = new Segment(66, 28, 14);
        final Node nodeC = new Segment(50, 87, 2);
        final Node nodeD = new Segment(45, 20, 48);
        addEdge(nodeA, nodeB);
        addEdge(nodeA, nodeC);
        addEdge(nodeA, nodeD);
        addAtPosition(nodeA, 0);
        addAtPosition(nodeB, 3000);
        addAtPosition(nodeC, 2000);
        addAtPosition(nodeD, 3000);

        layerer.layer(subgraph);
        final Node dummyB1 = getPreviousDummy(nodeB);
        final Node dummyB2 = getPreviousDummy(dummyB1);
        final Node dummyC1 = getPreviousDummy(nodeC);
        final Node dummyD1 = getPreviousDummy(nodeD);
        final Node dummyD2 = getPreviousDummy(dummyD1);

        assertThat(nodeA.getOutgoingEdges()).hasSize(3);
        assertThat(nodeB.getIncomingEdges()).hasSize(1);
        assertThat(nodeC.getIncomingEdges()).hasSize(1);
        assertThat(nodeD.getIncomingEdges()).hasSize(1);

        assertThatNodeIsDummy(dummyB1);
        assertThatNodeIsDummy(dummyB2);
        assertThat(getPreviousDummy(dummyB2)).isEqualTo(nodeA);

        assertThatNodeIsDummy(dummyC1);
        assertThat(getPreviousDummy(dummyC1)).isEqualTo(nodeA);

        assertThatNodeIsDummy(dummyD1);
        assertThatNodeIsDummy(dummyD2);
        assertThat(getPreviousDummy(dummyD2)).isEqualTo(nodeA);
    }

    @Test
    void testSubstitutionDummyEdge() {
        final Node nodeA = new Segment(71, 5, 68);
        final Node nodeB = new Segment(18, 99, 54);
        final Node nodeC = new Segment(35, 76, 40);
        final Node nodeD = new Segment(63, 32, 36);
        addEdge(nodeA, nodeB);
        addEdge(nodeA, nodeC);
        addEdge(nodeB, nodeD);
        addEdge(nodeC, nodeD);
        addAtPosition(nodeA, 0);
        addAtPosition(nodeB, 3000);
        addAtPosition(nodeC, 2000);
        addAtPosition(nodeD, 5000);

        layerer.layer(subgraph);
        final Node dummyAB1 = getPreviousDummy(nodeB);
        final Node dummyAB2 = getPreviousDummy(dummyAB1);
        final Node dummyAC = getPreviousDummy(nodeC);
        final Node dummyBD = getNextDummy(nodeB);
        final Node dummyCD1 = getNextDummy(nodeC);
        final Node dummyCD2 = getNextDummy(dummyCD1);

        assertThat(nodeA.getOutgoingEdges()).hasSize(2);
        assertThat(nodeB.getIncomingEdges()).hasSize(1);
        assertThat(nodeB.getOutgoingEdges()).hasSize(1);
        assertThat(nodeC.getIncomingEdges()).hasSize(1);
        assertThat(nodeC.getOutgoingEdges()).hasSize(1);
        assertThat(nodeD.getIncomingEdges()).hasSize(2);

        assertThat(getPreviousDummy(dummyAB2)).isEqualTo(nodeA);
        assertThatNodeIsDummy(dummyAB2);
        assertThatNodeIsDummy(dummyAB1);

        assertThat(getPreviousDummy(dummyAC)).isEqualTo(nodeA);
        assertThatNodeIsDummy(dummyAC);

        assertThatNodeIsDummy(dummyBD);
        assertThat(getNextDummy(dummyBD)).isEqualTo(nodeD);

        assertThatNodeIsDummy(dummyCD1);
        assertThatNodeIsDummy(dummyCD2);
        assertThat(getNextDummy(dummyCD2)).isEqualTo(nodeD);
    }



    /*
     * Helper methods
     */

    /**
     * Adds the given {@link Node} to the {@link Subgraph} and sets the given horizontal position.
     *
     * @param node      a {@link Node}
     * @param xPosition the new horizontal position for the {@link Node}
     */
    private void addAtPosition(final Node node, final int xPosition) {
        node.setXPosition(xPosition);
        subgraph.add(node);
    }

    /**
     * Adds an edge between the given {@link Node}s.
     *
     * @param from the {@link Node} the edge leaves from
     * @param to   the {@link Node} the edge arrives at
     */
    private void addEdge(final Node from, final Node to) {
        final Edge edge = new Link(from, to, 17);
        from.getOutgoingEdges().add(edge);
        to.getIncomingEdges().add(edge);
    }

    /**
     * Returns the first outgoing neighbour of the given {@link Node}.
     *
     * @param node a {@link Node}
     * @return the first outgoing neighbour of the given {@link Node}
     */
    private Node getNextDummy(final Node node) {
        assert node.getOutgoingEdges().size() == 1;

        return node.getOutgoingEdges().iterator().next().getTo();
    }

    /**
     * Returns the first incoming neighbour of the given {@link Node}.
     *
     * @param node a {@link Node}
     * @return the first incoming neighbour of the given {@link Node}
     */
    private Node getPreviousDummy(final Node node) {
        assert node.getIncomingEdges().size() == 1;

        return node.getIncomingEdges().iterator().next().getFrom();
    }

    /**
     * Asserts the invariants that apply to dummy nodes.
     *
     * @param node a {@link Node}
     */
    private void assertThatNodeIsDummy(final Node node) {
        assertThat(node).isExactlyInstanceOf(DummyNode.class);
        assertThat(node.getIncomingEdges()).hasSize(1);
        assertThat(node.getOutgoingEdges()).hasSize(1);
        assertThat(node.getIncomingEdges().iterator().next()).isExactlyInstanceOf(DummyEdge.class);
        assertThat(node.getOutgoingEdges().iterator().next()).isExactlyInstanceOf(DummyEdge.class);
        assertThat(node.getIncomingEdges().iterator().next().getTo()).isEqualTo(node);
        assertThat(node.getOutgoingEdges().iterator().next().getFrom()).isEqualTo(node);
    }
}
