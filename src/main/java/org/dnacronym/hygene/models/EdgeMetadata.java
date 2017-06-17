package org.dnacronym.hygene.models;

/**
 * Represents the metadata of an edge.
 */
public final class EdgeMetadata {
    private final String fromOrient;
    private final String toOrient;
    private final String overlap;


    /**
     * Constructs and initializes an {@link EdgeMetadata} object.
     *
     * @param fromOrient the orient of the from node
     * @param toOrient   the orient of the to node
     * @param overlap    the overlap
     */
    public EdgeMetadata(final String fromOrient, final String toOrient, final String overlap) {
        this.fromOrient = fromOrient;
        this.toOrient = toOrient;
        this.overlap = overlap;
    }


    /**
     * Gets the orient of the from node.
     *
     * @return the orient of the from node
     */
    public String getFromOrient() {
        return fromOrient;
    }

    /**
     * Gets the orient of the to node.
     *
     * @return the orient of the to node
     */
    public String getToOrient() {
        return toOrient;
    }

    /**
     * Gets the overlap.
     *
     * @return the overlap
     */
    public String getOverlap() {
        return overlap;
    }
}
