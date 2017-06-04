package org.dnacronym.hygene.graph;


/**
 * Class representing a generic edge.
 */
public abstract class AbstractEdge {
    private final AbstractNode from;
    private final AbstractNode to;


    /**
     * Constructs a new {@link AbstractEdge} instance.
     *
     * @param from the source of the edge
     * @param to   the destination of the edge
     */
    public AbstractEdge(final AbstractNode from, final AbstractNode to) {
        this.from = from;
        this.to = to;
    }


    /**
     * Returns the source node.
     *
     * @return the source node
     */
    public AbstractNode getFrom() {
        return from;
    }

    /**
     * Returns the destination node.
     *
     * @return the destination node
     */
    public AbstractNode getTo() {
        return to;
    }
}
