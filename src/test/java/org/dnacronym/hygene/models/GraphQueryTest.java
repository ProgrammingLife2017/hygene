package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for {@link GraphQuery}s.
 */
class GraphQueryTest extends GraphTestBase {
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

        assertThat(collectVisit()).containsExactlyInAnyOrder(49);
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

        assertThat(collectVisit()).containsExactlyInAnyOrder(13, 22, 25);
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

        assertThat(collectVisit()).containsExactlyInAnyOrder(13, 17, 24, 28);
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

        assertThat(collectVisit()).containsExactly(1);
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

        assertThat(collectVisit()).containsExactlyInAnyOrder(2, 3, 4);
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

        assertThat(collectVisit()).containsExactlyInAnyOrder(2, 3, 4);
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

        assertThat(collectVisit()).containsExactly(0, 1, 2, 3, 4);
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

        assertThat(collectVisit()).containsExactlyInAnyOrder(3, 4);
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

        final List<Integer> nodes = collectVisit();
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

        assertThat(collectVisit()).contains(3, 20, 35, 59, 73);
    }

    @Test
    void testDecrementRadius() {
        createGraph(8);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}, {1, 4}, {4, 5}, {5, 6}, {3, 6}, {2, 7}});

        getGraphQuery().query(3, 2);
        getGraphQuery().decrementRadius();

        assertThat(collectVisit()).contains(1, 2, 3, 6);
    }

    @Test
    void testIncrementThenDecrementRadius() {
        createGraph(7);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 3}, {2, 4}, {3, 5}, {3, 6}, {4, 5}, {4, 6}});

        getGraphQuery().query(4, 1);
        getGraphQuery().incrementRadius();
        getGraphQuery().decrementRadius();

        assertThat(collectVisit()).contains(1, 2, 4, 5, 6);
    }

    @Test
    void testDecrementThenIncrementRadius() {
        createGraph(8);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {2, 4}, {3, 4}, {4, 5}, {4, 6}, {5, 7}, {6, 7}});

        getGraphQuery().query(4, 2);
        getGraphQuery().decrementRadius();
        getGraphQuery().incrementRadius();

        assertThat(collectVisit()).contains(1, 2, 3, 4, 5, 6, 7);
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

        assertThat(collectVisit()).doesNotContain(2);
    }


    /*
     * moveTo and change radius
     */

    @Test
    void testMoveToAndIncrementRadius() {
        createGraph(6);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}});

        getGraphQuery().query(1, 1);
        getGraphQuery().moveTo(2);
        getGraphQuery().incrementRadius();

        assertThat(collectVisit()).contains(0, 1, 2, 3, 4);
    }

    @Test
    void testIncrementRadiusAndMoveTo() {
        createGraph(6);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}});

        getGraphQuery().query(1, 1);
        getGraphQuery().incrementRadius();
        getGraphQuery().moveTo(2);

        assertThat(collectVisit()).contains(0, 1, 2, 3, 4);
    }


    /*
     * visitBFS
     */

    /**
     * Tests that only a single node is visited during BFS if the cache contains only that node, even if the graph
     * contains multiple nodes.
     */
    @Test
    void testVisitBFSSingleNode() {
        createGraph(4);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}});

        getGraphQuery().query(2, 0);

        assertThat(collectVisitBFS(SequenceDirection.LEFT)).containsExactly(2);
        assertThat(collectVisitBFS(SequenceDirection.RIGHT)).containsExactly(2);
    }

    /**
     * Tests that the first few nodes visited are the direct neighbours of the source node of the subgraph formed by
     * the query.
     */
    @Test
    void testVisitBFSSourceNodes() {
        createGraph(9);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 4}, {2, 5}, {4, 6}, {4, 7}, {6, 8}, {7, 8}});

        getGraphQuery().query(4, 1);

        assertThat(collectVisitBFS(SequenceDirection.RIGHT).subList(0, 2)).containsExactlyInAnyOrder(1, 2);
    }

    /**
     * Tests that the first few nodes visited are the direct neighbours of the sink node of the subgraph formed by
     * the query.
     */
    @Test
    void testVisitBFSSinkNodes() {
        createGraph(9);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 4}, {2, 5}, {4, 6}, {4, 7}, {6, 8}, {7, 8}});

        getGraphQuery().query(4, 1);

        assertThat(collectVisitBFS(SequenceDirection.LEFT).subList(0, 2)).containsExactlyInAnyOrder(6, 7);
    }

    /**
     * Tests that visiting in breadth-first order will visit the same nodes as in non-breadth-first order in a random
     * pre-generated graph.
     */
    @Test
    void testVisitBFSLeftRandomGraph() {
        createGraph(10);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 2}, {0, 3}, {1, 4}, {1, 5}, {2, 6}, {3, 7}, {5, 6}, {6, 7}, {6, 8}, {7, 9}});

        getGraphQuery().query(6, 2);

        final List<Integer> visitNodes = collectVisit();
        final List<Integer> visitBfsNodes = collectVisitBFS(SequenceDirection.LEFT);
        assertThat(visitNodes).containsAll(visitBfsNodes);
        assertThat(visitBfsNodes).containsAll(visitNodes);
    }

    /**
     * Tests that visiting in breadth-first order will visit the same nodes as in non-breadth-first order in a random
     * pre-generated graph.
     */
    @Test
    void testVisitBFSRightRandomGraph() {
        createGraph(10);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {0, 2}, {0, 3}, {1, 4}, {1, 5}, {2, 6}, {3, 7}, {5, 6}, {6, 7}, {6, 8}, {7, 9}});

        getGraphQuery().query(6, 2);

        final List<Integer> visitNodes = collectVisit();
        final List<Integer> visitBfsNodes = collectVisitBFS(SequenceDirection.RIGHT);
        assertThat(visitNodes).containsAll(visitBfsNodes);
        assertThat(visitBfsNodes).containsAll(visitNodes);
    }


    /*
     * Helper methods
     */

    /**
     * Returns all nodes that can be visited by the current {@link GraphQuery} using {@link GraphQuery#visit(Consumer)}.
     *
     * @return all nodes that can be visited by the current {@link GraphQuery} using {@link GraphQuery#visit(Consumer)}
     */
    private List<Integer> collectVisit() {
        final List<Integer> nodes = new ArrayList<>();
        getGraphQuery().visit(nodes::add);
        return nodes;
    }

    /**
     * Returns all nodes that can be visited by the current {@link GraphQuery} using
     * {@link GraphQuery#visitBFS(SequenceDirection, Consumer)}.
     *
     * @return all nodes that can be visited by the current {@link GraphQuery} using {@link
     * GraphQuery#visitBFS(SequenceDirection, Consumer)}
     */
    private List<Integer> collectVisitBFS(final SequenceDirection direction) {
        final List<Integer> nodes = new ArrayList<>();
        getGraphQuery().visitBFS(direction, nodes::add);
        return nodes;
    }


    /*
     * setRadius
     */

    @Test
    void testSetRadiusValueBelowZero() {
        createGraph(48);
        createGraphQuery();

        getGraphQuery().query(17, 9);
        getGraphQuery().setRadius(-8);

        assertThat(getGraphQuery().getRadius()).isEqualTo(0);
    }

    @Test
    void testSetRadiusValueEquals() {
        createGraph(82);
        createGraphQuery();

        getGraphQuery().query(14, 8);
        getGraphQuery().setRadius(8);

        assertThat(getGraphQuery().getRadius()).isEqualTo(8);
    }

    @Test
    void testSetRadiusValueIncrease() {
        createGraph(95);
        createGraphQuery();

        getGraphQuery().query(63, 7);
        getGraphQuery().setRadius(18);

        assertThat(getGraphQuery().getRadius()).isEqualTo(18);
    }

    @Test
    void testSetRadiusValueDecrease() {
        createGraph(76);
        createGraphQuery();

        getGraphQuery().query(50, 12);
        getGraphQuery().setRadius(4);

        assertThat(getGraphQuery().getRadius()).isEqualTo(4);
    }

    @Test
    void testSetRadiusEqual() {
        createGraph(7);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}});

        getGraphQuery().query(3, 2);
        getGraphQuery().setRadius(2);

        assertThat(collectVisit()).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
    }

    @Test
    void testSetRadiusIncreaseSimple() {
        createGraph(9);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 8}});

        getGraphQuery().query(4, 1);
        getGraphQuery().setRadius(3);

        assertThat(collectVisit()).contains(1, 2, 3, 4, 5, 6, 7);
    }

    @Test
    void testSetRadiusDecreaseSimple() {
        createGraph(9);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 8}});

        getGraphQuery().query(3, 4);
        getGraphQuery().setRadius(1);

        assertThat(collectVisit()).contains(3);
    }

    /**
     * Tests that the correct nodes are present when the range is increased but the necessary nodes for this new
     * range are already in the cache.
     */
    @Test
    void testSetRadiusIncreaseInCache() {
        createGraph(9);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {7, 8}});

        getGraphQuery().query(3, 2);
        getGraphQuery().moveTo(4);
        getGraphQuery().moveTo(3);
        getGraphQuery().setRadius(3);

        assertThat(collectVisit()).contains(0, 1, 2, 3, 4, 5, 6);
    }

    /**
     * Tests that the correct nodes are present when the range is increased by more than the maximum.
     */
    @Test
    void testSetRadiusIncreaseExceedMaxSetRadiusIncrease() {
        createGraph(8);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}});

        getGraphQuery().query(0, 0);
        getGraphQuery().setRadius(6);

        assertThat(collectVisit()).containsExactlyInAnyOrder(0, 1, 2, 3, 4, 5, 6);
    }

    /**
     * Tests that the cache is rebuilt after decreasing the range with a sufficiently large step.
     */
    @Test
    void testSetRadiusDecreaseExceedMaxRadiusDifference() {
        createGraph(7);
        createGraphQuery();
        addEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}});

        getGraphQuery().query(0, 11);
        getGraphQuery().setRadius(0);

        assertThat(collectVisit()).containsExactly(0);
    }
}
