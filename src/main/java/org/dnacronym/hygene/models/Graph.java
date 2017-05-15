package org.dnacronym.hygene.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.dnacronym.hygene.parser.NewGfaFile;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * Class wraps around the graph data represented as a nested array and provides utility methods.
 * <p>
 * Node array format:
 * [[nodeLineNumber, nodeColor, outgoingEdges, xPosition, yPosition, edge1, edge1LineNumber...]]
 */
public final class Graph {
    private final int[][] nodeArrays;
    private final NewGfaFile gfaFile;


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
     * @return the {@link Node}'s line number.
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
     * Getter for the unscaled y position.
     *
     * @param id the {@link Node}'s id
     * @return the unscaled y position.
     */
    public int getUnscaledYPosition(final int id) {
        return nodeArrays[id][Node.UNSCALED_Y_POSITION_INDEX];
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
                        - nodeArrays[id][Node.NODE_OUTGOING_EDGES_INDEX]
                        - (Node.NODE_OUTGOING_EDGES_INDEX + 1)
                ) / Node.EDGE_DATA_SIZE,
                nodeArrays[id][Node.NODE_OUTGOING_EDGES_INDEX]
        );
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
                + direction.ternary(getNeighbourCount(id, direction.opposite()), 0);

        for (int i = 0; i < getNeighbourCount(id, direction); i++) {
            final int neighbourIndex = neighbourOffset + 2 * i;
            action.accept(nodeArrays[id][neighbourIndex]);
        }
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

    /**
     * Getter for the {@code GfaFile} instance where the graph belongs to.
     *
     * @return the {@code GfaFile} instance where the graph belongs to.
     */
    public NewGfaFile getGfaFile() {
        return gfaFile;
    }
}
