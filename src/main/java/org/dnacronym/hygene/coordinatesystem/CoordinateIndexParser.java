package org.dnacronym.hygene.coordinatesystem;

import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.MetadataParser;
import org.dnacronym.hygene.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * Created by gandr on 5/23/2017.
 */
public class CoordinateIndexParser {

    private GfaFile gfaFile;
    private Map<String, Integer> genomeBaseCounts;


    public CoordinateIndexParser(GfaFile gfaFile) {
        this.gfaFile = gfaFile;
        this.genomeBaseCounts = new HashMap<>();
    }

    void parse() throws ParseException {
        // Get new buffered reader
        final BufferedReader gfa = gfaFile.readFile();

        try {
            String line;
            while ((line = gfa.readLine()) != null && line.startsWith("H")) {
                parseHeader(line);
            }
        } catch (final IOException e) {
            throw new ParseException("An error while reading the GFA file.", e);
        }
    }

    private void parseHeader(final String line) throws ParseException {
        final StringTokenizer stringTokenizer = new StringTokenizer(line, "\t");

        stringTokenizer.nextToken(); // Ignore "H" token
        // Ignore headers not containing relevant genome information
        final String headerBody = stringTokenizer.nextToken();
        if (!headerBody.startsWith(MetadataParser.GENOME_LIST_HEADER_PREFIX)) {
            return;
        }

        final String genomeListString = headerBody.substring(MetadataParser.GENOME_LIST_HEADER_PREFIX.length());
        final StringTokenizer bodyTokenizer = new StringTokenizer(genomeListString, ";");

        while (bodyTokenizer.hasMoreTokens()) {
            final String nextGenome = bodyTokenizer.nextToken();

            if (!nextGenome.equals("")) {
                genomeBaseCounts.put(nextGenome, 0);
            }
        }

        if (genomeBaseCounts.isEmpty()) {
            throw new ParseException("Expected at least one genome to be present in GFA file.");
        }
    }
}
