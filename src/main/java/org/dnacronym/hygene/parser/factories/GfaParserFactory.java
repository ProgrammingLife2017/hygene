package org.dnacronym.hygene.parser.factories;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.parser.GfaParser;


/**
 * Constructs {@link GfaParser} objects.
 */
public final class GfaParserFactory {
    private static @MonotonicNonNull GfaParser instance;


    /**
     * Makes class non instantiable.
     */
    private GfaParserFactory() {
    }


    /**
     * Sets a custom instance of {@link GfaParser}.
     *
     * @param gfaParser custom instance of {@link GfaParser}
     */
    public static void setInstance(final GfaParser gfaParser) {
        instance = gfaParser;
    }

    /**
     * Returns a new instance of {@link GfaParser} if no preferred instance is set.
     *
     * @return an instance of {@link GfaParser}
     */
    public static GfaParser createInstance() {
        if (instance == null) {
            return new GfaParser();
        }

        return instance;
    }
}
