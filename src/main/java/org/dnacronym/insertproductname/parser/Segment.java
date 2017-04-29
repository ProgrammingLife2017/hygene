package org.dnacronym.insertproductname.parser;


/**
 * .
 *
 * @author Felix Dekker
 */
public final class Segment {
    private final String name;
    private final String sequence;


    public Segment(final String name, final String sequence) {
        if (name == null || sequence == null) {
            throw new IllegalArgumentException("Segment name and sequence must be non-null");
        }

        this.name = name;
        this.sequence = sequence;
    }


    public String getName() {
        return name;
    }

    public String getSequence() {
        return sequence;
    }

    public int getLength() {
        return sequence.length();
    }

    public String getReversedSequence() {
        return new StringBuilder(sequence).reverse().toString();
    }
}
