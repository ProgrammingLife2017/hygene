package org.dnacronym.hygene.graph;


/**
 * Class representing a single, non-dummy edge.
 */
@SuppressWarnings("squid:S2160") // Superclass equals/hashCode use UUID, which is unique enough
public final class Link extends Edge {
    private final int lineNumber;


    /**
     * Constructs a new {@link Link} instance.
     *
     * @param from       the source of the edge
     * @param to         the destination of the edge
     * @param lineNumber the number of the corresponding link in the GFA file this edge was defined in
     */
    public Link(final NewNode from, final NewNode to, final int lineNumber) {
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
