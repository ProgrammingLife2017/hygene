package org.dnacronym.hygene.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
    public List<String[]> parse(final GffFile gffFile) throws ParseException {
        final BufferedReader bufferedReader = gffFile.readFile();

        final List<String[]> things = new ArrayList<>();
        try {
            String line = bufferedReader.readLine();
            if (line == null || !line.equals(GFF_VERSION_HEADER)) {
                throw new ParseException("GFF file does not have the appropriate header : " + GFF_VERSION_HEADER);
            }

            for (int i = 0; (line = bufferedReader.readLine()) != null; i++) {
                things.add(parseLine(line, i));
            }
        } catch (final IOException e) {
            throw new ParseException("An error while reading the GFF file.", e);
        }

        return things;
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
