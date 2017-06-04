package org.dnacronym.hygene.graph;


/**
 * Class representing a generic edge.
 */
public class GenericEdge {
    private final GenericNode from;
    private final GenericNode to;


    /**
     * Constructs a new {@link GenericEdge} instance.
     *
     * @param from the source of the edge
     * @param to   the destination of the edge
     */
    protected GenericEdge(final GenericNode from, final GenericNode to) {
        this.from = from;
        this.to = to;
    }


    /**
     * Returns the source node.
     *
     * @return the source node
     */
    public final GenericNode getFrom() {
        return from;
    }

    /**
     * Returns the destination node.
     *
     * @return the destination node
     */
    public final GenericNode getTo() {
        return to;
    }
}
