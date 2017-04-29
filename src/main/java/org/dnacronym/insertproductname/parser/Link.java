package org.dnacronym.insertproductname.parser;


/**
 * .
 *
 * @author Felix Dekker
 */
public final class Link {
    private final Segment from;
    private final boolean fromOrient;
    private final Segment to;
    private final boolean toOrient;
    private final int overlap;


    public Link(final Segment from, final boolean fromOrient, final Segment to, final boolean toOrient, final int overlap) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Link segments cannot be null");
        }
        if (overlap < 0) {
            throw new IllegalArgumentException("Link overlap must be at least 0");
        }
        if (overlap > from.getLength() || overlap > to.getLength()) {
            // TODO replace this with a more appropriate exception
            throw new IllegalArgumentException("Link overlap cannot be larger than either segment");
        }

        this.from = from;
        this.fromOrient = fromOrient;
        this.to = to;
        this.toOrient = toOrient;
        this.overlap = overlap;
    }


    public Segment getFrom() {
        return from;
    }

    public boolean getFromOrient() {
        return fromOrient;
    }

    public Segment getTo() {
        return to;
    }

    public boolean getToOrient() {
        return toOrient;
    }

    public int getOverlap() {
        return overlap;
    }
}
