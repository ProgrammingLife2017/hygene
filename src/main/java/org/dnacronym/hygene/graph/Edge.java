package org.dnacronym.hygene.graph;


/**
 * Class representing a single, non-artificial edge.
 */
public final class Edge extends GenericEdge {
    private final int lineNumber;


    /**
     * Constructs a new {@link Edge} instance.
     *
     * @param from       the source of the edge
     * @param to         the destination of the edge
     * @param lineNumber the number of the corresponding link in the GFA file this edge was defined in
     */
    public Edge(final GenericNode from, final GenericNode to, final int lineNumber) {
        super(from, to);
        this.lineNumber = lineNumber;
    }


    /**
     * Returns the line number.
     *
     * @return the line number
     */
    public int getLineNumber() {
        return lineNumber;
    }
}
