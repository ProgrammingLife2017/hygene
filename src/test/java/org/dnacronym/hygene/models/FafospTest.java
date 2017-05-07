package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for FAFOSP.
 */
class FafospTest {
    @Test
    void testXNoNeighboursEvenLength() {
        final SequenceNode node = new SequenceNode("1", "123456");

        node.fafospX();

        assertThat(node.getHorizontalPosition()).isEqualTo(3);
    }

    @Test
    void testXNoNeighboursOddLength() {
        final SequenceNode node = new SequenceNode("1", "12345");

        node.fafospX();

        assertThat(node.getHorizontalPosition()).isEqualTo(2);
    }

    @Test
    void testXOneNeighbour() {
        final SequenceNode nodeA = new SequenceNode("1", "1234");
        final SequenceNode nodeB = new SequenceNode("2", "1234567");
        nodeB.linkToLeftNeighbour(nodeA);

        nodeB.fafospX();

        assertThat(nodeA.getHorizontalPosition()).isEqualTo(2);
        assertThat(nodeB.getHorizontalPosition()).isEqualTo(7);
    }

    @Test
    void testXTwoNeighbours() {
        final SequenceNode nodeA = new SequenceNode("1", "123");
        final SequenceNode nodeB = new SequenceNode("2", "1234567890123");
        final SequenceNode nodeC = new SequenceNode("3", "1234");
        nodeC.linkToLeftNeighbour(nodeA);
        nodeC.linkToLeftNeighbour(nodeB);

        nodeC.fafospX();

        assertThat(nodeA.getHorizontalPosition()).isEqualTo(1);
        assertThat(nodeB.getHorizontalPosition()).isEqualTo(6);
        assertThat(nodeC.getHorizontalPosition()).isEqualTo(15);
    }

    @Test
    void testXChainOfThree() {
        final SequenceNode nodeA = new SequenceNode("1", "123456789");
        final SequenceNode nodeB = new SequenceNode("2", "1234567890123456789");
        final SequenceNode nodeC = new SequenceNode("3", "12345");
        nodeB.linkToLeftNeighbour(nodeA);
        nodeC.linkToLeftNeighbour(nodeB);

        nodeC.fafospX();

        assertThat(nodeA.getHorizontalPosition()).isEqualTo(4);
        assertThat(nodeB.getHorizontalPosition()).isEqualTo(18);
        assertThat(nodeC.getHorizontalPosition()).isEqualTo(30);
    }

    @Test
    void testXDepthFirstVisitTwice() {
        final SequenceNode nodeA = new SequenceNode("1", "1234567");
        final SequenceNode nodeB = new SequenceNode("2", "12345");
        final SequenceNode nodeC = new SequenceNode("3", "12345678901234");
        final SequenceNode nodeD = new SequenceNode("4", "123456789012");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeC.linkToRightNeighbour(nodeD);

        nodeD.fafospX();

        assertThat(nodeA.getHorizontalPosition()).isEqualTo(3);
        assertThat(nodeB.getHorizontalPosition()).isEqualTo(9);
        assertThat(nodeC.getHorizontalPosition()).isEqualTo(14);
        assertThat(nodeD.getHorizontalPosition()).isEqualTo(27);
    }
}
