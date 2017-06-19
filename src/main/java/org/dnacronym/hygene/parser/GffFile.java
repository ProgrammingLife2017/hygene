package org.dnacronym.hygene.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.graph.annotation.AnnotationCollection;
import org.dnacronym.hygene.parser.factories.GffParserFactory;


/**
 * Represents a GFF file with its contents and metadata.
 */
public final class GffFile {
    private static final Logger LOGGER = LogManager.getLogger(GffFile.class);

    private final String fileName;
    private final GffParser gffParser;
    private @MonotonicNonNull AnnotationCollection annotationCollection;


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
     * Parses the GFF file into a {@link AnnotationCollection}.
     *
     * @param progressUpdater a {@link ProgressUpdater} to notify interested parties on progress updates
     * @return a {@link AnnotationCollection} based on the contents of the GFF file
     * @throws GffParseException if the file content is not GFF-compliant file
     */
    public AnnotationCollection parse(final ProgressUpdater progressUpdater) throws GffParseException {
        LOGGER.info("Start parsing " + fileName);
        final AnnotationCollection annotation = gffParser.parse(fileName, progressUpdater);
        LOGGER.info("Finished parsing " + fileName);

        this.annotationCollection = annotation;
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
    public AnnotationCollection getAnnotationCollection() {
        if (annotationCollection == null) {
            throw new IllegalStateException("Cannot get the " + AnnotationCollection.class.getSimpleName()
                    + " before parsing the file.");
        }
        return annotationCollection;
    }
}
