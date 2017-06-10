package org.dnacronym.hygene.parser;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.models.FeatureAnnotation;
import org.dnacronym.hygene.models.GeneAnnotation;

import java.io.BufferedReader;
import java.io.IOException;


/**
 * Parses GFF files.
 * <p>
 * These files become {@link GeneAnnotation}s.
 *
 * @see <a href="https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md">GFF v3 specification</a>
 * @see GeneAnnotation
 */
public final class GffParser {
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
     * Parse the file.
     *
     * @param gffFile the file
     * @return list of things
     * @throws ParseException if unable to parse the {@link GffFile}
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // An object is only instantiated once
    public GeneAnnotation parse(final GffFile gffFile) throws ParseException {
        final BufferedReader bufferedReader = gffFile.readFile();

        @MonotonicNonNull GeneAnnotation geneAnnotation = null;
        try {
            String line = bufferedReader.readLine();
            if (line == null || !line.equals(GFF_VERSION_HEADER)) {
                throw new ParseException("GFF file does not have the appropriate header: " + GFF_VERSION_HEADER
                        + ", it was: '" + line + "'.");
            }

            int lineNumber = 0;
            while ((line = bufferedReader.readLine()) != null) {
                lineNumber++;

                if (line.isEmpty()) {
                    continue;
                }

                final String[] columns = parseLine(line, lineNumber);
                final String seqId = columns[SEQ_ID_COLUMN];

                if (geneAnnotation == null) {
                    geneAnnotation = new GeneAnnotation(seqId);
                } else if (!geneAnnotation.getSeqId().equals(seqId)) {
                    throw new ParseException("GFF file contains more than one seqid: '" + geneAnnotation.getSeqId()
                            + "' and '" + seqId + "'.");
                }

                geneAnnotation.addFeatureAnnotation(makeFeatureAnnotation(columns, lineNumber));
            }
        } catch (final IOException e) {
            throw new ParseException("An error occurred while reading the GFF file.", e);
        }

        if (geneAnnotation == null) {
            throw new ParseException("An error occurred while reading the GFF file: There was no seqid.");
        }

        return geneAnnotation;
    }

    /**
     * Returns a feature annotation representing the current line.
     *
     * @param columns    the columns of the line to convert to a feature annotation
     * @param lineNumber the line number in the file
     * @return a {@link FeatureAnnotation} representing the current row in the file
     * @throws ParseException if the attribute section of the row is invalid
     */
    private FeatureAnnotation makeFeatureAnnotation(final String[] columns, final int lineNumber)
            throws ParseException {
        final FeatureAnnotation featureAnnotation = new FeatureAnnotation(
                columns[SOURCE_COLUMN],
                columns[TYPE_COLUMN],
                columns[START_COLUMN],
                columns[END_COLUMN],
                columns[SCORE_COLUMN],
                columns[STRAND_COLUMN],
                columns[PHASE_COLUMN]
        );

        final String[] attributes = columns[ATTRIBUTES_COLUMN].split(";");
        for (final String attribute : attributes) {
            final String[] keyValuePair = attribute.split("=");
            if (keyValuePair.length != 2) {
                throw new ParseException("The attributes at line " + lineNumber
                        + " contained an invalid key value pair:" + attribute + ".");
            }

            featureAnnotation.setAttribute(keyValuePair[0], keyValuePair[1]);
        }

        return featureAnnotation;
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
        final String[] columns = line.split("\\t");
        if (columns.length != GFF_COLUMNS) {
            throw new ParseException("Line " + lineNumber + " did not contain " + GFF_COLUMNS
                    + " columns, it contained " + columns.length + ".");
        }

        return columns;
    }
}
