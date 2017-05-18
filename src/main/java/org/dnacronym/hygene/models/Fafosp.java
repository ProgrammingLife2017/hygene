package org.dnacronym.hygene.models;

import java.util.LinkedList;
import java.util.Queue;


/**
 * A collection of methods for algorithmically determining the optimal position for the nodes in a {@link Graph}
 * using FAFOSP, the Felix Algorithm For Optimal Segment Positioning.
 */
public final class Fafosp {
    private final Graph graph;
    private final int[][] nodeArrays;


    /**
     * Constructs a new {@link Fafosp}.
     *
     * @param graph the {@link Graph} to calculate positions for
     */
    public Fafosp(final Graph graph) {
        this.graph = graph;
        this.nodeArrays = graph.getNodeArrays();
    }


    /**
     * Calculates the optimal horizontal position of each node in the {@link Graph}.
     */
    public void horizontal() {
        final Queue<Integer> queue = new LinkedList<>();
        graph.iterator().visitDirectNeighbours(0, SequenceDirection.RIGHT, queue::add);
        graph.setUnscaledXPosition(0, 0);

        while (!queue.isEmpty()) {
            final Integer head = queue.remove();

            // Horizontal position may have been set since it was added to the queue
            if (graph.getUnscaledXPosition(head) >= 0) {
                continue;
            }

            horizontal(head);

            // Horizontal position cannot always be determined by FAFOSP-X
            if (graph.getUnscaledXPosition(head) >= 0) {
                // Add neighbours of which horizontal position was not set
                graph.iterator().visitDirectNeighbours(head, SequenceDirection.RIGHT, neighbour -> {
                    if (graph.getUnscaledXPosition(neighbour) < 0) {
                        queue.add(neighbour);
                    }
                });
            }
        }
    }

    /**
     * Calculates the optimal horizontal position relative to its left neighbours for the node with the given
     * identifier.
     *
     * @param id the node's identifier
     */
    private void horizontal(final int id) {
        final int[] width = {-1};
        graph.iterator().visitDirectNeighboursWhile(id, SequenceDirection.LEFT,
                neighbour -> graph.getUnscaledXPosition(neighbour) >= 0,
                ignored -> width[0] = -1,
                neighbour -> {
                    final int newWidth = graph.getUnscaledXPosition(neighbour) + 1;
                    if (newWidth > width[0]) {
                        width[0] = newWidth;
                    }
                }
        );

        if (width[0] >= 0) {
            graph.setUnscaledXPosition(id, width[0] + graph.getSequenceLength(id));
        }
    }


    /**
     * Calculates the optimal vertical position of each node using FAFOSP.
     */
    public void vertical() {
        final int[] meta = new int[nodeArrays.length * 2];

        verticalInit(meta);
        verticalCalculate(meta);
    }

    /**
     * Calculates the "left height" and "right height" properties for all nodes, including the sentinels.
     *
     * @param meta an array to store the left and right heights in
     */
    private void verticalInit(final int[] meta) {
        graph.iterator().visitAll(SequenceDirection.RIGHT,
                node -> meta[2 * node] < 0,
                node -> verticalInit(node, SequenceDirection.LEFT, meta)
        );
        graph.iterator().visitAll(SequenceDirection.LEFT,
                node -> meta[2 * node + 1] < 0,
                node -> verticalInit(node, SequenceDirection.RIGHT, meta)
        );
    }

    /**
     * Calculates the "left height" or "right height" property for the given node, depending on the given direction.
     *
     * @param node      the node's identifier
     * @param direction which height to calculate
     * @param meta      the array to store the height in
     */
    @SuppressWarnings("squid:AssignmentInSubExpressionCheck") // False positive
    private void verticalInit(final int node, final SequenceDirection direction, final int[] meta) {
        final int neighbourSize = graph.getNeighbourCount(node, direction);

        final int[] height = {0};
        if (neighbourSize == 0) {
            height[0] = 2;
        } else if (neighbourSize == 1) {
            final int neighbour = nodeArrays[node][Node.NODE_EDGE_DATA_OFFSET + direction.ternary(
                    2 * graph.getNeighbourCount(node, SequenceDirection.RIGHT),
                    0
            )];

            final int neighbourNeighbourSize = graph.getNeighbourCount(neighbour, direction.opposite());
            if (neighbourNeighbourSize == 1) {
                height[0] = direction.ternary(meta[neighbour * 2], meta[neighbour * 2 + 1]);
            } else {
                height[0] = 2;
            }
        } else {
            graph.iterator().visitDirectNeighbours(node, direction,
                    neighbour -> height[0] += direction.ternary(meta[2 * neighbour], meta[2 * neighbour + 1])
            );
        }

        direction.ternary(
                () -> meta[2 * node] = height[0],
                () -> meta[2 * node + 1] = height[0]
        );
    }

    /**
     * Calculates the vertical positions for all nodes, including the sentinels.
     *
     * @param meta the array to read the left and right heights from
     */
    private void verticalCalculate(final int[] meta) {
        final Queue<Integer> queue = new LinkedList<>();
        queue.add(0);

        while (!queue.isEmpty()) {
            final Integer head = queue.remove();

            // Do not revisit visited nodes
            graph.iterator().visitDirectNeighbours(head, SequenceDirection.RIGHT, neighbour -> {
                if (graph.getUnscaledYPosition(neighbour) < 0) {
                    queue.add(neighbour);
                }
            });

            // Calculate vertical position
            verticalCalculate(meta, head, meta[1] / 2);
        }
    }

    /**
     * Calculates the vertical position for the given node.
     *
     * @param meta            the array to read the left and right heights from
     * @param node            the node's identifier
     * @param defaultPosition the default position if no other is set
     */
    private void verticalCalculate(final int[] meta, final int node, final int defaultPosition) {
        if (graph.getUnscaledYPosition(node) < 0) {
            graph.setUnscaledYPosition(node, defaultPosition);
        }

        if (graph.getNeighbourCount(node, SequenceDirection.RIGHT) == 1) {
            final int neighbour = graph.getNodeArrays()[node][Node.NODE_EDGE_DATA_OFFSET];
            verticalCalculateNeighbour(meta, node, neighbour);
        } else {
            verticalCalculateNeighbours(meta, node);
        }
    }

    /**
     * Calculates the vertical position for the given neighbour relative to the given node.
     *
     * @param meta      the array to read the left and right heights from
     * @param node      the node's identifier
     * @param neighbour the neighbour's identifier
     */
    @SuppressWarnings("squid:AssignmentInSubExpressionCheck") // False positive
    private void verticalCalculateNeighbour(final int[] meta, final int node, final int neighbour) {
        final int neighbourLeftNeighbourSize = graph.getNeighbourCount(neighbour, SequenceDirection.LEFT);

        if (neighbourLeftNeighbourSize == 1) {
            // This node and the neighbour are each other's only neighbour
            graph.setUnscaledYPosition(neighbour, graph.getUnscaledYPosition(node));
        } else {
            // Neighbour has multiple neighbours
            final int[] neighbourLeftNeighboursHeight = {0};

            graph.iterator().visitDirectNeighboursUntil(neighbour, SequenceDirection.LEFT,
                    neighbourLeftNeighbour -> neighbourLeftNeighbour == node,
                    neighbourLeftNeighbour -> neighbourLeftNeighboursHeight[0] += meta[2 * neighbourLeftNeighbour]
            );

            // Calculate the top of the neighbour's left height range
            final int neighbourTop = graph.getUnscaledYPosition(node) - neighbourLeftNeighboursHeight[0];
            // Compensate for differences in height
            final int leftDifference = meta[2 * neighbour] / 2 - meta[2 * node] / 2;

            graph.setUnscaledYPosition(neighbour, neighbourTop + leftDifference);
        }
    }

    /**
     * Calculates the vertical position for all of the node's right neighbours.
     *
     * @param meta the array to read the left and right heights from
     * @param node the node's identifier
     */
    private void verticalCalculateNeighbours(final int[] meta, final int node) {
        final int[] relativeHeight = {graph.getUnscaledYPosition(node) - meta[2 * node + 1] / 2};

        graph.iterator().visitDirectNeighbours(node, SequenceDirection.RIGHT, neighbour -> {
            final int neighbourLeftNeighbourCount = graph.getNeighbourCount(neighbour, SequenceDirection.LEFT);

            if (neighbourLeftNeighbourCount == 1) {
                // Current neighbour has only one neighbour; this neighbour cannot have been visited before
                graph.setUnscaledYPosition(neighbour, relativeHeight[0] + meta[2 * neighbour + 1] / 2);
                relativeHeight[0] += meta[2 * neighbour + 1];
            } else {
                // Current neighbour has multiple neighbours, so the left width is different from the current node
                if (graph.getUnscaledYPosition(neighbour) >= 0) {
                    relativeHeight[0] = graph.getUnscaledYPosition(neighbour) + meta[2 * neighbour] / 2;
                } else {
                    graph.setUnscaledYPosition(neighbour, relativeHeight[0] + meta[2 * neighbour] / 2);
                    relativeHeight[0] += meta[2 * neighbour];
                }
            }
        });
    }
}
