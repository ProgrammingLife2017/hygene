package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.Annotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * GFF parser.
 *
 * @see <a href="https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md">GFF v3 specification</a>
 */
public final class GffParser {
    private static final String GFF_VERSION_HEADER = "##gff-version 3.2.1";
    private static final int GFF_COLUMNS = 9;


    /**
     * Parse the file.
     *
     * @param gffFile the file
     * @return list of things
     * @throws ParseException if unable to parse the {@link GffFile}
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // ugh
    public List<Annotation> parse(final GffFile gffFile) throws ParseException {
        final BufferedReader bufferedReader = gffFile.readFile();

        final Map<String, Annotation> annotations = new HashMap<>();
        try {
            String line = bufferedReader.readLine();
            if (line == null || !line.equals(GFF_VERSION_HEADER)) {
                throw new ParseException("GFF file does not have the appropriate header: " + GFF_VERSION_HEADER
                        + ", it was: '" + line + "'.");
            }

            for (int i = 0; (line = bufferedReader.readLine()) != null; i++) {
                final String[] columns = parseLine(line, i);
                final String seqId = columns[0];

                if (!annotations.containsKey(seqId)) {
                    annotations.put(seqId, new Annotation(seqId));
                }

                final Annotation annotation = annotations.get(seqId);

                final String[] attributes = columns[8].split(";");
                for (final String attribute : attributes) {
                    final String[] attributePair = attribute.split("=");
                    if (attributePair.length != 2) {
                        throw new ParseException("Attributes of line " + i + " contained an error.");
                    }

                    annotation.setAttribute(attributePair[0], attributePair[1]);
                }
            }
        } catch (final IOException e) {
            throw new ParseException("An error while reading the GFF file.", e);
        }

        return new ArrayList<>(annotations.values());
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
