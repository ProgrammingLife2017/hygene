package org.dnacronym.hygene.models;

import org.dnacronym.hygene.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for FAFOSP-Y.
 */
final class FafospYTest {
    private SequenceNode[] nodes;


    /*
     * Default values.
     */
    @Test
    void testDefaultHeights() {
        final SequenceNode node = new SequenceNode("A", "A");

        assertThat(node.getLeftHeight()).isEqualTo(-1);
        assertThat(node.getRightHeight()).isEqualTo(-1);
        assertThat(node.getVerticalPosition()).isEqualTo(-1);
    }


    /*
     * Left height.
     */
    @Test
    void testLeftHeightNoNeighbours() {
        createNodes(1);

        initNodes(SequenceDirection.LEFT);

        assertNodes(new int[] {2}, SequenceNode::getLeftHeight);
    }

    @Test
    void testLeftHeightSingleNeighbour() {
        createNodes(2);
        linkNodes(SequenceDirection.LEFT, new int[][] {{0, 1}});

        initNodes(SequenceDirection.LEFT);

        assertNodes(new int[] {2, 2}, SequenceNode::getLeftHeight);
    }

    @Test
    void testLeftHeightTwoNeighbours() {
        createNodes(3);
        linkNodes(SequenceDirection.LEFT, new int[][] {{0, 1}, {0, 2}});

        initNodes(SequenceDirection.LEFT);

        assertNodes(new int[] {4, 2, 2}, SequenceNode::getLeftHeight);
    }

    @Test
    void testLeftHeightTwoNeighboursAndOneNeighbourAlsoHasTwoNeighbours() {
        createNodes(5);
        linkNodes(SequenceDirection.LEFT, new int[][] {{0, 1}, {0, 2}, {1, 3}, {1, 4}});

        initNodes(SequenceDirection.LEFT);

        assertNodes(new int[] {6, 4, 2, 2, 2}, SequenceNode::getLeftHeight);
    }

    @Test
    void testLeftHeightSharedNeighbour() {
        createNodes(4);
        linkNodes(SequenceDirection.LEFT, new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}});

        initNodes(SequenceDirection.LEFT);

        assertNodes(new int[] {4, 2, 2, 2}, SequenceNode::getLeftHeight);
    }

    @Test
    void testLeftHeightSequentialSplits() {
        createNodes(7);
        linkNodes(SequenceDirection.LEFT,
                new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}, {3, 5}, {4, 6}, {5, 6}});

        initNodes(SequenceDirection.LEFT);

        assertNodes(new int[] {4, 2, 2, 4, 2, 2, 2}, SequenceNode::getLeftHeight);
    }


    /*
     * Right height.
     */
    @Test
    void testRightHeightNoNeighbours() {
        createNodes(1);

        initNodes(SequenceDirection.RIGHT);

        assertNodes(new int[] {2}, SequenceNode::getRightHeight);
    }

    @Test
    void testRightHeightSingleNeighbour() {
        createNodes(2);
        linkNodes(SequenceDirection.RIGHT, new int[][] {{0, 1}});

        initNodes(SequenceDirection.RIGHT);

        assertNodes(new int[] {2, 2}, SequenceNode::getRightHeight);
    }

    @Test
    void testRightHeightTwoNeighbours() {
        createNodes(3);
        linkNodes(SequenceDirection.RIGHT, new int[][] {{0, 1}, {0, 2}});

        initNodes(SequenceDirection.RIGHT);

        assertNodes(new int[] {4, 2, 2}, SequenceNode::getRightHeight);
    }

    @Test
    void testRightHeightTwoNeighboursAndOneNeighbourAlsoHasTwoNeighbours() {
        createNodes(5);
        linkNodes(SequenceDirection.RIGHT, new int[][] {{0, 1}, {0, 2}, {1, 3}, {1, 4}});

        initNodes(SequenceDirection.RIGHT);

        assertNodes(new int[] {6, 4, 2, 2, 2}, SequenceNode::getRightHeight);
    }

    @Test
    void testRightHeightSharedNeighbour() {
        createNodes(4);
        linkNodes(SequenceDirection.RIGHT,
                new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}});

        initNodes(SequenceDirection.RIGHT);

        assertNodes(new int[] {4, 2, 2, 2}, SequenceNode::getRightHeight);
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

        assertNodes(new int[] {4, 2, 2, 4, 2, 2, 2}, SequenceNode::getRightHeight);
    }


    /*
     * Vertical position.
     */
    @Test
    void testPositionEmptyGraph() {
        createNodes(0);

        initGraph();

        assertNodes(new int[] {1, 1}, SequenceNode::getVerticalPosition);
    }

    @Test
    void testPositionSingleNode() {
        createNodes(1);

        initGraph();

        assertNodes(new int[] {1}, SequenceNode::getVerticalPosition);
    }

    @Test
    void testPositionDiamond() throws ParseException {
        createNodes(4);
        linkNodes(SequenceDirection.RIGHT, new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}});

        initGraph();

        assertNodes(new int[] {2, 1, 3, 2}, SequenceNode::getVerticalPosition);
    }

    @Test
    void testFafospDoubleTwoSplit() throws ParseException {
        createNodes(8);
        linkNodes(SequenceDirection.RIGHT, new int[][] {
                {0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5},
                {2, 6}, {3, 7}, {4, 7}, {5, 7}, {6, 7}
        });

        initGraph();

        assertNodes(new int[] {4, 2, 6, 1, 3, 5, 7, 4}, SequenceNode::getVerticalPosition);
    }

    @Test
    void testFafospBidirectionalDoubleTwoSplit() throws ParseException {
        createNodes(10);
        linkNodes(SequenceDirection.RIGHT, new int[][] {
                {0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {2, 6},
                {3, 7}, {4, 7}, {5, 8}, {6, 8}, {7, 9}, {8, 9}
        });

        initGraph();

        assertNodes(new int[] {4, 2, 6, 1, 3, 5, 7, 2, 6, 4}, SequenceNode::getVerticalPosition);
    }

    @Test
    void testFafospSharedDoubleTwoSplit() throws ParseException {
        createNodes(7);
        linkNodes(SequenceDirection.RIGHT, new int[][] {
                {0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 4}, {2, 5}, {3, 6}, {4, 6}, {5, 6}});

        initGraph();

        assertNodes(new int[] {4, 2, 6, 1, 4, 7, 4}, SequenceNode::getVerticalPosition);
    }

    @Test
    void testFafospDoubleReversedSplit() throws ParseException {
        createNodes(6);
        linkNodes(SequenceDirection.RIGHT, new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}, {2, 4}, {3, 5}, {4, 5}});

        initGraph();

        assertNodes(new int[] {3, 1, 4, 2, 5, 3}, SequenceNode::getVerticalPosition);
    }


    /*
     * Helper methods.
     */

    /**
     * Creates an array of {@link SequenceNode}s, each with a locally unique identifier and sequence.
     *
     * @param count the number of nodes to create
     */
    private void createNodes(final int count) {
        assert (count >= 0);

        nodes = new SequenceNode[count];
        for (int i = 0; i < count; i++) {
            nodes[i] = new SequenceNode(Integer.toString(i), Integer.toString(i));
        }
    }

    /**
     * Links nodes together in the given direction.
     *
     * @param direction {@link SequenceDirection#LEFT} iff. the first node in a link is the node from which each the
     *                  edge leaves
     * @param links     an array of links, where each link is described as a pair of node identifiers representing the
     *                  from and to nodes of the link
     */
    private void linkNodes(final SequenceDirection direction, final int[][] links) {
        assert (nodes != null);

        for (final int[] link : links) {
            assert (link.length == 2);
            assert (link[0] != link[1]);

            final SequenceNode from = nodes[link[0]];
            final SequenceNode to = nodes[link[1]];

            direction.ternary(
                    () -> from.linkToLeftNeighbour(to),
                    () -> from.linkToRightNeighbour(to));
        }
    }

    /**
     * Indirectly calls the fafosp methods through the {@link SequenceGraph}'s constructor.
     */
    private void initGraph() {
        assert (nodes != null);

        new SequenceGraph(Arrays.asList(nodes));
    }

    /**
     * Calls the {@link SequenceNode#fafospYInit} method using the given direction on all nodes in the array in
     * reverse order.
     *
     * @param direction which height to calculate
     */
    private void initNodes(final SequenceDirection direction) {
        assert (nodes != null);

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
     * @param expected an array of expected values
     * @param actual   a getter for a {@link SequenceNode}
     */
    private void assertNodes(final int[] expected, final Function<SequenceNode, Integer> actual) {
        assert (nodes != null);

        for (int i = 0; i < nodes.length; i++) {
            assertThat(actual.apply(nodes[i])).isEqualTo(expected[i]);
        }
    }
}
