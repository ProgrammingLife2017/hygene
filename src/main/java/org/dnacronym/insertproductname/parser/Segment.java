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
        this.name = name;
        this.sequence = sequence;
    }


    public String getName() {
        return name;
    }

    public String getSequence() {
        return sequence;
    }

    public String getReversedSequence() {
        return new StringBuilder(sequence).reverse().toString();
    }
}
