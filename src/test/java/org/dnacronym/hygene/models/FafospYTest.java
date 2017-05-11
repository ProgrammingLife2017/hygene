package org.dnacronym.hygene.models;

import org.dnacronym.hygene.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for FAFOSP-Y.
 */
class FafospYTest {
    /*
     * Default values.
     */
    @Test
    void testDefaultHeights() {
        final SequenceNode node = new SequenceNode("A", "A");

        assertThat(node.getLeftHeight()).isEqualTo(-1);
        assertThat(node.getRightHeight()).isEqualTo(-1);
        assertThat(node.getMaxHeight()).isEqualTo(-1);
        assertThat(node.getVerticalPosition()).isEqualTo(-1);
    }

    /*
     * Left height.
     */
    @Test
    void testLeftHeightNoNeighbours() {
        final SequenceNode node = new SequenceNode("A", "A");

        node.fafospYInit(SequenceDirection.LEFT);

        assertThat(node.getLeftHeight()).isEqualTo(2);
    }

    @Test
    void testLeftHeightSingleNeighbour() {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        nodeA.linkToLeftNeighbour(nodeB);

        nodeB.fafospYInit(SequenceDirection.LEFT);
        nodeA.fafospYInit(SequenceDirection.LEFT);

        assertThat(nodeA.getLeftHeight()).isEqualTo(2);
        assertThat(nodeB.getLeftHeight()).isEqualTo(2);
    }

    @Test
    void testLeftHeightTwoNeighbours() {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        nodeA.linkToLeftNeighbour(nodeB);
        nodeA.linkToLeftNeighbour(nodeC);

        nodeC.fafospYInit(SequenceDirection.LEFT);
        nodeB.fafospYInit(SequenceDirection.LEFT);
        nodeA.fafospYInit(SequenceDirection.LEFT);

        assertThat(nodeA.getLeftHeight()).isEqualTo(4);
        assertThat(nodeB.getLeftHeight()).isEqualTo(2);
        assertThat(nodeC.getLeftHeight()).isEqualTo(2);
    }

    @Test
    void testLeftHeightTwoNeighboursAndOneNeighbourAlsoHasTwoNeighbours() {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        final SequenceNode nodeD = new SequenceNode("D", "D");
        final SequenceNode nodeE = new SequenceNode("E", "E");
        nodeA.linkToLeftNeighbour(nodeB);
        nodeA.linkToLeftNeighbour(nodeC);
        nodeB.linkToLeftNeighbour(nodeD);
        nodeB.linkToLeftNeighbour(nodeE);

        nodeE.fafospYInit(SequenceDirection.LEFT);
        nodeD.fafospYInit(SequenceDirection.LEFT);
        nodeC.fafospYInit(SequenceDirection.LEFT);
        nodeB.fafospYInit(SequenceDirection.LEFT);
        nodeA.fafospYInit(SequenceDirection.LEFT);

        assertThat(nodeA.getLeftHeight()).isEqualTo(6);
        assertThat(nodeB.getLeftHeight()).isEqualTo(4);
        assertThat(nodeC.getLeftHeight()).isEqualTo(2);
        assertThat(nodeD.getLeftHeight()).isEqualTo(2);
        assertThat(nodeE.getLeftHeight()).isEqualTo(2);
    }

    @Test
    void testLeftHeightSharedNeighbour() {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        final SequenceNode nodeD = new SequenceNode("D", "D");
        nodeA.linkToLeftNeighbour(nodeB);
        nodeA.linkToLeftNeighbour(nodeC);
        nodeB.linkToLeftNeighbour(nodeD);
        nodeC.linkToLeftNeighbour(nodeD);

        nodeD.fafospYInit(SequenceDirection.LEFT);
        nodeC.fafospYInit(SequenceDirection.LEFT);
        nodeB.fafospYInit(SequenceDirection.LEFT);
        nodeA.fafospYInit(SequenceDirection.LEFT);

        assertThat(nodeA.getLeftHeight()).isEqualTo(4);
        assertThat(nodeB.getLeftHeight()).isEqualTo(2);
        assertThat(nodeC.getLeftHeight()).isEqualTo(2);
        assertThat(nodeD.getLeftHeight()).isEqualTo(2);
    }

    @Test
    void testLeftHeightSequentialSplits() {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        final SequenceNode nodeD = new SequenceNode("D", "D");
        final SequenceNode nodeE = new SequenceNode("E", "E");
        final SequenceNode nodeF = new SequenceNode("F", "F");
        final SequenceNode nodeG = new SequenceNode("G", "G");
        nodeA.linkToLeftNeighbour(nodeB);
        nodeA.linkToLeftNeighbour(nodeC);
        nodeB.linkToLeftNeighbour(nodeD);
        nodeC.linkToLeftNeighbour(nodeD);
        nodeD.linkToLeftNeighbour(nodeE);
        nodeD.linkToLeftNeighbour(nodeF);
        nodeE.linkToLeftNeighbour(nodeG);
        nodeF.linkToLeftNeighbour(nodeG);

        nodeG.fafospYInit(SequenceDirection.LEFT);
        nodeF.fafospYInit(SequenceDirection.LEFT);
        nodeE.fafospYInit(SequenceDirection.LEFT);
        nodeD.fafospYInit(SequenceDirection.LEFT);
        nodeC.fafospYInit(SequenceDirection.LEFT);
        nodeB.fafospYInit(SequenceDirection.LEFT);
        nodeA.fafospYInit(SequenceDirection.LEFT);

        assertThat(nodeA.getLeftHeight()).isEqualTo(4);
        assertThat(nodeB.getLeftHeight()).isEqualTo(2);
        assertThat(nodeC.getLeftHeight()).isEqualTo(2);
        assertThat(nodeD.getLeftHeight()).isEqualTo(4);
        assertThat(nodeE.getLeftHeight()).isEqualTo(2);
        assertThat(nodeF.getLeftHeight()).isEqualTo(2);
        assertThat(nodeG.getLeftHeight()).isEqualTo(2);
    }

    /*
     * Right height.
     */
    @Test
    void testRightHeightNoNeighbours() {
        final SequenceNode node = new SequenceNode("A", "A");

        node.fafospYInit(SequenceDirection.RIGHT);

        assertThat(node.getRightHeight()).isEqualTo(2);
    }

    @Test
    void testRightHeightSingleNeighbour() {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        nodeA.linkToRightNeighbour(nodeB);

        nodeB.fafospYInit(SequenceDirection.RIGHT);
        nodeA.fafospYInit(SequenceDirection.RIGHT);

        assertThat(nodeA.getRightHeight()).isEqualTo(2);
        assertThat(nodeB.getRightHeight()).isEqualTo(2);
    }

    @Test
    void testRightHeightTwoNeighbours() {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);

        nodeC.fafospYInit(SequenceDirection.RIGHT);
        nodeB.fafospYInit(SequenceDirection.RIGHT);
        nodeA.fafospYInit(SequenceDirection.RIGHT);

        assertThat(nodeA.getRightHeight()).isEqualTo(4);
        assertThat(nodeB.getRightHeight()).isEqualTo(2);
        assertThat(nodeC.getRightHeight()).isEqualTo(2);
    }

    @Test
    void testRightHeightTwoNeighboursAndOneNeighbourAlsoHasTwoNeighbours() {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        final SequenceNode nodeD = new SequenceNode("D", "D");
        final SequenceNode nodeE = new SequenceNode("E", "E");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeB.linkToRightNeighbour(nodeE);

        nodeE.fafospYInit(SequenceDirection.RIGHT);
        nodeD.fafospYInit(SequenceDirection.RIGHT);
        nodeC.fafospYInit(SequenceDirection.RIGHT);
        nodeB.fafospYInit(SequenceDirection.RIGHT);
        nodeA.fafospYInit(SequenceDirection.RIGHT);

        assertThat(nodeA.getRightHeight()).isEqualTo(6);
        assertThat(nodeB.getRightHeight()).isEqualTo(4);
        assertThat(nodeC.getRightHeight()).isEqualTo(2);
        assertThat(nodeD.getRightHeight()).isEqualTo(2);
        assertThat(nodeE.getRightHeight()).isEqualTo(2);
    }

    @Test
    void testRightHeightSharedNeighbour() {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        final SequenceNode nodeD = new SequenceNode("D", "D");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeC.linkToRightNeighbour(nodeD);

        nodeD.fafospYInit(SequenceDirection.RIGHT);
        nodeC.fafospYInit(SequenceDirection.RIGHT);
        nodeB.fafospYInit(SequenceDirection.RIGHT);
        nodeA.fafospYInit(SequenceDirection.RIGHT);

        assertThat(nodeA.getRightHeight()).isEqualTo(4);
        assertThat(nodeB.getRightHeight()).isEqualTo(2);
        assertThat(nodeC.getRightHeight()).isEqualTo(2);
        assertThat(nodeD.getRightHeight()).isEqualTo(2);
    }

    @Test
    void testRightHeightSequentialSplits() {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        final SequenceNode nodeD = new SequenceNode("D", "D");
        final SequenceNode nodeE = new SequenceNode("E", "E");
        final SequenceNode nodeF = new SequenceNode("F", "F");
        final SequenceNode nodeG = new SequenceNode("G", "G");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeC.linkToRightNeighbour(nodeD);
        nodeD.linkToRightNeighbour(nodeE);
        nodeD.linkToRightNeighbour(nodeF);
        nodeE.linkToRightNeighbour(nodeG);
        nodeF.linkToRightNeighbour(nodeG);

        nodeG.fafospYInit(SequenceDirection.RIGHT);
        nodeF.fafospYInit(SequenceDirection.RIGHT);
        nodeE.fafospYInit(SequenceDirection.RIGHT);
        nodeD.fafospYInit(SequenceDirection.RIGHT);
        nodeC.fafospYInit(SequenceDirection.RIGHT);
        nodeB.fafospYInit(SequenceDirection.RIGHT);
        nodeA.fafospYInit(SequenceDirection.RIGHT);

        assertThat(nodeA.getRightHeight()).isEqualTo(4);
        assertThat(nodeB.getRightHeight()).isEqualTo(2);
        assertThat(nodeC.getRightHeight()).isEqualTo(2);
        assertThat(nodeD.getRightHeight()).isEqualTo(4);
        assertThat(nodeE.getRightHeight()).isEqualTo(2);
        assertThat(nodeF.getRightHeight()).isEqualTo(2);
        assertThat(nodeG.getRightHeight()).isEqualTo(2);
    }

    /*
     * Max height.
     */
    @Test
    void testGetSetMaxHeight() {
        final SequenceNode node = new SequenceNode("A", "A");

        node.setMaxHeight(1197);

        assertThat(node.getMaxHeight()).isEqualTo(1197);
    }

    @Test
    void testMaxHeightEmptyGraph() {
        final SequenceGraph graph = new SequenceGraph(new ArrayList<>());

        assertThat(graph.getSourceNode().getMaxHeight()).isEqualTo(2);
        assertThat(graph.getSinkNode().getMaxHeight()).isEqualTo(2);
    }

    @Test
    void testMaxHeightDisconnectedNodes() {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");

        // This modifies the nodes
        new SequenceGraph(Arrays.asList(nodeA, nodeB));

        assertThat(nodeA.getMaxHeight()).isEqualTo(4);
        assertThat(nodeB.getMaxHeight()).isEqualTo(4);
    }

    /*
     * Vertical position.
     */
    @Test
    void testPositionEmptyGraph() {
        final SequenceGraph graph = new SequenceGraph(new ArrayList<>());

        assertThat(graph.getSourceNode().getVerticalPosition()).isEqualTo(1);
        assertThat(graph.getSinkNode().getVerticalPosition()).isEqualTo(1);
    }

    @Test
    void testPositionSingleNode() {
        final SequenceNode node = new SequenceNode("A", "A");

        // This modifies the node
        new SequenceGraph(Collections.singletonList(node));

        assertThat(node.getVerticalPosition()).isEqualTo(1);
    }

    @Test
    void testPositionDiamond() throws ParseException {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        final SequenceNode nodeD = new SequenceNode("D", "D");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeC.linkToRightNeighbour(nodeD);

        // This modifies the nodes
        new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD));

        assertThat(nodeA.getVerticalPosition()).isEqualTo(2);
        assertThat(nodeB.getVerticalPosition()).isEqualTo(1);
        assertThat(nodeC.getVerticalPosition()).isEqualTo(3);
        assertThat(nodeD.getVerticalPosition()).isEqualTo(2);
    }

    @Test
    void testFafospDoubleTwoSplit() throws ParseException {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        final SequenceNode nodeD = new SequenceNode("D", "D");
        final SequenceNode nodeE = new SequenceNode("E", "E");
        final SequenceNode nodeF = new SequenceNode("F", "F");
        final SequenceNode nodeG = new SequenceNode("G", "G");
        final SequenceNode nodeH = new SequenceNode("H", "H");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeB.linkToRightNeighbour(nodeE);
        nodeC.linkToRightNeighbour(nodeF);
        nodeC.linkToRightNeighbour(nodeG);
        nodeD.linkToRightNeighbour(nodeH);
        nodeE.linkToRightNeighbour(nodeH);
        nodeF.linkToRightNeighbour(nodeH);
        nodeG.linkToRightNeighbour(nodeH);

        // This modifies the nodes
        new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD, nodeE, nodeF, nodeG, nodeH));

        assertThat(nodeA.getVerticalPosition()).isEqualTo(4);
        assertThat(nodeB.getVerticalPosition()).isEqualTo(2);
        assertThat(nodeC.getVerticalPosition()).isEqualTo(6);
        assertThat(nodeD.getVerticalPosition()).isEqualTo(1);
        assertThat(nodeE.getVerticalPosition()).isEqualTo(3);
        assertThat(nodeF.getVerticalPosition()).isEqualTo(5);
        assertThat(nodeG.getVerticalPosition()).isEqualTo(7);
        assertThat(nodeH.getVerticalPosition()).isEqualTo(4);
    }

    @Test
    void testFafospBidirectionalDoubleTwoSplit() throws ParseException {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        final SequenceNode nodeD = new SequenceNode("D", "D");
        final SequenceNode nodeE = new SequenceNode("E", "E");
        final SequenceNode nodeF = new SequenceNode("F", "F");
        final SequenceNode nodeG = new SequenceNode("G", "G");
        final SequenceNode nodeH = new SequenceNode("H", "H");
        final SequenceNode nodeI = new SequenceNode("I", "I");
        final SequenceNode nodeJ = new SequenceNode("J", "J");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeB.linkToRightNeighbour(nodeE);
        nodeC.linkToRightNeighbour(nodeF);
        nodeC.linkToRightNeighbour(nodeG);
        nodeD.linkToRightNeighbour(nodeH);
        nodeE.linkToRightNeighbour(nodeH);
        nodeF.linkToRightNeighbour(nodeI);
        nodeG.linkToRightNeighbour(nodeI);
        nodeH.linkToRightNeighbour(nodeJ);
        nodeI.linkToRightNeighbour(nodeJ);

        // This modifies the nodes
        new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD, nodeE, nodeF, nodeG, nodeH, nodeI, nodeJ));

        assertThat(nodeA.getVerticalPosition()).isEqualTo(4);
        assertThat(nodeB.getVerticalPosition()).isEqualTo(2);
        assertThat(nodeC.getVerticalPosition()).isEqualTo(6);
        assertThat(nodeD.getVerticalPosition()).isEqualTo(1);
        assertThat(nodeE.getVerticalPosition()).isEqualTo(3);
        assertThat(nodeF.getVerticalPosition()).isEqualTo(5);
        assertThat(nodeG.getVerticalPosition()).isEqualTo(7);
        assertThat(nodeH.getVerticalPosition()).isEqualTo(2);
        assertThat(nodeI.getVerticalPosition()).isEqualTo(6);
        assertThat(nodeJ.getVerticalPosition()).isEqualTo(4);
    }

    @Test
    void testFafospSharedDoubleTwoSplit() throws ParseException {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        final SequenceNode nodeD = new SequenceNode("D", "D");
        final SequenceNode nodeE = new SequenceNode("E", "E");
        final SequenceNode nodeF = new SequenceNode("F", "F");
        final SequenceNode nodeG = new SequenceNode("G", "G");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeB.linkToRightNeighbour(nodeE);
        nodeC.linkToRightNeighbour(nodeE);
        nodeC.linkToRightNeighbour(nodeF);
        nodeD.linkToRightNeighbour(nodeG);
        nodeE.linkToRightNeighbour(nodeG);
        nodeF.linkToRightNeighbour(nodeG);

        // This modifies the nodes
        new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD, nodeE, nodeF, nodeG));

        assertThat(nodeA.getVerticalPosition()).isEqualTo(4);
        assertThat(nodeB.getVerticalPosition()).isEqualTo(2);
        assertThat(nodeC.getVerticalPosition()).isEqualTo(6);
        assertThat(nodeD.getVerticalPosition()).isEqualTo(1);
        assertThat(nodeE.getVerticalPosition()).isEqualTo(4);
        assertThat(nodeF.getVerticalPosition()).isEqualTo(7);
        assertThat(nodeG.getVerticalPosition()).isEqualTo(4);
    }

    @Test
    void testFafospDoubleReversedSplit() throws ParseException {
        final SequenceNode nodeA = new SequenceNode("A", "A");
        final SequenceNode nodeB = new SequenceNode("B", "B");
        final SequenceNode nodeC = new SequenceNode("C", "C");
        final SequenceNode nodeD = new SequenceNode("D", "D");
        final SequenceNode nodeE = new SequenceNode("E", "E");
        final SequenceNode nodeF = new SequenceNode("F", "F");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeC.linkToRightNeighbour(nodeD);
        nodeC.linkToRightNeighbour(nodeE);
        nodeD.linkToRightNeighbour(nodeF);
        nodeE.linkToRightNeighbour(nodeF);

        // This modifies the nodes
        new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD, nodeE, nodeF));

        assertThat(nodeA.getVerticalPosition()).isEqualTo(3);
        assertThat(nodeB.getVerticalPosition()).isEqualTo(1);
        assertThat(nodeC.getVerticalPosition()).isEqualTo(4);
        assertThat(nodeD.getVerticalPosition()).isEqualTo(2);
        assertThat(nodeE.getVerticalPosition()).isEqualTo(5);
        assertThat(nodeF.getVerticalPosition()).isEqualTo(3);
    }
}
