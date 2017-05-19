package org.dnacronym.hygene.parser.factories;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.parser.NewGfaParser;


/**
 * Constructs {@link NewGfaParser} objects.
 */
public final class NewGfaParserFactory {
    private static @MonotonicNonNull NewGfaParser instance;


    /**
     * Makes class non instantiable.
     */
    private NewGfaParserFactory() {
    }


    /**
     * Sets a custom instance of {@link NewGfaParser}.
     *
     * @param gfaParser custom instance of {@link NewGfaParser}
     */
    public static void setInstance(final NewGfaParser gfaParser) {
        instance = gfaParser;
    }

    /**
     * Returns a new instance of {@link NewGfaParser} if no preferred instance is set.
     *
     * @return an instance of {@link NewGfaParser}
     */
    public static NewGfaParser createInstance() {
        if (instance == null) {
            return new NewGfaParser();
        }

        return instance;
    }
}
