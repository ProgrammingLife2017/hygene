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
        this.from = from;
        this.fromOrient = fromOrient;
        this.to = to;
        this.toOrient = toOrient;
        this.overlap = overlap;
    }


    public Segment getFrom() {
        return from;
    }

    public boolean isFromOrient() {
        return fromOrient;
    }

    public Segment getTo() {
        return to;
    }

    public boolean isToOrient() {
        return toOrient;
    }

    public int getOverlap() {
        return overlap;
    }
}
