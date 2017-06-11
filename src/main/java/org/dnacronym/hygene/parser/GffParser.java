package org.dnacronym.hygene.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.models.FeatureAnnotation;
import org.dnacronym.hygene.models.SubFeatureAnnotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Parses GFF files.
 * <p>
 * These files become {@link FeatureAnnotation}s.
 *
 * @see <a href="https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md">GFF v3 specification</a>
 * @see FeatureAnnotation
 */
public final class GffParser {
    private static final Logger LOGGER = LogManager.getLogger(GffParser.class);
    private static final String GFF_VERSION_HEADER = "##gff-version 3.2.1";
    private static final int GFF_COLUMNS = 9;

    private static final int SEQ_ID_COLUMN = 0;
    private static final int SOURCE_COLUMN = 1;
    private static final int TYPE_COLUMN = 2;
    private static final int START_COLUMN = 3;
    private static final int END_COLUMN = 4;
    private static final int SCORE_COLUMN = 5;
    private static final int STRAND_COLUMN = 6;
    private static final int PHASE_COLUMN = 7;
    private static final int ATTRIBUTES_COLUMN = 8;


    /**
     * Parses a GFF file.
     * <p>
     * Firstly checks that the files starts with the '{@value GFF_VERSION_HEADER}'.<br>
     * Afterwards, starts parsing the file. Blank lines and lines starting with '#' are ignored. Lines starting with
     * '##' are added as file meta-data to the {@link FeatureAnnotation}.
     * <p>
     * All other lines are parsed and converted to {@link SubFeatureAnnotation}s to be stored in the
     * {@link FeatureAnnotation}. Only the first encountered seqid is used to construct a {@link FeatureAnnotation}, all
     * subsequent seqid's are ignored and assumed to be correct.
     *
     * @param gffFile the path of the GFF file to parse
     * @return a {@link FeatureAnnotation} representing the GFF file
     * @throws ParseException if unable to parse the {@link java.io.File}, which can either be caused by an
     *                        {@link IOException} when opening the file or a semantic error in the GFF file itself
     */
    @SuppressWarnings({"squid:S135", // An object is only instantiated once.
            "squid:S3776", "PMD.CyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity",
            "PMD.StdCyclomaticComplexity"}) // Splitting this method up further decreases readability
    public FeatureAnnotation parse(final String gffFile) throws ParseException {
        @MonotonicNonNull FeatureAnnotation featureAnnotation = null;
        final List<String> fileMetaData = new ArrayList<>();

        BufferedReader bufferedReader = null;
        int lineNumber = 1;
        try {
            bufferedReader = Files.newBufferedReader(Paths.get(gffFile), StandardCharsets.UTF_8);
            String line = bufferedReader.readLine();
            if (line == null || !line.equals(GFF_VERSION_HEADER)) {
                throw new ParseException("GFF file does not have the appropriate header: " + GFF_VERSION_HEADER
                        + ", it was: '" + line + "'.");
            }

            while ((line = bufferedReader.readLine()) != null) {
                lineNumber++;
                if (line.isEmpty() || line.charAt(0) == '#' && line.charAt(1) != '#') {
                    continue;
                } else if (line.startsWith("##")) {
                    fileMetaData.add(line.substring(2, line.length() - 1));
                    continue;
                }

                final String[] columns = parseLine(line, lineNumber);

                final String seqId = columns[SEQ_ID_COLUMN];
                if (featureAnnotation == null) {
                    featureAnnotation = createFeatureAnnotation(seqId, lineNumber);
                }

                featureAnnotation.addSubFeatureAnnotation(makeSubFeatureAnnotation(columns, lineNumber));
            }
        } catch (final IOException e) {
            throw new ParseException("An IO error occurred while reading the GFF file.", e);
        } catch (final IllegalArgumentException e) {
            throw new ParseException("An error occurred while reading line " + lineNumber + " of the GFF file.", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    LOGGER.error("Unable to close the reader of the GFF file.", e);
                }
            }
        }

        if (featureAnnotation == null) {
            throw new ParseException("An error occurred while reading the GFF file: There was no seqid.");
        }
        for (final String metaData : fileMetaData) {
            featureAnnotation.addMetaData(metaData);
        }

        return featureAnnotation;
    }

    /**
     * Creates a {@link FeatureAnnotation} representing the current file.
     *
     * @param seqId      the seqId of the {@link FeatureAnnotation}
     * @param lineNumber the line number
     * @return a {@link FeatureAnnotation} representing the current file
     * @throws ParseException if an error occurred whilst making the {@link FeatureAnnotation}
     */
    private FeatureAnnotation createFeatureAnnotation(final String seqId, final int lineNumber) throws ParseException {
        try {
            return new FeatureAnnotation(seqId);
        } catch (final IllegalArgumentException e) {
            throw new ParseException("An error occurred while reading line " + lineNumber + ": Unable to create a "
                    + FeatureAnnotation.class.getSimpleName() + ".", e);
        }
    }

    /**
     * Creates a {@link SubFeatureAnnotation} annotation representing the current line.
     *
     * @param columns    the columns of the line to convert to a feature annotation
     * @param lineNumber the line number in the file
     * @return a {@link SubFeatureAnnotation} representing the current row in the file
     * @throws ParseException if the attribute section of the row is invalid
     */
    private SubFeatureAnnotation makeSubFeatureAnnotation(final String[] columns, final int lineNumber)
            throws ParseException {
        final SubFeatureAnnotation subFeatureAnnotation;

        try {
            subFeatureAnnotation = new SubFeatureAnnotation(
                    columns[SOURCE_COLUMN],
                    columns[TYPE_COLUMN],
                    columns[START_COLUMN],
                    columns[END_COLUMN],
                    columns[SCORE_COLUMN],
                    columns[STRAND_COLUMN],
                    columns[PHASE_COLUMN]
            );
        } catch (final IllegalArgumentException e) {
            throw new ParseException("An error occurred while parsing line " + lineNumber + ": Unable to create a "
                    + SubFeatureAnnotation.class.getSimpleName() + ".", e);
        }

        final String[] attributes = columns[ATTRIBUTES_COLUMN].split(";");
        for (final String attribute : attributes) {
            final String[] keyValuePair = attribute.split("=");
            if (keyValuePair.length != 2) {
                throw new ParseException("The attributes at line " + lineNumber + " contained an key without a value: '"
                        + attribute + "'.");
            }

            subFeatureAnnotation.setAttribute(keyValuePair[0], keyValuePair[1]);
        }

        return subFeatureAnnotation;
    }

    /**
     * Parses a single line of a GFF file.
     *
     * @param line       the string containing the line contents of the file
     * @param lineNumber the line number in the file
     * @return a {@link String} array representing the columns of a line
     * @throws ParseException if the line does not consist of 9 tab delimited columns
     */
    private String[] parseLine(final String line, final int lineNumber) throws ParseException {
        final String[] columns = line.split("\\s+");
        if (columns.length != GFF_COLUMNS) {
            throw new ParseException("Line " + lineNumber + " did not contain " + GFF_COLUMNS
                    + " columns, it contained " + columns.length + ".");
        }

        return columns;
    }
}
