package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for {@link GraphQuery}s.
 */
class GraphQueryTest extends GraphBasedTest {
    /*
     * query
     */
    @Test
    void testQuerySetCentre() {
        createGraph(51);
        createGraphQuery();

        getGraphQuery().query(32, 10);

        assertThat(getGraphQuery().getCentre()).isEqualTo(32);
    }

    @Test
    void testQuerySetRadius() {
        createGraph(63);
        createGraphQuery();

        getGraphQuery().query(19, 18);

        assertThat(getGraphQuery().getRadius()).isEqualTo(18);
    }

    @Test
    void testQueryIllegalNegativeCentre() {
        createGraph(71);
        createGraphQuery();

        final Throwable e = catchThrowable(() -> getGraphQuery().query(-14, 43));

        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testQueryIllegalExcessiveCentre() {
        createGraph(85);
        createGraphQuery();

        final Throwable e = catchThrowable(() -> getGraphQuery().query(139, 9));

        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testQueryIllegalNegativeRadius() {
        createGraph(60);
        createGraphQuery();

        final Throwable e = catchThrowable(() -> getGraphQuery().query(23, -14));

        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests that no nodes are in the GraphQuery when the radius is zero.
     */
    @Test
    void testQueryZeroRadius() {
        createGraph(62);
        createGraphQuery();

        getGraphQuery().query(49, 0);

        assertThat(collectGraphQueryNodes()).containsExactlyInAnyOrder(49);
    }

    /**
     * Tests that only the centre node is in the GraphQuery when the radius is one.
     */
    @Test
    void testQueryOneRadius() {
        createGraph(72);
        createGraphQuery();
        addEdges(new int[][] {{13, 22}, {22, 25}, {25, 55}});

        getGraphQuery().query(22, 1);

        assertThat(collectGraphQueryNodes()).containsExactlyInAnyOrder(13, 22, 25);
    }

    /**
     * Tests that a new query removes the data from a previous query.
     */
    @Test
    void testQueryNewCentre() {
        createGraph(29);
        createGraphQuery();
        addEdges(new int[][] {
                {4, 19}, {19, 23}, {23, 28},
                {13, 17}, {17, 24}, {24, 28}
        });

        getGraphQuery().query(19, 1);
        getGraphQuery().query(17, 2);

        assertThat(collectGraphQueryNodes()).containsExactlyInAnyOrder(13, 17, 24, 28);
    }


    /*
     * moveTo
     */

    @Test
    void testMoveToIllegalBelowZero() {
        createGraph(93);
        createGraphQuery();

        getGraphQuery().query(66, 51);

        final Throwable e = catchThrowable(() -> getGraphQuery().moveTo(-18));

        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testMoveToIllegalAboveGraphSize() {
        createGraph(80);
        createGraphQuery();

        getGraphQuery().query(62, 24);

        final Throwable e = catchThrowable(() -> getGraphQuery().moveTo(147));

        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests that a range of zero will always result in a cleared cache when the centre is moved.
     */
    @Test
    void testMoveToZeroRange() {
        createGraph(7);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {1, 5}, {2, 4}, {2, 5}, {4, 6}});

        getGraphQuery().query(3, 0);
        getGraphQuery().moveTo(1);

        assertThat(collectGraphQueryNodes()).containsExactly(1);
    }

    /**
     * Tests that no additional nodes are in the cache after moving the centre to the current value.
     */
    @Test
    void testMoveToSelf() {
        createGraph(5);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 2}, {2, 3}, {2, 4}, {3, 4}});

        getGraphQuery().query(3, 1);
        getGraphQuery().moveTo(3);

        assertThat(collectGraphQueryNodes()).containsExactlyInAnyOrder(2, 3, 4);
    }

    /**
     * Tests that the cache is rebuilt when the centre is moved to a value that is not currently in the cache.
     */
    @Test
    void testMoveToOutOfRange() {
        createGraph(5);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}});

        getGraphQuery().query(0, 2);
        getGraphQuery().moveTo(4);

        assertThat(collectGraphQueryNodes()).containsExactlyInAnyOrder(2, 3, 4);
    }

    /**
     * Tests that no new query is done by checking that values that are now out of range remain in the cache.
     */
    @Test
    void testMoveToInRange() {
        createGraph(5);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 2}, {0, 3}, {1, 4}, {2, 4}, {3, 4}});

        getGraphQuery().query(2, 1);
        getGraphQuery().moveTo(0);

        assertThat(collectGraphQueryNodes()).containsExactly(0, 1, 2, 3, 4);
    }

    /**
     * Tests that the cache is emptied if the cache becomes too large relative to the actual query.
     */
    @Test
    void testMoveToManyTimes() {
        createGraph(5);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}});

        getGraphQuery().query(0, 1);
        getGraphQuery().moveTo(1);
        getGraphQuery().moveTo(2);
        getGraphQuery().moveTo(3);
        getGraphQuery().moveTo(4);

        assertThat(collectGraphQueryNodes()).containsExactlyInAnyOrder(3, 4);
    }

    /**
     * Tests that all nodes within the specified range of the new centre are in the cache.
     */
    @Test
    void testMoveToInRandomGraph() {
        createGraph(16);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 3}, {1, 2}, {2, 4}, {3, 4}, {4, 5}, {4, 6}, {5, 7}, {5, 8}, {6, 9}, {6, 10},
                {6, 11}, {6, 13}, {7, 12}, {8, 9}, {8, 12}, {9, 10}, {9, 13}, {10, 13}, {11, 13}, {11, 14}, {12, 13},
                {13, 14}, {14, 15}});

        getGraphQuery().query(14, 2);
        getGraphQuery().moveTo(12);

        final List<Integer> nodes = collectGraphQueryNodes();
        assertThat(nodes).contains(15); // From cache
        assertThat(nodes).contains(5, 6, 7, 8, 9, 10, 11, 12, 13, 14); // Current query
    }


    /*
     * incrementRadius / decrementRadius
     */

    @Test
    void testDecrementRadiusBelowZero() {
        createGraph(26);
        createGraphQuery();

        getGraphQuery().query(14, 0);
        getGraphQuery().decrementRadius();

        assertThat(getGraphQuery().getRadius()).isEqualTo(0);
    }

    @Test
    void testIncrementRadiusValue() {
        createGraph(51);
        createGraphQuery();

        getGraphQuery().query(12, 42);

        getGraphQuery().decrementRadius();
        assertThat(getGraphQuery().getRadius()).isEqualTo(41);
    }

    @Test
    void testDecrementRadiusValue() {
        createGraph(78);
        createGraphQuery();

        getGraphQuery().query(56, 69);

        getGraphQuery().incrementRadius();
        assertThat(getGraphQuery().getRadius()).isEqualTo(70);
    }

    @Test
    void testIncrementRadius() {
        createGraph(84);
        createGraphQuery();
        addEdges(new int[][] {{3, 20}, {20, 35}, {35, 59}, {59, 73}});

        getGraphQuery().query(35, 1);
        getGraphQuery().incrementRadius();

        assertThat(collectGraphQueryNodes()).contains(3, 20, 35, 59, 73);
    }

    @Test
    void testDecrementRadius() {
        createGraph(8);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}, {1, 4}, {4, 5}, {5, 6}, {3, 6}, {2, 7}});

        getGraphQuery().query(3, 2);
        getGraphQuery().decrementRadius();

        assertThat(collectGraphQueryNodes()).contains(1, 2, 3, 6);
    }

    @Test
    void testIncrementThenDecrementRadius() {
        createGraph(7);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 3}, {2, 4}, {3, 5}, {3, 6}, {4, 5}, {4, 6}});

        getGraphQuery().query(4, 1);
        getGraphQuery().incrementRadius();
        getGraphQuery().decrementRadius();

        assertThat(collectGraphQueryNodes()).contains(1, 2, 4, 5, 6);
    }

    @Test
    void testDecrementThenIncrementRadius() {
        createGraph(8);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {2, 4}, {3, 4}, {4, 5}, {4, 6}, {5, 7}, {6, 7}});

        getGraphQuery().query(4, 2);
        getGraphQuery().decrementRadius();
        getGraphQuery().incrementRadius();

        assertThat(collectGraphQueryNodes()).contains(1, 2, 3, 4, 5, 6, 7);
    }

    /**
     * Tests that cached radius is reset after decrementing the radius often.
     */
    @Test
    void testDecrementRadiusOften() {
        createGraph(14);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 8}, {8, 9}, {9, 10}, {10, 11},
                {11, 12}, {12, 13}});

        getGraphQuery().query(0, 12);
        for (int i = 0; i < 11; i++) {
            getGraphQuery().decrementRadius();
        }

        assertThat(collectGraphQueryNodes()).doesNotContain(2);
    }
}
