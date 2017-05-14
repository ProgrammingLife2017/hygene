package org.dnacronym.hygene.parser.factories;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.parser.SequenceAlignmentGraphParser;


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
    public static SequenceAlignmentGraphParser createInstance() {
        if (instance == null) {
            return new SequenceAlignmentGraphParser();
        }

        return instance;
    }
}
