package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for FAFOSP-X in {@link Fafosp}.
 */
final class NewFafospXTest extends GraphBasedTest {
    @Test
    void testNoNeighboursLength() {
        createGraph(3);
        addEdges(new int[][] {{0, 1}, {1, 2}});
        setSequenceLengths(new int[][] {{1, 600}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(Fafosp.EDGE_WIDTH + 600);
    }

    @Test
    void testOneNeighbour() {
        createGraph(4);
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}});
        setSequenceLengths(new int[][] {{1, 700}, {2, 400}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(Fafosp.EDGE_WIDTH + 700);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(Fafosp.EDGE_WIDTH + 1100 + Fafosp.EDGE_WIDTH);
    }

    @Test
    void testTwoNeighbours() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}});
        setSequenceLengths(new int[][] {{1, 300}, {2, 1300}, {3, 400}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(Fafosp.EDGE_WIDTH + 300);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(Fafosp.EDGE_WIDTH + 1300);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(Fafosp.EDGE_WIDTH + 1700 + Fafosp.EDGE_WIDTH);
    }

    @Test
    void testChainOfThree() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}});
        setSequenceLengths(new int[][] {{1, 900}, {2, 1900}, {3, 500}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(Fafosp.EDGE_WIDTH + 900);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(Fafosp.EDGE_WIDTH + 2800 + Fafosp.EDGE_WIDTH);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(Fafosp.EDGE_WIDTH + 3300 + 2 * Fafosp.EDGE_WIDTH);
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

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(Fafosp.EDGE_WIDTH + 700);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(Fafosp.EDGE_WIDTH + 1200 + Fafosp.EDGE_WIDTH);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(Fafosp.EDGE_WIDTH + 2100 + Fafosp.EDGE_WIDTH);
        assertThat(getGraph().getUnscaledXPosition(4)).isEqualTo(Fafosp.EDGE_WIDTH + 3300 + 2 * Fafosp.EDGE_WIDTH);
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

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(Fafosp.EDGE_WIDTH + 1400);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(Fafosp.EDGE_WIDTH + 2900 + Fafosp.EDGE_WIDTH);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(Fafosp.EDGE_WIDTH + 3700 + 2 * Fafosp.EDGE_WIDTH);
    }

    @Test
    void testInsertionBubble() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {2, 4}, {3, 2}});
        setSequenceLengths(new int[][] {{1, 300}, {2, 500}, {3, 700}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(Fafosp.EDGE_WIDTH + 300);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(Fafosp.EDGE_WIDTH + 1500 + 2 * Fafosp.EDGE_WIDTH);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(Fafosp.EDGE_WIDTH + 1000 + Fafosp.EDGE_WIDTH);
    }
}
