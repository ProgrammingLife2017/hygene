package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for FAFOSP-X.
 */
class NewFafospXTest extends GraphBasedTest {
    @Test
    void testNoNeighboursLength() {
        createGraph(3);
        addEdges(new int[][] {{0, 1}, {1, 2}});
        setSequenceLengths(new int[][] {{1, 6}});

        getGraph().fafospX();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(1 + 6);
    }

    @Test
    void testOneNeighbour() {
        createGraph(4);
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}});
        setSequenceLengths(new int[][] {{1, 7}, {2, 4}});

        getGraph().fafospX();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(1 + 7);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(1 + 11 + 1);
    }

    @Test
    void testTwoNeighbours() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}});
        setSequenceLengths(new int[][] {{1, 3}, {2, 13}, {3, 4}});

        getGraph().fafospX();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(1 + 3);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(1 + 13);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(1 + 17 + 1);
    }

    @Test
    void testChainOfThree() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}});
        setSequenceLengths(new int[][] {{1, 9}, {2, 19}, {3, 5}});

        getGraph().fafospX();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(1 + 9);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(1 + 28 + 1);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(1 + 33 + 2);
    }

    /**
     * Verifies correct behaviour when a node is visited twice in a depth-first order.
     */
    @Test
    void testDiamondShape() {
        createGraph(6);
        addEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {2, 4}, {3, 4}, {4, 5}});
        setSequenceLengths(new int[][] {{1, 7}, {2, 5}, {3, 14}, {4, 12}});

        getGraph().fafospX();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(1 + 7);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(1 + 12 + 1);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(1 + 21 + 1);
        assertThat(getGraph().getUnscaledXPosition(4)).isEqualTo(1 + 33 + 2);
    }

    /**
     * Verifies correct behaviour when a node is visited twice in a breadth-first order.
     */
    @Test
    void testBreadthFirstVisitTwice() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {2, 3}, {3, 4}});
        setSequenceLengths(new int[][] {{1, 14}, {2, 15}, {3, 8}});

        getGraph().fafospX();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(1 + 14);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(1 + 29 + 1);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(1 + 37 + 2);
    }

    @Test
    void testInsertionBubble() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {2, 4}, {3, 2}});
        setSequenceLengths(new int[][] {{1, 3}, {2, 5}, {3, 7}});

        getGraph().fafospX();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(1 + 3);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(1 + 15 + 2);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(1 + 10 + 1);
    }
}
