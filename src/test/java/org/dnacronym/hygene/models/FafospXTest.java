package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for FAFOSP-X.
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

        assertThat(nodeA.getHorizontalRightEnd()).isEqualTo(1 + 4);
        assertThat(nodeB.getHorizontalRightEnd()).isEqualTo(1 + 11 + 1);
    }

    @Test
    void testTwoNeighbours() {
        final SequenceNode nodeA = new SequenceNode("1", "123");
        final SequenceNode nodeB = new SequenceNode("2", "1234567890123");
        final SequenceNode nodeC = new SequenceNode("3", "1234");
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeC);

        new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC));

        assertThat(nodeA.getHorizontalRightEnd()).isEqualTo(1 + 3);
        assertThat(nodeB.getHorizontalRightEnd()).isEqualTo(1 + 13);
        assertThat(nodeC.getHorizontalRightEnd()).isEqualTo(1 + 17 + 1);
    }

    @Test
    void testChainOfThree() {
        final SequenceNode nodeA = new SequenceNode("1", "123456789");
        final SequenceNode nodeB = new SequenceNode("2", "1234567890123456789");
        final SequenceNode nodeC = new SequenceNode("3", "12345");
        nodeA.linkToRightNeighbour(nodeB);
        nodeB.linkToRightNeighbour(nodeC);

        new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC));

        assertThat(nodeA.getHorizontalRightEnd()).isEqualTo(1 + 9);
        assertThat(nodeB.getHorizontalRightEnd()).isEqualTo(1 + 28 + 1);
        assertThat(nodeC.getHorizontalRightEnd()).isEqualTo(1 + 33 + 2);
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

        assertThat(nodeA.getHorizontalRightEnd()).isEqualTo(1 + 7);
        assertThat(nodeB.getHorizontalRightEnd()).isEqualTo(1 + 12 + 1);
        assertThat(nodeC.getHorizontalRightEnd()).isEqualTo(1 + 21 + 1);
        assertThat(nodeD.getHorizontalRightEnd()).isEqualTo(1 + 33 + 2);
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

        assertThat(nodeA.getHorizontalRightEnd()).isEqualTo(1 + 14);
        assertThat(nodeB.getHorizontalRightEnd()).isEqualTo(1 + 29 + 1);
        assertThat(nodeC.getHorizontalRightEnd()).isEqualTo(1 + 37 + 2);
    }

    @Test
    void testInsertionBubble() {
        final SequenceNode nodeA = new SequenceNode("1", "123");
        final SequenceNode nodeB = new SequenceNode("2", "12345");
        final SequenceNode nodeC = new SequenceNode("3", "1234567");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeC.linkToRightNeighbour(nodeB);

        new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC));

        assertThat(nodeA.getHorizontalRightEnd()).isEqualTo(1 + 3);
        assertThat(nodeB.getHorizontalRightEnd()).isEqualTo(1 + 15 + 2);
        assertThat(nodeC.getHorizontalRightEnd()).isEqualTo(1 + 10 + 1);
    }
}
