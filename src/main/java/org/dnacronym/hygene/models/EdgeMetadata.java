package org.dnacronym.hygene.models;

public class EdgeMetadata {
    private String fromOrient;
    private String toOrient;
    private String overlap;

    public EdgeMetadata(String fromOrient, String toOrient, String overlap) {
        this.fromOrient = fromOrient;
        this.toOrient = toOrient;
        this.overlap = overlap;
    }

    public String getFromOrient() {
        return fromOrient;
    }

    public String getToOrient() {
        return toOrient;
    }

    public String getOverlap() {
        return overlap;
    }
}
