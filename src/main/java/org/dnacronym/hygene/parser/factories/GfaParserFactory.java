package org.dnacronym.hygene.parser.factories;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.parser.GfaParser;


/**
 * Constructs {@code GfaParser} objects.
 */
public final class GfaParserFactory {
    private static @MonotonicNonNull GfaParser instance;


    /**
     * Makes class non instantiable.
     */
    private GfaParserFactory() {
    }


    /**
     * Sets a custom instance of {@code GfaParser}.
     *
     * @param gfaParser custom instance of {@code GfaParser}
     */
    public static void setInstance(final GfaParser gfaParser) {
        instance = gfaParser;
    }

    /**
     * Returns a new instance of {@code GfaParser} if no preferred instance is set.
     *
     * @return an instance of {@code GfaParser}
     */
    public static GfaParser getInstance() {
        if (instance == null) {
            return new GfaParser();
        }

        return instance;
    }
}
