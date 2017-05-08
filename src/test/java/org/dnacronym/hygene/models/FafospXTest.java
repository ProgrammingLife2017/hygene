package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for FAFOSP.
 */
class FafospXTest {
    @Test
    void testNoNeighboursEvenLength() {
        final SequenceNode node = new SequenceNode("1", "123456");

        node.fafospX();

        assertThat(node.getHorizontalRightEnd()).isEqualTo(6);
    }

    @Test
    void testOneNeighbour() {
        final SequenceNode nodeA = new SequenceNode("1", "1234");
        final SequenceNode nodeB = new SequenceNode("2", "1234567");
        nodeB.linkToLeftNeighbour(nodeA);

        new SequenceGraph(Arrays.asList(nodeA, nodeB));

        assertThat(nodeA.getHorizontalRightEnd()).isEqualTo(4);
        assertThat(nodeB.getHorizontalRightEnd()).isEqualTo(11);
    }

    @Test
    void testTwoNeighbours() {
        final SequenceNode nodeA = new SequenceNode("1", "123");
        final SequenceNode nodeB = new SequenceNode("2", "1234567890123");
        final SequenceNode nodeC = new SequenceNode("3", "1234");
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeC);

        new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC));

        assertThat(nodeA.getHorizontalRightEnd()).isEqualTo(3);
        assertThat(nodeB.getHorizontalRightEnd()).isEqualTo(13);
        assertThat(nodeC.getHorizontalRightEnd()).isEqualTo(17);
    }

    @Test
    void testChainOfThree() {
        final SequenceNode nodeA = new SequenceNode("1", "123456789");
        final SequenceNode nodeB = new SequenceNode("2", "1234567890123456789");
        final SequenceNode nodeC = new SequenceNode("3", "12345");
        nodeA.linkToRightNeighbour(nodeB);
        nodeB.linkToRightNeighbour(nodeC);

        new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC));

        assertThat(nodeA.getHorizontalRightEnd()).isEqualTo(9);
        assertThat(nodeB.getHorizontalRightEnd()).isEqualTo(28);
        assertThat(nodeC.getHorizontalRightEnd()).isEqualTo(33);
    }

    /**
     * Verifies correct behaviour when a node is visited twice in a depth-first order.
     */
    @Test
    void testDepthFirstVisitTwice() {
        final SequenceNode nodeA = new SequenceNode("1", "1234567");
        final SequenceNode nodeB = new SequenceNode("2", "12345");
        final SequenceNode nodeC = new SequenceNode("3", "12345678901234");
        final SequenceNode nodeD = new SequenceNode("4", "123456789012");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeC.linkToRightNeighbour(nodeD);

        new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD));

        assertThat(nodeA.getHorizontalRightEnd()).isEqualTo(7);
        assertThat(nodeB.getHorizontalRightEnd()).isEqualTo(12);
        assertThat(nodeC.getHorizontalRightEnd()).isEqualTo(21);
        assertThat(nodeD.getHorizontalRightEnd()).isEqualTo(33);
    }

    /**
     * Verifies correct behaviour when a node is visited twice in a breadth-first order.
     */
    @Test
    void testBreadthFirstVisitTwice() {
        final SequenceNode nodeA = new SequenceNode("1", "12345678901234");
        final SequenceNode nodeB = new SequenceNode("2", "123456789012345");
        final SequenceNode nodeC = new SequenceNode("3", "12345678");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeC);

        new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC));

        assertThat(nodeA.getHorizontalRightEnd()).isEqualTo(14);
        assertThat(nodeB.getHorizontalRightEnd()).isEqualTo(29);
        assertThat(nodeC.getHorizontalRightEnd()).isEqualTo(37);
    }
}
