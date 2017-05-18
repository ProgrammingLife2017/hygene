package org.dnacronym.hygene.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.parser.NewGfaFile;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Class wraps around the graph data represented as a nested array and provides utility methods.
 * <p>
 * Node array format:
 * [[nodeLineNumber, sequenceLength, nodeColor, outgoingEdges, xPosition, yPosition, edge1, edge1LineNumber...]]
 */
public final class Graph {
    private final int[][] nodeArrays;
    private final NewGfaFile gfaFile;

    private @MonotonicNonNull GraphIterator iterator;


    /**
     * Constructs a graph from array based data structure.
     *
     * @param nodeArrays nested array containing the graphs data
     * @param gfaFile    a reference to the GFA file from which the graph is created
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "For performance reasons, we don't want to create a copy here"
    )
    public Graph(final int[][] nodeArrays, final NewGfaFile gfaFile) {
        this.nodeArrays = nodeArrays;
        this.gfaFile = gfaFile;
    }


    /**
     * Creates a new {@link Node} object containing a reference to the array of that node inside the graph array.
     *
     * @param id the id of the node
     * @return the created {@link Node} object
     */
    public Node getNode(final int id) {
        return new Node(id, nodeArrays[id], this);
    }

    /**
     * Getter for the array representing a {@link Node}'s metadata.
     *
     * @param id the {@link Node}'s id
     * @return the array representing a {@link Node}'s metadata.
     */
    public int[] getNodeArray(final int id) {
        return nodeArrays[id];
    }

    /**
     * Gets the array representation of all node arrays.
     *
     * @return the array representation of all node arrays.
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "For performance reasons, we don't want to create a copy here"
    )
    public int[][] getNodeArrays() {
        return nodeArrays;
    }

    /**
     * Getter for the line number where the {@link Node}'s metadata resides.
     *
     * @param id the {@link Node}'s id
     * @return the {@link Node}'s line number.
     */
    public int getLineNumber(final int id) {
        return nodeArrays[id][Node.NODE_LINE_NUMBER_INDEX];
    }

    /**
     * Getter for the sequence length of a {@link Node}.
     *
     * @param id the {@link Node}'s id
     * @return the {@link Node}'s sequence length.
     */
    public int getSequenceLength(final int id) {
        return nodeArrays[id][Node.NODE_SEQUENCE_LENGTH_INDEX];
    }

    /**
     * Getter for the color of a {@link Node}.
     *
     * @param id the {@link Node}'s id
     * @return the {@link Node}'s color.
     */
    public NodeColor getColor(final int id) {
        return NodeColor.values()[nodeArrays[id][Node.NODE_COLOR_INDEX]];
    }

    /**
     * Getter for the unscaled x position.
     *
     * @param id the {@link Node}'s id
     * @return the unscaled x position.
     */
    public int getUnscaledXPosition(final int id) {
        return nodeArrays[id][Node.UNSCALED_X_POSITION_INDEX];
    }

    /**
     * Sets the unscaled x position.
     *
     * @param id                the {@link Node}'s id
     * @param unscaledXPosition the unscaled x position
     */
    void setUnscaledXPosition(final int id, final int unscaledXPosition) {
        nodeArrays[id][Node.UNSCALED_X_POSITION_INDEX] = unscaledXPosition;
    }

    /**
     * Getter for the unscaled y position.
     *
     * @param id the {@link Node}'s id
     * @return the unscaled y position.
     */
    public int getUnscaledYPosition(final int id) {
        return nodeArrays[id][Node.UNSCALED_Y_POSITION_INDEX];
    }

    /**
     * Sets the unscaled y position.
     *
     * @param id                the node's id
     * @param unscaledYPosition the unscaled y position
     */
    void setUnscaledYPosition(final int id, final int unscaledYPosition) {
        nodeArrays[id][Node.UNSCALED_Y_POSITION_INDEX] = unscaledYPosition;
    }

    /**
     * Returns the number of neighbours of a node in the given direction.
     *
     * @param id        the node's identifier
     * @param direction the direction of neighbours to count
     * @return the number of neighbours of a node in the given direction.
     */
    public int getNeighbourCount(final int id, final SequenceDirection direction) {
        return direction.ternary(
                (nodeArrays[id].length
                        - nodeArrays[id][Node.NODE_OUTGOING_EDGES_INDEX] * Node.EDGE_DATA_SIZE
                        - (Node.NODE_OUTGOING_EDGES_INDEX + 1)
                ) / Node.EDGE_DATA_SIZE,
                nodeArrays[id][Node.NODE_OUTGOING_EDGES_INDEX]
        );
    }

    /**
     * Returns the {@link GraphIterator} for this {@link Graph} for iterating over its node.
     *
     * @return the {@link GraphIterator} for this {@link Graph} for iterating over its node
     */
    @EnsuresNonNull("iterator")
    public GraphIterator iterator() {
        if (iterator == null) {
            iterator = new GraphIterator(this);
        }
        return iterator;
    }

    /**
     * Returns a new {@link Fafosp} for invoking FAFOSP-related methods.
     * <p>
     * FAFOSP is the Felix Algorithm For Optimal Segment Positioning.
     *
     * @return a new {@link Fafosp}
     */
    Fafosp fafosp() {
        return new Fafosp(this);
    }

    /**
     * Calculates the optimal vertical position of each node using FAFOSP.
     */
    public void fafospY() {
        final int[] meta = new int[nodeArrays.length * 2];

        fafospYInit(meta);
        fafospYCalculate(meta);
    }

    /**
     * Calculates the "left height" and "right height" properties for all nodes, including the sentinels.
     *
     * @param meta an array to store the left and right heights in
     */
    private void fafospYInit(final int[] meta) {
        iterator().visitAll(SequenceDirection.RIGHT,
                node -> meta[2 * node] < 0,
                node -> fafospYInit(node, SequenceDirection.LEFT, meta)
        );
        iterator().visitAll(SequenceDirection.LEFT,
                node -> meta[2 * node + 1] < 0,
                node -> fafospYInit(node, SequenceDirection.RIGHT, meta)
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
    private void fafospYInit(final int node, final SequenceDirection direction, final int[] meta) {
        final int neighbourSize = getNeighbourCount(node, direction);

        final int[] height = {0};
        if (neighbourSize == 0) {
            height[0] = 2;
        } else if (neighbourSize == 1) {
            final int neighbour = nodeArrays[node][Node.NODE_EDGE_DATA_OFFSET + direction.ternary(
                    2 * getNeighbourCount(node, SequenceDirection.RIGHT),
                    0
            )];

            final int neighbourNeighbourSize = getNeighbourCount(neighbour, direction.opposite());
            if (neighbourNeighbourSize == 1) {
                height[0] = direction.ternary(meta[neighbour * 2], meta[neighbour * 2 + 1]);
            } else {
                height[0] = 2;
            }
        } else {
            iterator().visitDirectNeighbours(node, direction,
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
    private void fafospYCalculate(final int[] meta) {
        final Queue<Integer> queue = new LinkedList<>();
        queue.add(0);

        while (!queue.isEmpty()) {
            final Integer head = queue.remove();

            // Do not revisit visited nodes
            iterator().visitDirectNeighbours(head, SequenceDirection.RIGHT, neighbour -> {
                if (getUnscaledYPosition(neighbour) < 0) {
                    queue.add(neighbour);
                }
            });

            // Calculate vertical position
            fafospYCalculate(meta, head, meta[1] / 2);
        }
    }

    /**
     * Calculates the vertical position for the given node.
     *
     * @param meta            the array to read the left and right heights from
     * @param node            the node's identifier
     * @param defaultPosition the default position if no other is set
     */
    private void fafospYCalculate(final int[] meta, final int node, final int defaultPosition) {
        if (getUnscaledYPosition(node) < 0) {
            setUnscaledYPosition(node, defaultPosition);
        }

        if (getNeighbourCount(node, SequenceDirection.RIGHT) == 1) {
            final int neighbour = nodeArrays[node][Node.NODE_EDGE_DATA_OFFSET];
            fafospYCalculateNeighbour(meta, node, neighbour);
        } else {
            fafospYCalculateNeighbours(meta, node);
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
    private void fafospYCalculateNeighbour(final int[] meta, final int node, final int neighbour) {
        final int neighbourLeftNeighbourSize = getNeighbourCount(neighbour, SequenceDirection.LEFT);

        if (neighbourLeftNeighbourSize == 1) {
            // This node and the neighbour are each other's only neighbour
            setUnscaledYPosition(neighbour, getUnscaledYPosition(node));
        } else {
            // Neighbour has multiple neighbours
            final int[] neighbourLeftNeighboursHeight = {0};

            iterator().visitDirectNeighboursUntil(neighbour, SequenceDirection.LEFT,
                    neighbourLeftNeighbour -> neighbourLeftNeighbour == node,
                    neighbourLeftNeighbour -> neighbourLeftNeighboursHeight[0] += meta[2 * neighbourLeftNeighbour]
            );

            // Calculate the top of the neighbour's left height range
            final int neighbourTop = getUnscaledYPosition(node) - neighbourLeftNeighboursHeight[0];
            // Compensate for differences in height
            final int leftDifference = meta[2 * neighbour] / 2 - meta[2 * node] / 2;

            setUnscaledYPosition(neighbour, neighbourTop + leftDifference);
        }
    }

    /**
     * Calculates the vertical position for all of the node's right neighbours.
     *
     * @param meta the array to read the left and right heights from
     * @param node the node's identifier
     */
    private void fafospYCalculateNeighbours(final int[] meta, final int node) {
        final int[] relativeHeight = {getUnscaledYPosition(node) - meta[2 * node + 1] / 2};

        iterator().visitDirectNeighbours(node, SequenceDirection.RIGHT, neighbour -> {
            final int neighbourLeftNeighbourCount = getNeighbourCount(neighbour, SequenceDirection.LEFT);

            if (neighbourLeftNeighbourCount == 1) {
                // Current neighbour has only one neighbour
                if (getUnscaledYPosition(neighbour) >= 0) {
                    relativeHeight[0] = getUnscaledYPosition(neighbour) + meta[2 * neighbour + 1] / 2;
                } else {
                    setUnscaledYPosition(neighbour, relativeHeight[0] + meta[2 * neighbour + 1] / 2);
                    relativeHeight[0] += meta[2 * neighbour + 1];
                }
            } else {
                // Current neighbour has multiple neighbours, so the left width is different from the current node
                if (getUnscaledYPosition(neighbour) >= 0) {
                    relativeHeight[0] = getUnscaledYPosition(neighbour) + meta[2 * neighbour] / 2;
                } else {
                    setUnscaledYPosition(neighbour, relativeHeight[0] + meta[2 * neighbour] / 2);
                    relativeHeight[0] += meta[2 * neighbour];
                }
            }
        });
    }

    /**
     * Getter for the {@code GfaFile} instance where the graph belongs to.
     *
     * @return the {@code GfaFile} instance where the graph belongs to.
     */
    public NewGfaFile getGfaFile() {
        return gfaFile;
    }
}
