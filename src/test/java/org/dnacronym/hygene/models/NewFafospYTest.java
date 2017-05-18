package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for FAFOSP-Y.
 * <p>
 * Each method has a diagram representing the graph being tested. These graphs are not to scale; they only show the
 * edges that exist between the nodes.
 */
class NewFafospYTest extends GraphBasedTest {
    private final Function<Integer, Integer> getUnscaledYPosition = id -> getGraph().getUnscaledYPosition(id);


    @Test
    void testDefaultHeights() {
        createGraph(1);

        assertThat(getGraph().getUnscaledYPosition(0)).isEqualTo(-1);
    }

    /**
     * . . .
     * . 0 .
     * . . .
     */
    @Test
    void testSingleNode() {
        createGraph(1);

        getGraph().fafosp().vertical();

        assertForEachNode(new int[]{1}, getUnscaledYPosition);
    }

    /**
     * . . . . .
     * . 0 - 1 .
     * . . . . .
     */
    @Test
    void testTwoSequential() {
        createGraph(2);
        addEdges(new int[][]{{0, 1}});

        getGraph().fafosp().vertical();

        assertForEachNode(new int[]{1, 1}, getUnscaledYPosition);
    }

    /**
     * . . . . . . .
     * . 0 - 1 - 2 .
     * . . . . . . .
     */
    @Test
    void testThreeSequential() {
        createGraph(3);
        addEdges(new int[][]{{0, 1}, {1, 2}});

        getGraph().fafosp().vertical();

        assertForEachNode(new int[]{1, 1, 1}, getUnscaledYPosition);
    }

    /**
     * . . . . . . .
     * . . . 1 . . .
     * . . / . \ . .
     * . 0 . . . 3 .
     * . . \ . / . .
     * . . . 2 . . .
     * . . . . . . .
     */
    @Test
    void testTwoParallel() {
        createGraph(4);
        addEdges(new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}});

        getGraph().fafosp().vertical();

        assertForEachNode(new int[]{2, 1, 3, 2}, getUnscaledYPosition);
    }

    /**
     * . . . . . . .
     * . . . 1 . . .
     * . . / . \ . .
     * . 0 - 2 - 4 .
     * . . \ . / . .
     * . . . 3 . . .
     * . . . . . . .
     */
    @Test
    void testThreeParallel() {
        createGraph(5);
        addEdges(new int[][]{{0, 1}, {0, 2}, {0, 3}, {1, 4}, {2, 4}, {3, 4}});

        getGraph().fafosp().vertical();

        assertForEachNode(new int[]{3, 1, 3, 5, 3}, getUnscaledYPosition);
    }

    /**
     * . . . . . . . . . . .
     * . . . 1 . . . 4 . . .
     * . . / . \ . / . \ . .
     * . 0 . . . 3 . . . 6 .
     * . . \ . / . \ . / . .
     * . . . 2 . . . 5 . . .
     * . . . . . . . . . . .
     */
    @Test
    void testTwoParallelTwice() {
        createGraph(7);
        addEdges(new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}, {3, 5}, {4, 6}, {5, 6}});

        getGraph().fafosp().vertical();

        assertForEachNode(new int[]{2, 1, 3, 2, 1, 3, 2}, getUnscaledYPosition);
    }

    /**
     * . . . . . . . . . . . . . . .
     * . . . . . . . 3 . . . . . . .
     * . . . . . . / . \ . . . . . .
     * . . . . . 1 . . . \ . . . . .
     * . . . . / . \ . . . \ . . . .
     * . . . / . . . 4 - - - \ . . .
     * . . / . . . . . . . . . \ . .
     * . 0 . . . . . . . . . . . 7 .
     * . . \ . . . . . . . . . / . .
     * . . . \ . . . 5 - - - / . . .
     * . . . . \ . / . . . / . . . .
     * . . . . . 2 . . . / . . . . .
     * . . . . . . \ . / . . . . . .
     * . . . . . . . 6 . . . . . . .
     * . . . . . . . . . . . . . . .
     */
    @Test
    void testFafospDoubleTwoSplit() {
        createGraph(8);
        addEdges(new int[][]{
                {0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5},
                {2, 6}, {3, 7}, {4, 7}, {5, 7}, {6, 7}
        });

        getGraph().fafosp().vertical();

        assertForEachNode(new int[]{4, 2, 6, 1, 3, 5, 7, 4}, getUnscaledYPosition);
    }

    /**
     * . . . . . . . . . . . . . . .
     * . . . . . . . 3 . . . . . . .
     * . . . . . . / . \ . . . . . .
     * . . . . . 1 . . . 7 . . . . .
     * . . . . / . \ . / . \ . . . .
     * . . . / . . . 4 . . . \ . . .
     * . . / . . . . . . . . . \ . .
     * . 0 . . . . . . . . . . . 9 .
     * . . \ . . . . . . . . . / . .
     * . . . \ . . . 5 . . . / . . .
     * . . . . \ . / . \ . / . . . .
     * . . . . . 2 . . . 8 . . . . .
     * . . . . . . \ . / . . . . . .
     * . . . . . . . 6 . . . . . . .
     * . . . . . . . . . . . . . . .
     */
    @Test
    void testFafospBidirectionalDoubleTwoSplit() {
        createGraph(10);
        addEdges(new int[][]{
                {0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {2, 6},
                {3, 7}, {4, 7}, {5, 8}, {6, 8}, {7, 9}, {8, 9}
        });

        getGraph().fafosp().vertical();

        assertForEachNode(new int[]{4, 2, 6, 1, 3, 5, 7, 2, 6, 4}, getUnscaledYPosition);
    }

    /**
     * . . . . . . . . . . .
     * . . . . . 3 . . . . .
     * . . . . / . \ . . . .
     * . . . 1 . . . \ . . .
     * . . / . \ . . . \ . .
     * . 0 . . . 4 - - - 6 .
     * . . \ . / . . . / . .
     * . . . 2 . . . / . . .
     * . . . . \ . / . . . .
     * . . . . . 5 . . . . .
     * . . . . . . . . . . .
     */
    @Test
    void testFafospSharedDoubleTwoSplit() {
        createGraph(7);
        addEdges(new int[][]{{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 4}, {2, 5}, {3, 6}, {4, 6}, {5, 6}});

        getGraph().fafosp().vertical();

        assertForEachNode(new int[]{4, 2, 6, 1, 4, 7, 4}, getUnscaledYPosition);
    }

    /**
     * . . . . . . . . .
     * . . . 1 . . . . .
     * . . / . \ . . . .
     * . 0 . . . 3 . . .
     * . . \ . / . \ . .
     * . . . 2 . . . 5 .
     * . . . . \ . / . .
     * . . . . . 4 . . .
     * . . . . . . . . .
     */
    @Test
    void testFafospDoubleReversedSplit() {
        createGraph(6);
        addEdges(new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}, {2, 4}, {3, 5}, {4, 5}});

        getGraph().fafosp().vertical();

        assertForEachNode(new int[]{3, 1, 4, 2, 5, 3}, getUnscaledYPosition);
    }
}
