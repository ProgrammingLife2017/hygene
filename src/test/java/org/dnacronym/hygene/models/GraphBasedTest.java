package org.dnacronym.hygene.models;

import org.junit.jupiter.api.AfterEach;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Utility methods for testing {@link Graph}s.
 * <p>
 * This class is intended to be extended.
 */
abstract class GraphBasedTest {
    private Graph graph;
    private int[][] nodeArrays;
    private GraphQuery graphQuery;


    /**
     * Resets this {@link GraphBasedTest}'s fields.
     */
    @AfterEach
    void afterEach() {
        graph = null;
        nodeArrays = null;
        graphQuery = null;
    }


    /**
     * Returns the current {@link Graph}.
     *
     * @return the current {@link Graph}
     */
    final Graph getGraph() {
        return graph;
    }

    /**
     * Returns a new {@link GraphIterator} for the current {@link Graph}.
     *
     * @return a new {@link GraphIterator} for the current {@link Graph}
     */
    final GraphIterator getGraphIterator() {
        return graph.iterator();
    }

    /**
     * Returns the current {@link GraphQuery}.
     *
     * @return the current {@link GraphQuery}
     */
    final GraphQuery getGraphQuery() {
        return graphQuery;
    }

    /**
     * Creates a new {@link Graph} with the given size, and sets it in this {@link GraphBasedTest}.
     *
     * @param size the size of the {@link Graph} to create
     */
    final void createGraph(final int size) {
        nodeArrays = new int[size][];
        for (int i = 0; i < size; i++) {
            nodeArrays[i] = NodeBuilder.start().toArray();
        }

        graph = new Graph(nodeArrays, null);
    }

    /**
     * Creates a new {@link GraphQuery} for the current {@link Graph}, and sets it in this {@link GraphBasedTest}.
     */
    final void createGraphQuery() {
        graphQuery = new GraphQuery(graph);
    }

    /**
     * Adds the given edges to the nodes in the {@link Graph}.
     * <p>
     * Each edge is described as a pair of two identifiers, the first being the {@code from} node and the second
     * being the {@code to} node. All node identifiers must be valid, i.e. {@code 0 <= id <= getGraph().size() - 1}.
     *
     * @param edges the edges to add
     */
    final void addEdges(final int[][] edges) {
        addOutgoingEdges(edges);
        addIncomingEdges(edges);
    }

    /**
     * Adds the given outgoing edges to the sending nodes in the {@link Graph}.
     * <p>
     * Each edge is described as a pair of two identifiers, the first being the {@code from} node and the second
     * being the {@code to} node. Only the {@code from} identifier has to be valid; the {@code to} identifier may be
     * any integer.
     *
     * @param edges the edges to add
     */
    final void addOutgoingEdges(final int[][] edges) {
        for (final int[] edge : edges) {
            assert (edge.length == 2);

            final int from = edge[0];
            final int to = edge[1];
            nodeArrays[from] = NodeBuilder.fromArray(from, nodeArrays[from])
                    .withOutgoingEdge(to, 0)
                    .toArray();
        }
    }

    /**
     * Adds the given incoming edges to the receiving nodes in the {@link Graph}.
     * <p>
     * Each edge is described as a pair of two identifiers, the first being the {@code from} node and the second
     * being the {@code to} node. Only the {@code to} identifier has to be valid; the {@code from} identifier may be
     * any integer.
     *
     * @param edges the edges to add
     */
    final void addIncomingEdges(final int[][] edges) {
        for (final int[] edge : edges) {
            assert (edge.length == 2);

            final int from = edge[0];
            final int to = edge[1];
            nodeArrays[to] = NodeBuilder.fromArray(to, nodeArrays[to])
                    .withIncomingEdge(from, 0)
                    .toArray();
        }
    }

    /**
     * Applies the {@code actual} {@link Function} to each identifier in the current {@link Graph} and compares it
     * against the {@code expected} value.
     *
     * @param expected the expected values
     * @param actual   the {@link Function} to apply to each identifier
     */
    final void assertForEachNode(final int[] expected, final Function<Integer, Integer> actual) {
        assert (expected.length == nodeArrays.length);

        for (int i = 0; i < expected.length; i++) {
            assertThat(actual.apply(i)).isEqualTo(expected[i]);
        }
    }

    /**
     * Sets the sequence lengths for the indicated nodes.
     * <p>
     * Each given array has a length of two, where the first integer is the node's identifier and the second integer
     * is the new sequence length for that node.
     */
    final void setSequenceLengths(final int[][] sequenceLengths) {
        for (int[] sequenceLength : sequenceLengths) {
            assert (sequenceLength.length == 2);

            final int id = sequenceLength[0];
            nodeArrays[id][Node.NODE_SEQUENCE_LENGTH_INDEX] = sequenceLength[1];
        }
    }
}
