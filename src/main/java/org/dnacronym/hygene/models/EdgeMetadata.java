package org.dnacronym.hygene.models;

/**
 * Represents the metadata of a {@link Edge}.
 */
public class EdgeMetadata {
    private String fromOrient;
    private String toOrient;
    private String overlap;


    /**
     * Constructs and initializes a {@link Edge} object.
     *
     * @param fromOrient the orient of the from node
     * @param toOrient the orient of the to node
     * @param overlap the overlap
     */
    public EdgeMetadata(String fromOrient, String toOrient, String overlap) {
        this.fromOrient = fromOrient;
        this.toOrient = toOrient;
        this.overlap = overlap;
    }


    /**
     * Gets the orient of the from node.
     * @return the orient of the from node.
     */
    public String getFromOrient() {
        return fromOrient;
    }

    /**
     * Gets the orient of the to node.
     *
     * @return the orient of the to node.
     */
    public String getToOrient() {
        return toOrient;
    }

    /**
     * Gets the overlap.
     *
     * @return the overlap.
     */
    public String getOverlap() {
        return overlap;
    }
}
