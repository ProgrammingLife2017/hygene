package org.dnacronym.insertproductname.parser.factories;

import org.dnacronym.insertproductname.parser.GFAParser;


/**
 * Constructs {@code GFAParser} objects.
 */
public final class GFAParserFactory {
    private static GFAParser instance;


    /**
     * Makes class non instantiable.
     */
    private GFAParserFactory() {
    }


    /**
     * Sets a custom instance of {@code GFAParser}.
     *
     * @param gfaParser custom instance of {@code GFAParser}
     */
    public static void setInstance(final GFAParser gfaParser) {
        instance = gfaParser;
    }

    /**
     * Returns a new instance of {@code GFAParser} if no preferred instance is set.
     *
     * @return an instance of {@code GFAParser}
     */
    public static GFAParser getInstance() {
        if (instance == null) {
            return new GFAParser();
        }

        return instance;
    }
}
