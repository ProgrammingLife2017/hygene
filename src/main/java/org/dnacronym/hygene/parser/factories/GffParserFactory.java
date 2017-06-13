package org.dnacronym.hygene.parser.factories;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.parser.GffParser;


/**
 * Constructs {@link GffParser} objects.
 */
public final class GffParserFactory {
    private static @MonotonicNonNull GffParser instance;


    /**
     * Makes class non instantiable.
     */
    private GffParserFactory() {
    }


    /**
     * Sets a custom instance of {@link GffParser}.
     *
     * @param gffParser custom instance of {@link GffParser}
     */
    public static void setInstance(final GffParser gffParser) {
        instance = gffParser;
    }

    /**
     * Returns a new instance of {@link GffParser} if no preferred instance is set.
     *
     * @return an instance of {@link GffParser}
     */
    public static GffParser createInstance() {
        if (instance == null) {
            return new GffParser();
        }

        return instance;
    }
}
