package org.dnacronym.hygene.graph;


/**
 * Class representing a generic edge.
 */
public class Edge {
    private final Node from;
    private final Node to;


    /**
     * Constructs a new {@link Edge} instance.
     *
     * @param from the source of the edge
     * @param to   the destination of the edge
     */
    protected Edge(final Node from, final Node to) {
        this.from = from;
        this.to = to;
    }


    /**
     * Returns the source node.
     *
     * @return the source node
     */
    public final Node getFrom() {
        return from;
    }

    /**
     * Returns the destination node.
     *
     * @return the destination node
     */
    public final Node getTo() {
        return to;
    }
}
