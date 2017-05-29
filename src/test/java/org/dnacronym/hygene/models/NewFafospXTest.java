package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for FAFOSP-X in {@link Fafosp}.
 */
final class NewFafospXTest extends GraphTestBase {
    @Test
    void testNoNeighboursLength() {
        createGraph(3);
        addEdges(new int[][] {{0, 1}, {1, 2}});
        setSequenceLengths(new int[][] {{1, 600}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXEdgeCount(1)).isEqualTo(1);
        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(600);
    }

    @Test
    void testOneNeighbour() {
        createGraph(4);
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}});
        setSequenceLengths(new int[][] {{1, 700}, {2, 400}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXEdgeCount(1)).isEqualTo(1);
        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(700);
        assertThat(getGraph().getUnscaledXEdgeCount(2)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(1100);
    }

    @Test
    void testTwoNeighbours() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}});
        setSequenceLengths(new int[][] {{1, 300}, {2, 1300}, {3, 400}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXEdgeCount(1)).isEqualTo(1);
        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(300);
        assertThat(getGraph().getUnscaledXEdgeCount(2)).isEqualTo(1);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(1300);
        assertThat(getGraph().getUnscaledXEdgeCount(3)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(1700);
    }

    @Test
    void testChainOfThree() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}});
        setSequenceLengths(new int[][] {{1, 900}, {2, 1900}, {3, 500}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXEdgeCount(1)).isEqualTo(1);
        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(900);
        assertThat(getGraph().getUnscaledXEdgeCount(2)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(2800);
        assertThat(getGraph().getUnscaledXEdgeCount(3)).isEqualTo(3);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(3300);
    }

    /**
     * Verifies correct behaviour when a node is visited twice in a depth-first order.
     */
    @Test
    void testDiamondShape() {
        createGraph(6);
        addEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {2, 4}, {3, 4}, {4, 5}});
        setSequenceLengths(new int[][] {{1, 700}, {2, 500}, {3, 1400}, {4, 1200}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXEdgeCount(1)).isEqualTo(1);
        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(700);
        assertThat(getGraph().getUnscaledXEdgeCount(2)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(1200);
        assertThat(getGraph().getUnscaledXEdgeCount(3)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(2100);
        assertThat(getGraph().getUnscaledXEdgeCount(4)).isEqualTo(3);
        assertThat(getGraph().getUnscaledXPosition(4)).isEqualTo(3300);
    }

    /**
     * Verifies correct behaviour when a node is visited twice in a breadth-first order.
     */
    @Test
    void testBreadthFirstVisitTwice() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {2, 3}, {3, 4}});
        setSequenceLengths(new int[][] {{1, 1400}, {2, 1500}, {3, 800}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXEdgeCount(1)).isEqualTo(1);
        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(1400);
        assertThat(getGraph().getUnscaledXEdgeCount(2)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(2900);
        assertThat(getGraph().getUnscaledXEdgeCount(3)).isEqualTo(3);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(3700);
    }

    @Test
    void testInsertionBubble() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {2, 4}, {3, 2}});
        setSequenceLengths(new int[][] {{1, 300}, {2, 500}, {3, 700}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXEdgeCount(1)).isEqualTo(1);
        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(300);
        assertThat(getGraph().getUnscaledXEdgeCount(2)).isEqualTo(3);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(1500);
        assertThat(getGraph().getUnscaledXEdgeCount(3)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(1000);
    }
}
