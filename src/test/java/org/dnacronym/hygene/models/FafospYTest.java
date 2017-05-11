package org.dnacronym.hygene.models;

import org.dnacronym.hygene.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for FAFOSP-Y.
 */
class FafospYTest {
    private SequenceNode[] nodes;


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
        createNodes(1);

        initNodes(SequenceDirection.LEFT);

        assertNodes(SequenceNode::getLeftHeight, new int[] {2});
    }

    @Test
    void testLeftHeightSingleNeighbour() {
        createNodes(2);
        nodes[0].linkToLeftNeighbour(nodes[1]);

        initNodes(SequenceDirection.LEFT);

        assertNodes(SequenceNode::getLeftHeight, new int[] {2, 2});
    }

    @Test
    void testLeftHeightTwoNeighbours() {
        createNodes(3);
        nodes[0].linkToLeftNeighbour(nodes[1]);
        nodes[0].linkToLeftNeighbour(nodes[2]);

        initNodes(SequenceDirection.LEFT);

        assertNodes(SequenceNode::getLeftHeight, new int[] {4, 2, 2});
    }

    @Test
    void testLeftHeightTwoNeighboursAndOneNeighbourAlsoHasTwoNeighbours() {
        createNodes(5);
        nodes[0].linkToLeftNeighbour(nodes[1]);
        nodes[0].linkToLeftNeighbour(nodes[2]);
        nodes[1].linkToLeftNeighbour(nodes[3]);
        nodes[1].linkToLeftNeighbour(nodes[4]);

        initNodes(SequenceDirection.LEFT);

        assertNodes(SequenceNode::getLeftHeight, new int[] {6, 4, 2, 2, 2});
    }

    @Test
    void testLeftHeightSharedNeighbour() {
        createNodes(4);
        nodes[0].linkToLeftNeighbour(nodes[1]);
        nodes[0].linkToLeftNeighbour(nodes[2]);
        nodes[1].linkToLeftNeighbour(nodes[3]);
        nodes[2].linkToLeftNeighbour(nodes[3]);

        initNodes(SequenceDirection.LEFT);

        assertNodes(SequenceNode::getLeftHeight, new int[] {4, 2, 2, 2});
    }

    @Test
    void testLeftHeightSequentialSplits() {
        createNodes(7);
        nodes[0].linkToLeftNeighbour(nodes[1]);
        nodes[0].linkToLeftNeighbour(nodes[2]);
        nodes[1].linkToLeftNeighbour(nodes[3]);
        nodes[2].linkToLeftNeighbour(nodes[3]);
        nodes[3].linkToLeftNeighbour(nodes[4]);
        nodes[3].linkToLeftNeighbour(nodes[5]);
        nodes[4].linkToLeftNeighbour(nodes[6]);
        nodes[5].linkToLeftNeighbour(nodes[6]);

        initNodes(SequenceDirection.LEFT);

        assertNodes(SequenceNode::getLeftHeight, new int[] {4, 2, 2, 4, 2, 2, 2});
    }

    /*
     * Right height.
     */
    @Test
    void testRightHeightNoNeighbours() {
        createNodes(1);

        initNodes(SequenceDirection.RIGHT);

        assertNodes(SequenceNode::getRightHeight, new int[] {2});
    }

    @Test
    void testRightHeightSingleNeighbour() {
        createNodes(2);
        nodes[0].linkToRightNeighbour(nodes[1]);

        initNodes(SequenceDirection.RIGHT);

        assertNodes(SequenceNode::getRightHeight, new int[] {2, 2});
    }

    @Test
    void testRightHeightTwoNeighbours() {
        createNodes(3);
        nodes[0].linkToRightNeighbour(nodes[1]);
        nodes[0].linkToRightNeighbour(nodes[2]);

        initNodes(SequenceDirection.RIGHT);

        assertNodes(SequenceNode::getRightHeight, new int[] {4, 2, 2});
    }

    @Test
    void testRightHeightTwoNeighboursAndOneNeighbourAlsoHasTwoNeighbours() {
        createNodes(5);
        nodes[0].linkToRightNeighbour(nodes[1]);
        nodes[0].linkToRightNeighbour(nodes[2]);
        nodes[1].linkToRightNeighbour(nodes[3]);
        nodes[1].linkToRightNeighbour(nodes[4]);

        initNodes(SequenceDirection.RIGHT);

        assertNodes(SequenceNode::getRightHeight, new int[] {6, 4, 2, 2, 2});
    }

    @Test
    void testRightHeightSharedNeighbour() {
        createNodes(4);
        nodes[0].linkToRightNeighbour(nodes[1]);
        nodes[0].linkToRightNeighbour(nodes[2]);
        nodes[1].linkToRightNeighbour(nodes[3]);
        nodes[2].linkToRightNeighbour(nodes[3]);

        initNodes(SequenceDirection.RIGHT);

        assertNodes(SequenceNode::getRightHeight, new int[] {4, 2, 2, 2});
    }

    @Test
    void testRightHeightSequentialSplits() {
        createNodes(7);
        nodes[0].linkToRightNeighbour(nodes[1]);
        nodes[0].linkToRightNeighbour(nodes[2]);
        nodes[1].linkToRightNeighbour(nodes[3]);
        nodes[2].linkToRightNeighbour(nodes[3]);
        nodes[3].linkToRightNeighbour(nodes[4]);
        nodes[3].linkToRightNeighbour(nodes[5]);
        nodes[4].linkToRightNeighbour(nodes[6]);
        nodes[5].linkToRightNeighbour(nodes[6]);

        initNodes(SequenceDirection.RIGHT);

        assertNodes(SequenceNode::getRightHeight, new int[] {4, 2, 2, 4, 2, 2, 2});
    }

    /*
     * Max height.
     */
    @Test
    void testGetSetMaxHeight() {
        createNodes(1);

        nodes[0].setMaxHeight(1197);

        assertNodes(SequenceNode::getMaxHeight, new int[] {1197});
    }

    @Test
    void testMaxHeightEmptyGraph() {
        final SequenceGraph graph = new SequenceGraph(new ArrayList<>());

        assertThat(graph.getSourceNode().getMaxHeight()).isEqualTo(2);
        assertThat(graph.getSinkNode().getMaxHeight()).isEqualTo(2);
    }

    @Test
    void testMaxHeightDisconnectedNodes() {
        createNodes(2);

        initGraph();

        assertNodes(SequenceNode::getMaxHeight, new int[] {4, 4});
    }

    /*
     * Vertical position.
     */
    @Test
    void testPositionEmptyGraph() {
        createNodes(0);

        initGraph();

        assertNodes(SequenceNode::getVerticalPosition, new int[] {1, 1});
    }

    @Test
    void testPositionSingleNode() {
        createNodes(1);

        initGraph();

        assertNodes(SequenceNode::getVerticalPosition, new int[] {1});
    }

    @Test
    void testPositionDiamond() throws ParseException {
        createNodes(4);
        nodes[0].linkToRightNeighbour(nodes[1]);
        nodes[0].linkToRightNeighbour(nodes[2]);
        nodes[1].linkToRightNeighbour(nodes[3]);
        nodes[2].linkToRightNeighbour(nodes[3]);

        initGraph();

        assertNodes(SequenceNode::getVerticalPosition, new int[] {2, 1, 3, 2});
    }

    @Test
    void testFafospDoubleTwoSplit() throws ParseException {
        createNodes(8);
        nodes[0].linkToRightNeighbour(nodes[1]);
        nodes[0].linkToRightNeighbour(nodes[2]);
        nodes[1].linkToRightNeighbour(nodes[3]);
        nodes[1].linkToRightNeighbour(nodes[4]);
        nodes[2].linkToRightNeighbour(nodes[5]);
        nodes[2].linkToRightNeighbour(nodes[6]);
        nodes[3].linkToRightNeighbour(nodes[7]);
        nodes[4].linkToRightNeighbour(nodes[7]);
        nodes[5].linkToRightNeighbour(nodes[7]);
        nodes[6].linkToRightNeighbour(nodes[7]);

        initGraph();

        assertNodes(SequenceNode::getVerticalPosition, new int[] {4, 2, 6, 1, 3, 5, 7, 4});
    }

    @Test
    void testFafospBidirectionalDoubleTwoSplit() throws ParseException {
        createNodes(10);
        nodes[0].linkToRightNeighbour(nodes[1]);
        nodes[0].linkToRightNeighbour(nodes[2]);
        nodes[1].linkToRightNeighbour(nodes[3]);
        nodes[1].linkToRightNeighbour(nodes[4]);
        nodes[2].linkToRightNeighbour(nodes[5]);
        nodes[2].linkToRightNeighbour(nodes[6]);
        nodes[3].linkToRightNeighbour(nodes[7]);
        nodes[4].linkToRightNeighbour(nodes[7]);
        nodes[5].linkToRightNeighbour(nodes[8]);
        nodes[6].linkToRightNeighbour(nodes[8]);
        nodes[7].linkToRightNeighbour(nodes[9]);
        nodes[8].linkToRightNeighbour(nodes[9]);

        initGraph();

        assertNodes(SequenceNode::getVerticalPosition, new int[] {4, 2, 6, 1, 3, 5, 7, 2, 6, 4});
    }

    @Test
    void testFafospSharedDoubleTwoSplit() throws ParseException {
        createNodes(7);
        nodes[0].linkToRightNeighbour(nodes[1]);
        nodes[0].linkToRightNeighbour(nodes[2]);
        nodes[1].linkToRightNeighbour(nodes[3]);
        nodes[1].linkToRightNeighbour(nodes[4]);
        nodes[2].linkToRightNeighbour(nodes[4]);
        nodes[2].linkToRightNeighbour(nodes[5]);
        nodes[3].linkToRightNeighbour(nodes[6]);
        nodes[4].linkToRightNeighbour(nodes[6]);
        nodes[5].linkToRightNeighbour(nodes[6]);

        initGraph();

        assertNodes(SequenceNode::getVerticalPosition, new int[] {4, 2, 6, 1, 4, 7, 4});
    }

    @Test
    void testFafospDoubleReversedSplit() throws ParseException {
        createNodes(6);
        nodes[0].linkToRightNeighbour(nodes[1]);
        nodes[0].linkToRightNeighbour(nodes[2]);
        nodes[1].linkToRightNeighbour(nodes[3]);
        nodes[2].linkToRightNeighbour(nodes[3]);
        nodes[2].linkToRightNeighbour(nodes[4]);
        nodes[3].linkToRightNeighbour(nodes[5]);
        nodes[4].linkToRightNeighbour(nodes[5]);

        initGraph();

        assertNodes(SequenceNode::getVerticalPosition, new int[] {3, 1, 4, 2, 5, 3});
    }


    /*
     * Helper methods.
     */

    /**
     * Creates an array of {@code SequenceNode}s, each with a locally unique identifier and sequence.
     *
     * @param count the number of nodes to create
     */
    private void createNodes(final int count) {
        nodes = new SequenceNode[count];
        for (int i = 0; i < count; i++) {
            nodes[i] = new SequenceNode(Integer.toString(i), Integer.toString(i));
        }
    }

    /**
     * Indirectly calls the fafosp methods through the {@code SequenceGraph}'s constructor.
     */
    private void initGraph() {
        new SequenceGraph(Arrays.asList(nodes));
    }

    /**
     * Calls the {@code fafospYInit} method using the given direction on all nodes in the array in reverse order.
     *
     * @param direction which height to calculate
     */
    private void initNodes(final SequenceDirection direction) {
        for (int i = nodes.length - 1; i >= 0; i--) {
            nodes[i].fafospYInit(direction);
        }
    }

    /**
     * Asserts for each initialised node that the value returned by some getter equals a corresponding value in the
     * array.
     * <p>
     * That is, if the given function is applied to the {@code i}th node, this method asserts that that value must
     * equal the {@code i}th value in the array.
     *
     * @param actual   a getter for a {@code SequenceNode}
     * @param expected an array of expected values
     */
    private void assertNodes(final Function<SequenceNode, Integer> actual, final int[] expected) {
        for (int i = 0; i < nodes.length; i++) {
            assertThat(actual.apply(nodes[i])).isEqualTo(expected[i]);
        }
    }
}
