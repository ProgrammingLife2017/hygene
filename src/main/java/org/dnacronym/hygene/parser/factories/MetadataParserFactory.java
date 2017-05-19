package org.dnacronym.hygene.parser.factories;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.parser.GfaParser;
import org.dnacronym.hygene.parser.MetadataParser;


/**
 * Constructs {@link MetadataParser} objects.
 */
public final class MetadataParserFactory {
    private static @MonotonicNonNull MetadataParser instance;


    /**
     * Makes class non instantiable.
     */
    private MetadataParserFactory() {
    }


    /**
     * Sets a custom instance of {@link MetadataParser}.
     *
     * @param metadataParser custom instance of {@link MetadataParser}
     */
    public static void setInstance(final MetadataParser metadataParser) {
        instance = metadataParser;
    }

    /**
     * Returns a new instance of {@link GfaParser} if no preferred instance is set.
     *
     * @return an instance of {@link GfaParser}
     */
    public static MetadataParser createInstance() {
        if (instance == null) {
            return new MetadataParser();
        }

        return instance;
    }
}
