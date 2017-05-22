package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link GraphIterator}s.
 */
final class GraphIteratorTest extends GraphBasedTest {
    private final Consumer<Integer> dummyConsumer = ignored -> {
    };


    /*
     * visitDirectNeighbours
     */

    @Test
    void testVisitDirectNeighboursEitherNoNeighbours() {
        createGraph(1);

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighbours(0, neighbours::add);

        assertThat(neighbours).isEmpty();
    }

    @Test
    void testVisitDirectNeighboursEither() {
        createGraph(1);
        addOutgoingEdges(new int[][] {{0, 1}, {0, 2}, {0, 3}});
        addIncomingEdges(new int[][] {{4, 0}, {5, 0}, {6, 0}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighbours(0, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6);
    }

    @Test
    void testVisitDirectNeighboursLeftNoLeftNeighbours() {
        createGraph(1);
        addOutgoingEdges(new int[][] {{0, 22}, {0, 29}, {0, 18}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighbours(0, SequenceDirection.LEFT, neighbours::add);

        assertThat(neighbours).isEmpty();
    }

    @Test
    void testVisitDirectNeighboursLeft() {
        createGraph(1);
        addIncomingEdges(new int[][] {{15, 0}, {67, 0}, {10, 0}, {77, 0}, {60, 0}});
        addOutgoingEdges(new int[][] {{0, 73}, {0, 86}, {0, 20}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighbours(0, SequenceDirection.LEFT, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(15, 67, 10, 77, 60);
    }

    @Test
    void testVisitDirectNeighboursRightNoRightNeighbours() {
        createGraph(1);
        addIncomingEdges(new int[][] {{60, 0}, {44, 0}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighbours(0, SequenceDirection.RIGHT, neighbours::add);

        assertThat(neighbours).isEmpty();
    }

    @Test
    void testVisitDirectNeighboursRight() {
        createGraph(1);
        addIncomingEdges(new int[][] {{18, 0}, {8, 0}});
        addOutgoingEdges(new int[][] {{0, 69}, {0, 99}, {0, 72}, {0, 54}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighbours(0, SequenceDirection.RIGHT, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(69, 99, 72, 54);
    }


    /*
     * visitDirectNeighboursWhile
     */

    @Test
    void testVisitDirectNeighboursWhileLeftTrue() {
        createGraph(1);
        addIncomingEdges(new int[][] {{25, 0}, {39, 0}, {58, 0}});
        addOutgoingEdges(new int[][] {{0, 92}, {0, 69}, {0, 30}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighboursWhile(0, SequenceDirection.LEFT, ignored -> true,
                neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(25, 39, 58);
    }

    @Test
    void testVisitDirectNeighboursWhileLeftFalse() {
        createGraph(1);
        addIncomingEdges(new int[][] {{61, 0}, {84, 0}});
        addOutgoingEdges(new int[][] {{0, 56}, {0, 87}, {0, 72}, {0, 60}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighboursWhile(0, SequenceDirection.LEFT, ignored -> false,
                neighbours::add);

        assertThat(neighbours).isEmpty();
    }

    /**
     * Tests that neighbours are visited while the number of visited neighbours has a particular value.
     */
    @Test
    void testVisitDirectNeighboursWhileLeftSize() {
        createGraph(1);
        addIncomingEdges(new int[][] {{71, 0}, {11, 0}, {60, 0}, {18, 0}, {6, 0}});
        addOutgoingEdges(new int[][] {{0, 53}, {0, 83}, {0, 73}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighboursWhile(0, SequenceDirection.LEFT,
                ignored -> neighbours.size() < 3, neighbours::add);

        assertThat(neighbours).hasSize(3);
    }

    /**
     * Tests that the exit action is performed on the expected node.
     */
    @Test
    void testVisitDirectNeighboursWhileLeftCatchAction() {
        createGraph(1);
        addIncomingEdges(new int[][] {{75, 0}, {25, 0}, {22, 0}, {50, 0}, {58, 0}});
        addOutgoingEdges(new int[][] {{0, 94}, {0, 57}});

        final int[] exitNeighbour = new int[1];
        getGraph().iterator().visitDirectNeighboursWhile(0, SequenceDirection.LEFT,
                neighbour -> neighbour != 22,
                neighbour -> exitNeighbour[0] = neighbour,
                dummyConsumer
        );

        assertThat(exitNeighbour[0]).isEqualTo(22);
    }

    @Test
    void testVisitDirectNeighboursWhileRightTrue() {
        createGraph(1);
        addIncomingEdges(new int[][] {{30, 0}, {63, 0}});
        addOutgoingEdges(new int[][] {{0, 67}, {0, 98}, {0, 29}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighboursWhile(0, SequenceDirection.RIGHT, ignored -> true,
                neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(67, 98, 29);
    }

    @Test
    void testVisitDirectNeighboursWhileRightFalse() {
        createGraph(1);
        addIncomingEdges(new int[][] {{27, 0}, {18, 0}});
        addOutgoingEdges(new int[][] {{0, 53}, {0, 45}, {0, 65}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighboursWhile(0, SequenceDirection.RIGHT, ignored -> false,
                neighbours::add);

        assertThat(neighbours).isEmpty();
    }

    /**
     * Tests that neighbours are visited while the number of visited neighbours has a particular value.
     */
    @Test
    void testVisitDirectNeighboursWhileRightSize() {
        createGraph(1);
        addIncomingEdges(new int[][] {{23, 0}, {94, 0}});
        addOutgoingEdges(new int[][] {{0, 95}, {0, 12}, {0, 22}, {0, 43}, {0, 100}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighboursWhile(0, SequenceDirection.RIGHT,
                ignored -> neighbours.size() < 3, neighbours::add);

        assertThat(neighbours).hasSize(3);
    }

    /**
     * Tests that the exit action is performed on the expected node.
     */
    @Test
    void testVisitDirectNeighboursWhileRightCatchAction() {
        createGraph(1);
        addIncomingEdges(new int[][] {{31, 0}, {10, 0}, {66, 0}});
        addOutgoingEdges(new int[][] {{0, 30}, {0, 70}, {0, 57}});

        final int[] exitNeighbour = {-1};
        getGraph().iterator().visitDirectNeighboursWhile(0, SequenceDirection.RIGHT,
                neighbour -> neighbour != 57,
                neighbour -> exitNeighbour[0] = neighbour,
                dummyConsumer
        );

        assertThat(exitNeighbour[0]).isEqualTo(57);
    }


    /*
     * visitDirectNeighboursUntil
     */

    @Test
    void testVisitDirectNeighboursUntilLeftTrue() {
        createGraph(1);
        addIncomingEdges(new int[][] {{65, 0}, {79, 0}});
        addOutgoingEdges(new int[][] {{0, 56}, {0, 62}, {0, 38}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighboursUntil(0, SequenceDirection.LEFT, ignored -> true,
                neighbours::add);

        assertThat(neighbours).isEmpty();
    }

    @Test
    void testVisitDirectNeighboursUntilLeftFalse() {
        createGraph(1);
        addIncomingEdges(new int[][] {{94, 0}, {13, 0}, {23, 0}});
        addOutgoingEdges(new int[][] {{0, 46}, {0, 99}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighboursUntil(0, SequenceDirection.LEFT, ignored -> false,
                neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(94, 13, 23);
    }

    /**
     * Tests that left neighbours are visited until the number of visited neighbours exceeds a particular value.
     */
    @Test
    void testVisitDirectNeighboursUntilLeftSize() {
        createGraph(1);
        addIncomingEdges(new int[][] {{54, 0}, {50, 0}, {24, 0}, {5, 0}, {42, 0}, {32, 0}});
        addOutgoingEdges(new int[][] {{0, 45}, {0, 65}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighboursUntil(0, SequenceDirection.LEFT,
                ignored -> neighbours.size() > 4, neighbours::add);

        assertThat(neighbours).hasSize(5);
    }

    /**
     * Tests that the exit action is performed on the expected node.
     */
    @Test
    void testVisitDirectNeighbourUntilLeftCatchAction() {
        createGraph(1);
        addIncomingEdges(new int[][] {{98, 0}, {82, 0}, {35, 0}});
        addOutgoingEdges(new int[][] {{0, 83}, {0, 19}});

        final int[] exitNeighbour = new int[1];
        getGraph().iterator().visitDirectNeighboursUntil(0, SequenceDirection.LEFT,
                neighbour -> neighbour == 82,
                neighbour -> exitNeighbour[0] = neighbour,
                dummyConsumer
        );

        assertThat(exitNeighbour[0]).isEqualTo(82);
    }

    @Test
    void testVisitDirectNeighboursUntilRightTrue() {
        createGraph(1);
        addIncomingEdges(new int[][] {{21, 0}, {88, 0}});
        addOutgoingEdges(new int[][] {{0, 38}, {0, 49}, {0, 24}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighboursUntil(0, SequenceDirection.RIGHT, ignored -> true,
                neighbours::add);

        assertThat(neighbours).isEmpty();
    }

    @Test
    void testVisitDirectNeighboursUntilRightFalse() {
        createGraph(1);
        addIncomingEdges(new int[][] {{89, 0}, {17, 0}, {61, 0}});
        addOutgoingEdges(new int[][] {{0, 32}, {0, 40}, {0, 57}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighboursUntil(0, SequenceDirection.RIGHT, ignored -> false,
                neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(32, 40, 57);
    }

    /**
     * Tests that right neighbours are visited until the number of visited neighbours exceeds a particular value.
     */
    @Test
    void testVisitDirectNeighboursUntilRightSize() {
        createGraph(1);
        addIncomingEdges(new int[][] {{48, 0}, {82, 0}, {22, 0}});
        addOutgoingEdges(new int[][] {{0, 85}, {0, 31}, {0, 15}, {0, 92}, {0, 13}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitDirectNeighboursUntil(0, SequenceDirection.RIGHT,
                ignored -> neighbours.size() > 3, neighbours::add);

        assertThat(neighbours).hasSize(4);
    }

    /**
     * Tests that the exit action is performed on the expected node.
     */
    @Test
    void testVisitDirectNeighbourUntilRightCatchAction() {
        createGraph(1);
        addIncomingEdges(new int[][] {{13, 0}, {35, 0}, {57, 0}});
        addOutgoingEdges(new int[][] {{0, 43}, {0, 32}, {0, 25}, {0, 41}});

        final int[] exitNeighbour = new int[1];
        getGraph().iterator().visitDirectNeighboursUntil(0, SequenceDirection.RIGHT,
                neighbour -> neighbour == 32,
                neighbour -> exitNeighbour[0] = neighbour,
                dummyConsumer
        );

        assertThat(exitNeighbour[0]).isEqualTo(32);
    }


    /*
     * visitIndirectNeighbours
     */

    @Test
    void testVisitAllNeighboursLeftWithDuplicates() {
        createGraph(5);
        addEdges(new int[][] {{4, 3}, {4, 2}, {4, 1}, {3, 0}, {2, 0}, {1, 0}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitIndirectNeighbours(0, SequenceDirection.LEFT, node -> false, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(0, 1, 2, 3, 4, 4, 4);
    }

    @Test
    void testVisitAllNeighboursLeftWithoutDuplicates() {
        createGraph(9);
        addEdges(new int[][] {{8, 7}, {8, 6}, {7, 5}, {6, 5}, {5, 4}, {5, 3}, {4, 2}, {3, 2}, {2, 1}, {2, 0}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitIndirectNeighbours(2, SequenceDirection.LEFT, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(2, 3, 4, 5, 6, 7, 8);
    }

    @Test
    void testVisitAllNeighboursRightWithDuplicates() {
        createGraph(8);
        addEdges(new int[][] {{0, 1}, {1, 2}, {1, 3}, {1, 4}, {2, 5}, {3, 5}, {4, 6}, {5, 7}, {6, 7}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitIndirectNeighbours(1, SequenceDirection.RIGHT, node -> false, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 5, 6, 7, 7, 7);
    }

    @Test
    void testVisitAllNeighboursRightWithoutDuplicates() {
        createGraph(7);
        addEdges(new int[][] {{0, 2}, {1, 2}, {2, 3}, {2, 4}, {3, 5}, {4, 5}, {2, 5}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitIndirectNeighbours(2, SequenceDirection.RIGHT, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(2, 3, 4, 5);
    }


    /*
     * visitIndirectNeighboursWithinRange
     */

    @Test
    void testVisitAllNeighboursWithinRangeSimpleSingleHop() {
        createGraph(4);
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}});

        final List<Integer> nodes = new ArrayList<>();
        getGraph().iterator().visitIndirectNeighboursWithinRange(1, 1, nodes::add);

        assertThat(nodes).containsExactlyInAnyOrder(0, 1, 3);
    }

    @Test
    void testVisitAllNeighboursWithinRangeSimpleDoubleHop() {
        createGraph(4);
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 3}});

        final List<Integer> nodes = new ArrayList<>();
        getGraph().iterator().visitIndirectNeighboursWithinRange(1, 2, nodes::add);

        assertThat(nodes).containsExactlyInAnyOrder(0, 1, 2, 3);
    }

    @Test
    void testVisitAllNeighboursWithinRangeComplexSingleHop() {
        createGraph(8);
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {2, 6}, {3, 7}, {4, 7}, {5, 7}, {6, 7}});

        final List<Integer> nodes = new ArrayList<>();
        getGraph().iterator().visitIndirectNeighboursWithinRange(6, 1, nodes::add);

        assertThat(nodes).containsExactlyInAnyOrder(2, 6, 7);
    }

    @Test
    void testVisitAllNeighboursWithinRangeComplexDoubleHop() {
        createGraph(8);
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {2, 6}, {3, 7}, {4, 7}, {5, 7}, {6, 7}});

        final List<Integer> nodes = new ArrayList<>();
        getGraph().iterator().visitIndirectNeighboursWithinRange(6, 2, nodes::add);

        assertThat(nodes).containsExactlyInAnyOrder(0, 2, 3, 4, 5, 6, 7);
    }

    @Test
    void testVisitAllNeighboursWithinRangeUnbalanced() {
        createGraph(8);
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 6}, {5, 7}, {6, 7}});

        final List<Integer> nodes = new ArrayList<>();
        getGraph().iterator().visitIndirectNeighboursWithinRange(6, 2, nodes::add);

        assertThat(nodes).containsExactlyInAnyOrder(2, 4, 5, 6, 7);
    }


    /*
     * visitAll
     */

    @Test
    void testVisitAllLeftWithDuplicates() {
        createGraph(8);
        addEdges(new int[][] {{0, 2}, {1, 2}, {2, 3}, {2, 4}, {2, 5}, {3, 5}, {3, 6}, {3, 7}, {4, 7}});

        final List<Integer> nodes = new ArrayList<>();
        getGraph().iterator().visitAll(SequenceDirection.LEFT, ignored -> false, nodes::add);

        assertThat(nodes).containsExactlyInAnyOrder(7, 4, 3, 2, 2, 1, 1, 0, 0);
    }

    @Test
    void testVisitAllLeftWithoutDuplicates() {
        createGraph(8);
        addEdges(new int[][] {{0, 2}, {1, 2}, {2, 3}, {2, 4}, {2, 5}, {3, 5}, {3, 7}, {4, 7}});

        final List<Integer> nodes = new ArrayList<>();
        getGraph().iterator().visitAll(SequenceDirection.LEFT, nodes::add);

        assertThat(nodes).containsExactlyInAnyOrder(7, 4, 3, 2, 1, 0);
    }

    @Test
    void testVisitAllRightWithDuplicates() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {0, 2}, {0, 4}, {1, 2}, {1, 3}, {3, 4}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitAll(SequenceDirection.RIGHT, node -> false, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(0, 1, 2, 4, 2, 3, 4);
    }

    @Test
    void testVisitAllRightWithoutDuplicates() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 4}, {3, 4}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitAll(SequenceDirection.RIGHT, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(0, 1, 2, 3, 4);
    }


    /*
     * visitAllWithinRange
     */

    @Test
    void testVisitAllWithinRangeLeft() {
        createGraph(7);
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {3, 4}, {3, 5}, {3, 6}, {4, 5}, {4, 6}, {5, 6}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitAllWithinRange(SequenceDirection.LEFT, 2, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(6, 5, 4, 3, 1);
    }

    @Test
    void testVisitAllWithinRangeRight() {
        createGraph(7);
        addEdges(new int[][] {{0, 2}, {1, 2}, {2, 3}, {2, 4}, {3, 6}, {4, 6}, {5, 6}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitAllWithinRange(SequenceDirection.RIGHT, 2, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(0, 1, 2, 3, 4);
    }
}
