package org.dnacronym.hygene.parser;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.models.FeatureAnnotation;
import org.dnacronym.hygene.models.SubFeatureAnnotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Parses GFF files.
 * <p>
 * These files become {@link FeatureAnnotation}s.
 *
 * @see <a href="https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md">GFF v3 specification</a>
 * @see FeatureAnnotation
 */
@SuppressWarnings("PMD.TooManyMethods") // No reasonable refactor possible
public final class GffParser {
    private static final int PROGRESS_UPDATE_INTERVAL = 1000;
    private static final String PARSE_EXCEPTION_FORMAT = "There was an error at line %d: %s";
    /**
     * Progress is always the same, as we don't know how many lines the file has in advance.
     */
    private static final int PROGRESS = 50;
    private static final int PROGRESS_TOTAL = 100;
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
     * @param gffFile         the path of the GFF file to parse
     * @param progressUpdater the {@link ProgressUpdater} to update whilst parsing
     * @return a {@link FeatureAnnotation} representing the GFF file
     * @throws ParseException if unable to parse the {@link java.io.File}, which can either be caused by an
     *                        {@link IOException} when opening the file or a semantic error in the GFF file itself
     */
    @SuppressWarnings("squid:S135") // An object is only created once in the loop.
    public FeatureAnnotation parse(final String gffFile, final ProgressUpdater progressUpdater) throws ParseException {
        @MonotonicNonNull FeatureAnnotation featureAnnotation = null;
        final List<String> fileMetadata = new LinkedList<>();

        int lineNumber = 1;
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(gffFile), StandardCharsets.UTF_8)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lineNumber++;
                if (lineNumber % PROGRESS_UPDATE_INTERVAL == 0) {
                    progressUpdater.updateProgress(PROGRESS, "Reading line " + lineNumber + " of " + gffFile);
                }
                if (!lineIsFeature(line, fileMetadata)) {
                    continue;
                }

                final String[] columns = parseLine(line, lineNumber);
                if (featureAnnotation == null) {
                    checkSequenceId(columns[SEQ_ID_COLUMN], lineNumber);
                    featureAnnotation = createFeatureAnnotation(columns[SEQ_ID_COLUMN]);
                }

                final SubFeatureAnnotation subFeatureAnnotation = parseSubFeatureAnnotation(columns, lineNumber);
                checkParent(featureAnnotation, subFeatureAnnotation, lineNumber);
                featureAnnotation.addSubFeatureAnnotation(subFeatureAnnotation);
            }

            if (featureAnnotation == null) {
                throw new ParseException("An error occurred while reading the GFF file: There was no seqid.");
            }
        } catch (final IOException e) {
            throw new ParseException("An IO error occurred while reading the GFF file.", e);
        }

        featureAnnotation.addMetadata(fileMetadata);
        progressUpdater.updateProgress(PROGRESS_TOTAL, "Finished reading the file.");

        return featureAnnotation;
    }

    /**
     * Checks whether the given line is a feature.
     * <p>
     * If it is an empty line or a comment, the line is not a feature.<br>
     * If it start with "##", it is not a feature, but it is metadata and is therefore added to fileMetadata.
     *
     * @param line         the line to check
     * @param fileMetadata the file meta data to append to if line is meta data
     * @return true if line is an actual feature, false otherwise
     */
    private static boolean lineIsFeature(final String line, final List<String> fileMetadata) {
        if (line.isEmpty() || line.charAt(0) == '#' && line.charAt(1) != '#') {
            return false;
        } else if (line.startsWith("##")) {
            fileMetadata.add(line.substring(2, line.length() - 1));
            return false;
        }

        return true;
    }

    /**
     * Creates a {@link FeatureAnnotation} representing the current file.
     *
     * @param seqId the seqId of the {@link FeatureAnnotation}
     * @return a {@link FeatureAnnotation} representing the current file
     */
    private FeatureAnnotation createFeatureAnnotation(final String seqId) {
        return new FeatureAnnotation(seqId);
    }

    /**
     * Creates a {@link SubFeatureAnnotation} annotation representing the current line.
     *
     * @param columns    the columns of the line to convert to a feature annotation
     * @param lineNumber the current line number
     * @return a {@link SubFeatureAnnotation} representing the current row in the file
     * @throws ParseException if unable to create a {@link SubFeatureAnnotation}
     */
    private SubFeatureAnnotation parseSubFeatureAnnotation(final String[] columns, final int lineNumber)
            throws ParseException {
        final SubFeatureAnnotation subFeatureAnnotation;

        try {
            final int start = Integer.parseInt(columns[START_COLUMN]);
            final int end = Integer.parseInt(columns[END_COLUMN]);
            final int phase = ".".equals(columns[PHASE_COLUMN]) ? -1 : Integer.parseInt(columns[PHASE_COLUMN]);

            checkStartEndValid(start, end, lineNumber);
            checkPhaseValid(phase, lineNumber);
            checkStrandValid(columns[STRAND_COLUMN], lineNumber);

            subFeatureAnnotation = new SubFeatureAnnotation(
                    columns[SOURCE_COLUMN],
                    columns[TYPE_COLUMN],
                    start,
                    end,
                    ".".equals(columns[SCORE_COLUMN]) ? -1 : Double.parseDouble(columns[SCORE_COLUMN]),
                    columns[STRAND_COLUMN],
                    phase);
        } catch (final NumberFormatException e) {
            throw new ParseException(
                    String.format(PARSE_EXCEPTION_FORMAT, lineNumber, "A number could not be parsed."), e);
        }

        final String[] attributes = columns[ATTRIBUTES_COLUMN].split(";");
        for (final String attribute : attributes) {
            final String[] keyValuePair = attribute.split("=");
            if (keyValuePair.length != 2) {
                throw new ParseException(String.format(PARSE_EXCEPTION_FORMAT, lineNumber,
                        "Unable to parse the '" + attribute + "'."));
            }
            final String key = keyValuePair[0];

            subFeatureAnnotation.setAttribute(
                    key,
                    getValues(key, keyValuePair[1], subFeatureAnnotation.getAttributes(), lineNumber));
        }

        return subFeatureAnnotation;
    }

    /**
     * Checks that the sequence id is valid.
     *
     * @param seqId      the sequence id to check
     * @param lineNumber the current line number
     * @throws ParseException if the sequence starts with '>'
     */
    private static void checkSequenceId(final String seqId, final int lineNumber) throws ParseException {
        if (seqId.charAt(0) == '>') {
            throw new ParseException(String.format(PARSE_EXCEPTION_FORMAT, lineNumber,
                    "Seqid '" + seqId + "' started with the unescaped character '>'."));
        }
    }

    /**
     * Checks that the start and end values are valid.
     *
     * @param start      the start value to check
     * @param end        the end value to check
     * @param lineNumber the current line number
     * @throws ParseException if the end is before the start
     */
    private static void checkStartEndValid(final int start, final int end, final int lineNumber) throws ParseException {
        if (end < start) {
            throw new ParseException(String.format(PARSE_EXCEPTION_FORMAT, lineNumber,
                    "Start (" + start + ") was not before end (" + end + ")."));
        }
    }

    /**
     * Checks that the phase is valid.
     *
     * @param phase      the phase to check
     * @param lineNumber the current line number
     * @throws ParseException if the phase is not -1, and it is not in the range {@code [0, 2]}
     */
    private static void checkPhaseValid(final int phase, final int lineNumber) throws ParseException {
        if (phase != -1 && phase < 0 || phase > 2) {
            throw new ParseException(String.format(PARSE_EXCEPTION_FORMAT, lineNumber,
                    "Phase was not 0, 1, or 2, it was: '" + phase + "'."));
        }
    }

    /**
     * Checks that the strand is valid.
     *
     * @param strand     the strand to check
     * @param lineNumber the current line number
     * @throws ParseException if the strand is not ".", "-" or "+"
     */
    private static void checkStrandValid(final String strand, final int lineNumber) throws ParseException {
        if (!"+".equals(strand) && !"-".equals(strand) && !".".equals(strand)) {
            throw new ParseException(String.format(PARSE_EXCEPTION_FORMAT, lineNumber,
                    "Strand was not '+', '-' or '.', it was: '" + strand + "'."));
        }
    }

    /**
     * Checks that if the {@link SubFeatureAnnotation} refers to a parent, the {@link FeatureAnnotation} contains a
     * {@link SubFeatureAnnotation} with that sequence id.
     *
     * @param featureAnnotation    the {@link FeatureAnnotation} to get the {@link SubFeatureAnnotation}s from
     * @param subFeatureAnnotation the {@link SubFeatureAnnotation} with the possible parent reference
     * @param lineNumber           the current line number
     * @throws ParseException if the {@link SubFeatureAnnotation} has an "ID" that refers to a non-existent
     *                        {@link SubFeatureAnnotation} in the {@link FeatureAnnotation}
     */
    private static void checkParent(final FeatureAnnotation featureAnnotation,
                                    final SubFeatureAnnotation subFeatureAnnotation, final int lineNumber)
            throws ParseException {
        final String[] parents = subFeatureAnnotation.getAttributes().get("Parent");
        if (parents == null) {
            return;
        }

        final Map<String, List<SubFeatureAnnotation>> subFeatureAnnotations =
                featureAnnotation.getSubFeatureAnnotationsMap();

        for (final String parent : parents) {
            if (!subFeatureAnnotations.containsKey(parent)) {
                throw new ParseException(String.format(PARSE_EXCEPTION_FORMAT, lineNumber,
                        "Reference made to non-existent parent '" + parent + "'."));
            }
        }
    }

    /**
     * Returns the list of values of a certain key value pair.
     *
     * @param key        the key of the key value pair
     * @param values     the value of the value pair
     * @param attributes the map of attributes of the current {@link SubFeatureAnnotation}
     * @param lineNumber the current line number
     * @return the {@link String} array of values of this key
     * @throws ParseException if the key is "ID" and there is more than one value, or if the given attributes map
     *                        already contains the passed key
     */
    private static String[] getValues(final String key, final String values, final Map<String, String[]> attributes,
                                      final int lineNumber) throws ParseException {
        if (attributes.containsKey(key)) {
            throw new ParseException(String.format(PARSE_EXCEPTION_FORMAT, lineNumber,
                    "Tried to set a key twice: '" + key + "'."));
        }

        final String[] v = values.split(",");
        if ("ID".equals(key) && v.length > 1) {
            throw new ParseException(String.format(PARSE_EXCEPTION_FORMAT, lineNumber,
                    "The ID tag had more than one id, it had: " + v.length + "."));
        }

        return v;
    }

    /**
     * Parses a single line of a GFF file.
     *
     * @param line       the string containing the line contents of the file
     * @param lineNumber the current line number
     * @return a {@link String} array representing the columns of a line
     * @throws ParseException when unable to split the line up into 9 columns
     */
    private String[] parseLine(final String line, final int lineNumber) throws ParseException {
        final String[] columns = line.split("\\s+");
        if (columns.length != GFF_COLUMNS) {
            throw new ParseException(String.format(PARSE_EXCEPTION_FORMAT, lineNumber,
                    "Line did not contain " + GFF_COLUMNS + " columns, it contained " + columns.length + " columns."));
        }

        return columns;
    }
}
