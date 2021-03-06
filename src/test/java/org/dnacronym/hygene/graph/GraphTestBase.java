package org.dnacronym.hygene.graph;

import org.junit.jupiter.api.AfterEach;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Utility methods for testing {@link Graph}s.
 * <p>
 * This class is intended to be extended.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod") // Must be abstract because it contains no tests
public abstract class GraphTestBase {
    private Graph graph;
    private int[][] nodeArrays;


    /**
     * Resets this {@link GraphTestBase}'s fields.
     */
    @AfterEach
    final void afterEach() {
        graph = null;
        nodeArrays = null;
    }


    /**
     * Creates a new {@link Graph} with the given size, and sets it in this {@link GraphTestBase}.
     *
     * @param size the size of the {@link Graph} to create
     */
    protected final void createGraph(final int size) {
        nodeArrays = new int[size][];
        for (int i = 0; i < size; i++) {
            nodeArrays[i] = NodeBuilder.start().toArray();
        }

        graph = new Graph(nodeArrays, null);
    }

    /**
     * Returns the current {@link Graph}.
     *
     * @return the current {@link Graph}
     */
    protected final Graph getGraph() {
        return graph;
    }

    /**
     * Adds the given edges to the nodes in the {@link Graph}.
     * <p>
     * Each edge is described as a pair of two identifiers, the first being the {@code from} node and the second
     * being the {@code to} node. All node identifiers must be valid, i.e. {@code 0 <= id <= getGraph().size() - 1}.
     *
     * @param edges the edges to add
     */
    protected final void addEdges(final int[][] edges) {
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
    protected final void addOutgoingEdges(final int[][] edges) {
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
    protected final void addIncomingEdges(final int[][] edges) {
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
     * Sets the sequence lengths for the indicated nodes.
     * <p>
     * Each given array has a length of two, where the first integer is the node's identifier and the second integer
     * is the new sequence length for that node.
     */
    protected final void setSequenceLengths(final int[][] sequenceLengths) {
        for (final int[] sequenceLength : sequenceLengths) {
            assert (sequenceLength.length == 2);

            final int id = sequenceLength[0];
            nodeArrays[id][Graph.NODE_SEQUENCE_LENGTH_INDEX] = sequenceLength[1];
        }
    }

    /**
     * Applies the {@code actual} {@link Function} to each identifier in the current {@link Graph} and compares it
     * against the {@code expected} value.
     *
     * @param expected the expected values
     * @param actual   the {@link Function} to apply to each identifier
     */
     protected final void assertForEachNode(final int[] expected, final Function<Integer, Integer> actual) {
        assert (expected.length == nodeArrays.length);

        for (int i = 0; i < expected.length; i++) {
            assertThat(actual.apply(i)).isEqualTo(expected[i]);
        }
    }
}
