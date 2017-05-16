package org.dnacronym.hygene.models;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * Created by Felix on 2017-05-16.
 */
public final class GraphIterator {
    private final Graph graph;
    private final int[][] nodeArrays;


    /**
     * Constructs a new {@link GraphIterator} for a particular {@link Graph}.
     *
     * @param graph the {@link Graph} to iterate over
     */
    GraphIterator(final Graph graph) {
        this.graph = graph;
        this.nodeArrays = graph.getNodeArrays();
    }


    /**
     * Applies the given {@code Consumer} to the identifiers of the neighbours in the given direction.
     *
     * @param id        the node's identifier
     * @param direction the direction of neighbours to visit
     * @param action    the function to apply to each neighbour's identifier
     */
    public void visitNeighbours(final int id, final SequenceDirection direction, final Consumer<Integer> action) {
        final int neighbourOffset = 1 + Node.NODE_OUTGOING_EDGES_INDEX
                + direction.ternary(graph.getNeighbourCount(id, direction.opposite()) * Node.EDGE_DATA_SIZE, 0);

        for (int i = 0; i < graph.getNeighbourCount(id, direction); i++) {
            final int neighbourIndex = neighbourOffset + 2 * i;
            action.accept(nodeArrays[id][neighbourIndex]);
        }
    }

    /**
     * Applies the given {@link Consumer} to the identifiers of the neighbours in the given direction until the given
     * {@link Predicate} returns {@code false} for that neighbour's identifier or until there are no more neighbours.
     *
     * @param id        the node's identifier
     * @param direction the direction of neighbours to visit
     * @param condition the {@link Predicate} that holds until no more neighbours should be visited
     * @param action    the function to apply to each neighbour's identifier
     */
    public void visitNeighboursWhile(final int id, final SequenceDirection direction,
                                     final Predicate<Integer> condition, final Consumer<Integer> action) {
        final Consumer<Integer> emptyCatchAction = ignored -> {
            // Do nothing
        };
        visitNeighboursWhile(id, direction, condition, emptyCatchAction, action);
    }

    /**
     * Applies the given {@link Consumer} to the identifiers of the neighbours in the given direction until the given
     * {@link Predicate} returns {@code false} for that neighbour's identifier or until there are no more neighbours.
     *
     * @param id          the node's identifier
     * @param direction   the direction of neighbours to visit
     * @param condition   the {@link Predicate} that holds until no more neighbours should be visited
     * @param catchAction the {@link Consumer} to execute as soon as the condition no longer holds
     * @param action      the function to apply to each neighbour's identifier
     */
    public void visitNeighboursWhile(final int id, final SequenceDirection direction,
                                     final Predicate<Integer> condition, final Consumer<Integer> catchAction,
                                     final Consumer<Integer> action) {
        final int neighbourOffset = 1 + Node.NODE_OUTGOING_EDGES_INDEX
                + direction.ternary(graph.getNeighbourCount(id, direction.opposite()) * Node.EDGE_DATA_SIZE, 0);

        for (int i = 0; i < graph.getNeighbourCount(id, direction); i++) {
            final int neighbour = nodeArrays[id][neighbourOffset + Node.EDGE_DATA_SIZE * i];

            if (!condition.test(neighbour)) {
                catchAction.accept(neighbour);
                break;
            }

            action.accept(neighbour);
        }
    }

    /**
     * Applies the given {@link Consumer} to the identifiers of the neighbours in the given direction until the given
     * {@link Predicate} returns {@code true} for that neighbour's identifier or until there are no more neighbours.
     *
     * @param id        the node's identifier
     * @param direction the direction of neighbours to visit
     * @param condition the {@link Predicate} that fails until no more neighbours should be visited
     * @param action    the function to apply to each neighbour's identifier
     */
    public void visitNeighboursUntil(final int id, final SequenceDirection direction,
                                     final Predicate<Integer> condition, final Consumer<Integer> action) {
        visitNeighboursWhile(id, direction, neighbour -> !condition.test(neighbour), action);
    }

    /**
     * Applies the given {@link Consumer} to the identifiers of the neighbours in the given direction until the given
     * {@link Predicate} returns {@code true} for that neighbour's identifier or until there are no more neighbours.
     *
     * @param id          the node's identifier
     * @param direction   the direction of neighbours to visit
     * @param condition   the {@link Predicate} that fails until no more neighbours should be visited
     * @param catchAction the {@link Consumer} to execute as soon as the condition holds
     * @param action      the function to apply to each neighbour's identifier
     */
    public void visitNeighboursUntil(final int id, final SequenceDirection direction,
                                     final Predicate<Integer> condition, final Consumer<Integer> catchAction,
                                     final Consumer<Integer> action) {
        visitNeighboursWhile(id, direction, neighbour -> !condition.test(neighbour), catchAction, action);
    }

    /**
     * Visits all nodes in this {@code Graph} and applies the given {@code Consumer} to their identifiers.
     *
     * @param direction the direction to visit the nodes in
     * @param action    the function to apply to each node's identifier
     */
    public void visitAll(final SequenceDirection direction, final Consumer<Integer> action) {
        final boolean[] visited = new boolean[nodeArrays.length];
        visitAll(direction, node -> visited[node], node -> {
            visited[node] = true;
            action.accept(node);
        });
    }

    /**
     * Visits all nodes in this {@code Graph} and applies the given {@code Consumer} to their identifiers.
     *
     * @param direction the direction to visit the nodes in
     * @param visited   a function that returns true if the node with the supplied id has been visited during this
     *                  iteration
     * @param action    the function to apply to each node's identifier
     */
    public void visitAll(final SequenceDirection direction, final Predicate<Integer> visited,
                         final Consumer<Integer> action) {
        final Queue<Integer> queue = new LinkedList<>();
        queue.add(0);

        while (!queue.isEmpty()) {
            final int head = queue.remove();
            if (visited.test(head)) {
                continue;
            }

            action.accept(head);

            visitNeighbours(head, direction, index -> {
                if (!visited.test(index)) {
                    queue.add(index);
                }
            });
        }
    }
}
