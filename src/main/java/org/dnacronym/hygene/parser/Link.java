package org.dnacronym.hygene.parser;


/**
 * A {@code Link} connects two {@code Segment}s, possibly with some overlap.
 *
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class Link {
    private final String from;
    private final String to;
    private final int overlap;


    /**
     * Constructs a new {@code Link}.
     * <p>
     * The orientation of a {@code Segment} indicates how the segment is placed in the link. If the orientation is
     * {@code false}, it is treated in reverse and is inverted.
     *
     * @param from    the {@code Segment}. Cannot be {@code null}
     * @param to      the {@code Segment}. Cannot be {@code null}
     * @param overlap the number of bases that overlap between the two {@code Segment}s. Must be positive
     */
    public Link(final String from, final String to, final int overlap) {
        if (overlap < 0) {
            throw new IllegalArgumentException("Link overlap must be at least 0.");
        }

        this.from = from;
        this.to = to;
        this.overlap = overlap;
    }


    /**
     * Returns the from {@code Segment}.
     *
     * @return the from {@code Segment}
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the to {@code Segment}.
     *
     * @return the to {@code Segment}
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the number of overlapping bases in this {@code Link}.
     *
     * @return the number of overlapping bases in this {@code Link}
     */
    public int getOverlap() {
        return overlap;
    }
}
