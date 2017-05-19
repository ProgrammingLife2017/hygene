package org.dnacronym.hygene.parser;


/**
 * A {@link Link} connects two {@link Segment}s, possibly with some overlap.
 *
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class Link {
    private final String from;
    private final String to;
    private final int overlap;


    /**
     * Constructs a new {@link Link}.
     * <p>
     * The orientation of a {@link Segment} indicates how the segment is placed in the link. If the orientation is
     * {@code false}, it is treated in reverse and is inverted.
     *
     * @param from    the {@link Segment}. Cannot be {@code null}
     * @param to      the {@link Segment}. Cannot be {@code null}
     * @param overlap the number of bases that overlap between the two {@link Segment}s. Must be positive
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
     * Returns the from {@link Segment}.
     *
     * @return the from {@link Segment}
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the to {@link Segment}.
     *
     * @return the to {@link Segment}
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the number of overlapping bases in this {@link Link}.
     *
     * @return the number of overlapping bases in this {@link Link}
     */
    public int getOverlap() {
        return overlap;
    }
}
