package org.dnacronym.hygene.models;

import org.junit.jupiter.api.BeforeEach;


/**
 * Utility methods for testing {@link Graph}s.
 * <p>
 * This class is intended to be extended.
 */
abstract class GraphBasedTest {
    private Graph graph;
    private int[][] nodeArrays;


    /**
     * Resets this {@link GraphBasedTest}'s fields.
     */
    @BeforeEach
    private void beforeEach() {
        graph = null;
        nodeArrays = null;
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
}
