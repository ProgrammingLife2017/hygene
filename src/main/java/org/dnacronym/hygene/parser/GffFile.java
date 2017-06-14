package org.dnacronym.hygene.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.models.FeatureAnnotation;
import org.dnacronym.hygene.parser.factories.GffParserFactory;


/**
 * Represents a GFF file with its contents and metadata.
 */
public final class GffFile {
    private static final Logger LOGGER = LogManager.getLogger(GffFile.class);

    private final String fileName;
    private final GffParser gffParser;
    private @MonotonicNonNull FeatureAnnotation featureAnnotation;


    /**
     * Constructs a new {@link GffFile}.
     *
     * @param fileName the name of the GFF file
     */
    public GffFile(final String fileName) {
        this.fileName = fileName;
        gffParser = GffParserFactory.createInstance();
    }


    /**
     * Parses the GFF file into a {@link FeatureAnnotation}.
     *
     * @param progressUpdater a {@link ProgressUpdater} to notify interested parties on progress updates
     * @return a {@link FeatureAnnotation} based on the contents of the GFF file
     * @throws ParseException if the file content is not GFF-compliant file
     */
    public FeatureAnnotation parse(final ProgressUpdater progressUpdater) throws ParseException {
        LOGGER.info("Start parsing " + fileName);
        final FeatureAnnotation annotation = gffParser.parse(fileName, progressUpdater);
        LOGGER.info("Finished parsing " + fileName);

        this.featureAnnotation = annotation;
        return annotation;
    }

    /**
     * Returns the name of the GFF file.
     *
     * @return the name of the GFF file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the contents of the GFF file.
     * <p>
     * May only be called after the file has been parsed.
     *
     * @return the contents of the GFF file
     */
    public FeatureAnnotation getFeatureAnnotation() {
        if (featureAnnotation == null) {
            throw new IllegalStateException("Cannot get the " + FeatureAnnotation.class.getSimpleName()
                    + " before parsing the file.");
        }
        return featureAnnotation;
    }
}
