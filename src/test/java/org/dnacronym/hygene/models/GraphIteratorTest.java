package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link GraphIterator}s.
 */
class GraphIteratorTest extends GraphBasedTest {
    private final Consumer<Integer> dummyConsumer = ignored -> {
    };


    @Test
    void testVisitRightNeighbours() {
        createGraph(1);
        addOutgoingEdges(new int[][] {{0, 69}, {0, 99}, {0, 72}, {0, 54}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitNeighbours(0, SequenceDirection.RIGHT, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(69, 99, 72, 54);
    }

    @Test
    void testVisitLeftNeighbours() {
        createGraph(1);
        addIncomingEdges(new int[][] {{15, 0}, {67, 0}, {10, 0}, {77, 0}, {60, 0}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitNeighbours(0, SequenceDirection.LEFT, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(15, 67, 10, 77, 60);
    }

    @Test
    void testVisitAllRightWithDuplicates() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {0, 2}, {0, 4}, {1, 2}, {1, 3}, {3, 4}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitAll(SequenceDirection.RIGHT, node -> false, neighbours::add);

        assertThat(neighbours).containsExactly(0, 1, 2, 4, 2, 3, 4);
    }

    @Test
    void testVisitAllRightWithoutDuplicates() {
        createGraph(5);
        addEdges(new int[][] {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 4}, {3, 4}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitAll(SequenceDirection.RIGHT, neighbours::add);

        assertThat(neighbours).containsExactly(0, 1, 2, 3, 4);
    }

    /**
     * Tests that all neighbours are visited if we visit "while true".
     */
    @Test
    void testVisitNeighboursWhileTrue() {
        createGraph(1);
        addOutgoingEdges(new int[][] {{0, 92}, {0, 69}, {0, 30}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitNeighboursWhile(0, SequenceDirection.RIGHT, ignored -> true, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(92, 69, 30);
    }

    /**
     * Tests that no neighbours are visited if we visit "while false".
     */
    @Test
    void testVisitNeighboursWhileFalse() {
        createGraph(1);
        addOutgoingEdges(new int[][] {{0, 56}, {0, 87}, {0, 72}, {0, 60}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitNeighboursWhile(0, SequenceDirection.RIGHT, ignored -> false, neighbours::add);

        assertThat(neighbours).isEmpty();
    }

    /**
     * Tests that neighbours are visited while the number of visited neighbours has a particular value.
     */
    @Test
    void testVisitNeighboursWhileSize() {
        createGraph(1);
        addIncomingEdges(new int[][] {{71, 0}, {11, 0}, {60, 0}, {18, 0}, {6, 0}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitNeighboursWhile(0, SequenceDirection.LEFT, ignored -> neighbours.size() < 3,
                neighbours::add);

        assertThat(neighbours).hasSize(3);
    }

    /**
     * Tests that the exit action is performed on the expected node.
     */
    @Test
    void testVisitNeighboursWhileCatchAction() {
        createGraph(1);
        addIncomingEdges(new int[][] {{75, 0}, {25, 0}, {22, 0}, {50, 0}, {58, 0}});

        final int[] exitNeighbour = new int[1];
        getGraph().iterator().visitNeighboursWhile(0, SequenceDirection.LEFT,
                neighbour -> neighbour != 22,
                neighbour -> exitNeighbour[0] = neighbour,
                dummyConsumer
        );

        assertThat(exitNeighbour[0]).isEqualTo(22);
    }

    /**
     * Tests that all neighbours are visited if we visit "until false".
     */
    @Test
    void testVisitNeighboursUntilFalse() {
        createGraph(1);
        addIncomingEdges(new int[][] {{94, 0}, {13, 0}, {23, 0}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitNeighboursUntil(0, SequenceDirection.LEFT, ignored -> false, neighbours::add);

        assertThat(neighbours).containsExactlyInAnyOrder(94, 13, 23);
    }

    /**
     * Tests that all neighbours are visited if we visit "until true".
     */
    @Test
    void testVisitNeighboursUntilTrue() {
        createGraph(1);
        addIncomingEdges(new int[][] {{65, 0}, {79, 0}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitNeighboursUntil(0, SequenceDirection.RIGHT, ignored -> true, neighbours::add);

        assertThat(neighbours).isEmpty();
    }

    /**
     * Tests that neighbours are visited until the number of visited neighbours exceeds a particular value.
     */
    @Test
    void testVisitNeighboursUntilSize() {
        createGraph(1);
        addIncomingEdges(new int[][] {{54, 0}, {50, 0}, {24, 0}, {5, 0}, {42, 0}, {32, 0}});

        final List<Integer> neighbours = new ArrayList<>();
        getGraph().iterator().visitNeighboursUntil(0, SequenceDirection.LEFT, ignored -> neighbours.size() > 4,
                neighbours::add);

        assertThat(neighbours).hasSize(5);
    }

    /**
     * Tests that the exit action is performed on the expected node.
     */
    @Test
    void testVisitNeighbourUntilCatchAction() {
        createGraph(1);
        addOutgoingEdges(new int[][] {{0, 83}});
        addIncomingEdges(new int[][] {{98, 0}, {94, 0}, {35, 0}});

        final int[] exitNeighbour = new int[1];
        getGraph().iterator().visitNeighboursUntil(0, SequenceDirection.LEFT,
                neighbour -> neighbour == 35,
                neighbour -> exitNeighbour[0] = neighbour,
                dummyConsumer
        );

        assertThat(exitNeighbour[0]).isEqualTo(35);
    }
}
