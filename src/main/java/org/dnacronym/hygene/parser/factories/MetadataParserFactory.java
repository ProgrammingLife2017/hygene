package org.dnacronym.hygene.parser.factories;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.parser.MetadataParser;


/**
 * Constructs {@code MetadataParser} objects.
 */
public final class MetadataParserFactory {
    private static @MonotonicNonNull MetadataParser instance;


    /**
     * Makes class non instantiable.
     */
    private MetadataParserFactory() {
    }


    /**
     * Sets a custom instance of {@code MetadataParser}.
     *
     * @param metadataParser custom instance of {@code MetadataParser}
     */
    public static void setInstance(final MetadataParser metadataParser) {
        instance = metadataParser;
    }

    /**
     * Returns a new instance of {@code GfaParser} if no preferred instance is set.
     *
     * @return an instance of {@code GfaParser}
     */
    public static MetadataParser getInstance() {
        if (instance == null) {
            return new MetadataParser();
        }

        return instance;
    }
}
