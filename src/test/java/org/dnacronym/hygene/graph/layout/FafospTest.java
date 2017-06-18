package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.GraphTestBase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for FAFOSP-X in {@link Fafosp}.
 */
final class FafospTest extends GraphTestBase {
    @Test
    void testNoNeighboursLength() {
        createGraph(3);
        addEdges(new int[][] {{0, 1}, {1, 2}});
        setSequenceLengths(new int[][] {{1, 600}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(2);
    }

    @Test
    void testOneNeighbour() {
        createGraph(4);
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}});
        setSequenceLengths(new int[][] {{1, 700}, {2, 400}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(4);
    }

    @Test
    void testTwoNeighbours() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}});
        setSequenceLengths(new int[][] {{1, 300}, {2, 1300}, {3, 400}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(5);
    }

    @Test
    void testChainOfThree() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}});
        setSequenceLengths(new int[][] {{1, 900}, {2, 1900}, {3, 500}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(4);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(7);
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

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(4);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(4);
        assertThat(getGraph().getUnscaledXPosition(4)).isEqualTo(7);
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

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(5);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(8);
    }

    @Test
    void testInsertionBubble() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {2, 4}, {3, 2}});
        setSequenceLengths(new int[][] {{1, 300}, {2, 500}, {3, 700}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(2);
        assertThat(getGraph().getUnscaledXPosition(2)).isEqualTo(6);
        assertThat(getGraph().getUnscaledXPosition(3)).isEqualTo(4);
    }

    @Test
    void testOverflow() {
        createGraph(2);
        addEdges(new int[][] {{0, 1}});
        setSequenceLengths(new int[][] {{0, Integer.MAX_VALUE}, {1, 100}});

        getGraph().fafosp().horizontal();

        assertThat(getGraph().getUnscaledXPosition(0)).isEqualTo(0);
        assertThat(getGraph().getUnscaledXPosition(1)).isEqualTo(2147485);
    }
}
