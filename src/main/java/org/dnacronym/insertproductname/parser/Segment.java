package org.dnacronym.insertproductname.parser;


/**
 * A {@code Segment} is a sequence of bases.
 *
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class Segment {
    private final String name;
    private final String sequence;


    /**
     * Constructs a new {@code Segment}.
     *
     * @param name     the name
     * @param sequence the sequence
     */
    public Segment(final String name, final String sequence) {
        this.name = name;
        this.sequence = sequence;
    }


    /**
     * Returns the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the sequence.
     *
     * @return the sequence.
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Returns the sequence length. This is the same as calling {@code getSequence().length()}.
     *
     * @return the sequence length.
     */
    public int getLength() {
        return sequence.length();
    }

    /**
     * Returns the reversed sequence.
     *
     * @return the reversed sequence.
     */
    public String getReversedSequence() {
        return new StringBuilder(sequence).reverse().toString();
    }
}
