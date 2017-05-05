package org.dnacronym.insertproductname.parser.factories;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.insertproductname.parser.SequenceAlignmentGraphParser;


/**
 * Constructs {@code SequenceAlignmentGraphParser} objects.
 */
public final class SequenceAlignmentGraphParserFactory {
    private static @MonotonicNonNull SequenceAlignmentGraphParser instance;


    /**
     * Makes class non instantiable.
     */
    private SequenceAlignmentGraphParserFactory() {
    }


    /**
     * Sets a custom instance of {@code SequenceAlignmentGraphParser}.
     *
     * @param sagParser custom instance of {@code SequenceAlignmentGraphParser}
     */
    public static void setInstance(final SequenceAlignmentGraphParser sagParser) {
        instance = sagParser;
    }

    /**
     * Returns a new instance of {@code SequenceAlignmentGraphParser} if no preferred instance is set.
     *
     * @return an instance of {@code SequenceAlignmentGraphParser}
     */
    public static SequenceAlignmentGraphParser getInstance() {
        if (instance == null) {
            return new SequenceAlignmentGraphParser();
        }

        return instance;
    }
}
