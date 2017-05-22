package org.dnacronym.hygene.models;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * A collection of iteration functions over a particular {@link Graph} or a part of it.
 * <p>
 * A direct neighbour is a node that can be reached over a single edge. An indirect neighbour is a node that can be
 * reached over at least one edge.
 */
// All methods are closely related and cannot be refactored further
@SuppressWarnings("PMD.TooManyMethods")
public final class GraphIterator {
    private final Graph graph;
    private final int[][] nodeArrays;


    /**
     * Constructs a new {@link GraphIterator} for a particular {@link Graph}.
     *
     * @param graph the {@link Graph} to iterate over
     */
    public GraphIterator(final Graph graph) {
        this.graph = graph;
        this.nodeArrays = graph.getNodeArrays();
    }


    /**
     * Applies the given {@link Consumer} to the identifiers of the direct neighbours in either direction.
     *
     * @param id     the node's identifier
     * @param action the function to apply to each neighbour's identifier
     */
    public void visitDirectNeighbours(final int id, final Consumer<Integer> action) {
        visitDirectNeighbours(id, SequenceDirection.LEFT, action);
        visitDirectNeighbours(id, SequenceDirection.RIGHT, action);
    }

    /**
     * Applies the given {@link Consumer} to the identifiers of the direct neighbours in the given direction.
     *
     * @param id        the node's identifier
     * @param direction the direction of neighbours to visit
     * @param action    the function to apply to each neighbour's identifier
     */
    public void visitDirectNeighbours(final int id, final SequenceDirection direction, final Consumer<Integer> action) {
        final int neighbourOffset = getNeighbourOffset(id, direction);

        for (int i = 0; i < graph.getNeighbourCount(id, direction); i++) {
            final int neighbourIndex = neighbourOffset + Node.EDGE_DATA_SIZE * i;
            action.accept(nodeArrays[id][neighbourIndex]);
        }
    }

    /**
     * Applies the given {@link Consumer} to the identifiers of the direct neighbours in the given direction until the
     * given {@link Predicate} returns {@code false} for that neighbour's identifier or until there are no more
     * neighbours.
     *
     * @param id        the node's identifier
     * @param direction the direction of neighbours to visit
     * @param condition the {@link Predicate} that holds until no more neighbours should be visited
     * @param action    the function to apply to each neighbour's identifier
     */
    public void visitDirectNeighboursWhile(final int id, final SequenceDirection direction,
                                           final Predicate<Integer> condition, final Consumer<Integer> action) {
        final Consumer<Integer> emptyCatchAction = ignored -> {
            // Do nothing
        };
        visitDirectNeighboursWhile(id, direction, condition, emptyCatchAction, action);
    }

    /**
     * Applies the given {@link Consumer} to the identifiers of the direct neighbours in the given direction until the
     * given {@link Predicate} returns {@code false} for that neighbour's identifier or until there are no more
     * neighbours.
     *
     * @param id          the node's identifier
     * @param direction   the direction of neighbours to visit
     * @param condition   the {@link Predicate} that holds until no more neighbours should be visited
     * @param catchAction the {@link Consumer} to execute as soon as the condition no longer holds
     * @param action      the function to apply to each neighbour's identifier
     */
    public void visitDirectNeighboursWhile(final int id, final SequenceDirection direction,
                                           final Predicate<Integer> condition, final Consumer<Integer> catchAction,
                                           final Consumer<Integer> action) {
        final int neighbourOffset = getNeighbourOffset(id, direction);

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
     * Applies the given {@link Consumer} to the identifiers of the indirect neighbours in the given direction.
     *
     * @param id        the node's identifier
     * @param direction the direction of neighbours to visit
     * @param action    the function to apply to each neighbour's identifier
     */
    public void visitIndirectNeighbours(final int id, final SequenceDirection direction,
                                        final Consumer<Integer> action) {
        final boolean[] visited = new boolean[nodeArrays.length];
        visitIndirectNeighbours(id, direction, node -> visited[node], node -> {
            visited[node] = true;
            action.accept(node);
        });
    }

    /**
     * Applies the given {@link Consumer} to the identifiers of the indirect neighbours in the given direction.
     *
     * @param id        the node's identifier
     * @param direction the direction of neighbours to visit
     * @param visited   a function that returns true if the node with the supplied id has been visited during this
     *                  iteration
     * @param action    the function to apply to each neighbour's identifier
     */
    public void visitIndirectNeighbours(final int id, final SequenceDirection direction,
                                        final Predicate<Integer> visited, final Consumer<Integer> action) {
        final Queue<Integer> queue = new LinkedList<>();
        queue.add(id);

        while (!queue.isEmpty()) {
            final int head = queue.remove();
            if (visited.test(head)) {
                continue;
            }

            action.accept(head);
            visitDirectNeighbours(head, direction, index -> {
                if (!visited.test(index)) {
                    queue.add(index);
                }
            });
        }
    }

    /**
     * Applies the given {@link Consumer} to the identifiers of the indirect neighbours that can be reached within
     * the given number of hops in both directions.
     * <p>
     * Visits left neighbours first.
     *
     * @param id       the node's identifier
     * @param maxDepth the maximum number of hops a neighbour can be removed from the node
     * @param action   the function to apply to each neighbour's depth and identifier
     */
    public void visitIndirectNeighboursWithinRange(final int id, final int maxDepth,
                                                   final BiConsumer<Integer, Integer> action) {
        final boolean[] visited = new boolean[nodeArrays.length];
        final Queue<Integer> queue = new LinkedList<>();
        queue.add(id);

        int currentDepth = 0;
        final int[] depthIncreaseTimes = {1, 0};
        while (!queue.isEmpty()) {
            final int head = queue.remove();
            if (visited[head]) {
                continue;
            }

            visited[head] = true;
            action.accept(currentDepth, head);

            if (currentDepth == maxDepth) {
                continue;
            } else if (currentDepth > maxDepth) {
                return;
            }

            visitDirectNeighbours(head, SequenceDirection.LEFT, neighbour -> {
                if (!visited[neighbour]) {
                    depthIncreaseTimes[1]++;
                    queue.add(neighbour);
                }
            });
            visitDirectNeighbours(head, SequenceDirection.RIGHT, neighbour -> {
                if (!visited[neighbour]) {
                    depthIncreaseTimes[1]++;
                    queue.add(neighbour);
                }
            });


            depthIncreaseTimes[0]--;
            if (depthIncreaseTimes[0] == 0) {
                currentDepth++;

                depthIncreaseTimes[0] = depthIncreaseTimes[1];
                depthIncreaseTimes[1] = 0;
            }
        }
    }

    /**
     * Visits all nodes in this {@link Graph} and applies the given {@link Consumer} to their identifiers.
     *
     * @param direction the direction to visit the nodes in
     * @param action    the function to apply to each node's identifier
     */
    public void visitAll(final SequenceDirection direction, final Consumer<Integer> action) {
        final int sentinelId = direction.ternary(nodeArrays.length - 1, 0);
        visitIndirectNeighbours(sentinelId, direction, action);
    }

    /**
     * Visits all nodes in this {@link Graph} that can be reached within the given number of hops from the sentinel
     * node and, applies the given {@link Consumer} to their identifiers.
     *
     * @param direction the direction to visit the nodes in
     * @param maxDepth  the maximum number of hops a neighbour can be removed from the node
     * @param action    the function to apply to each node's depth and identifier
     */
    public void visitAllWithinRange(final SequenceDirection direction, final int maxDepth,
                                    final BiConsumer<Integer, Integer> action) {
        final int sentinelId = direction.ternary(nodeArrays.length - 1, 0);
        visitIndirectNeighboursWithinRange(sentinelId, maxDepth, action);
    }


    /**
     * Returns the offset within a node array where the neighbours in the given direction begin.
     *
     * @param id        the node's identifier
     * @param direction the direction of the neighbours
     * @return the offset within a node array where the neighbours in the given direction begin
     */
    private int getNeighbourOffset(final int id, final SequenceDirection direction) {
        return 1 + Node.NODE_OUTGOING_EDGES_INDEX + direction.ternary(
                graph.getNeighbourCount(id, direction.opposite()) * Node.EDGE_DATA_SIZE, 0);
    }
}
