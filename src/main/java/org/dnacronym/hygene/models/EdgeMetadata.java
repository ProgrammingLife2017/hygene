package org.dnacronym.hygene.models;

import org.dnacronym.hygene.parser.ParseException;

import java.util.Optional;

/**
 * Represents the metadata of a {@link Edge}.
 */
public final class EdgeMetadata {
    private String fromOrient;
    private String toOrient;
    private String overlap;


    /**
     * Constructs and initializes a {@link Edge} object.
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
     * Retrieves metadata of the given edge.
     *
     * @param edge the edge to retrieve metadata of
     * @return metadata of the edge.
     * @throws ParseException if the edge metadata cannot be parsed
     */
    public static EdgeMetadata retrieveFor(final Edge edge) throws ParseException {
        return Optional.ofNullable(edge.getGraph())
                .orElseThrow(() -> new ParseException("Cannot parse metadata for edge because graph is unknown"))
                .getGfaFile()
                .parseEdgeMetadata(edge.getLineNumber());
    }


    /**
     * Gets the orient of the from node.
     *
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
