package org.dnacronym.hygene.coordinatesystem;

import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.ParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * Created by gandr on 5/23/2017.
 */
public class CoordinateIndexParser {
    private static final String GENOME_HEADER_SEQUENCE = "ORI:Z:";

    private GfaFile gfaFile;
    private Map<String, Integer> genomeBaseCounts;


    public CoordinateIndexParser(GfaFile gfaFile) {
        this.gfaFile = gfaFile;
        this.genomeBaseCounts = new HashMap<>();
    }

    void parse() {

    }

    private void parseLine(final String line) throws ParseException {
        final StringTokenizer st = new StringTokenizer(line, "\t");
        if (!st.hasMoreTokens()) {
            return;
        }

        final String recordType = st.nextToken();
        switch (recordType) {
            case "L":
            case "C":
            case "P":
                break;

            case "H":
                parseHeader(st);
                break;

            case "S":
                break;
        }
    }

    private void parseHeader(final StringTokenizer stringTokenizer) throws ParseException {
        // Ignore headers not containing relevant genome information
        if (!stringTokenizer.hasMoreTokens()) {
            return;
        }

        final String headerBody = stringTokenizer.nextToken();
        if (!headerBody.startsWith(GENOME_HEADER_SEQUENCE)) {
            return;
        }

        final String genomeListString = headerBody.substring(GENOME_HEADER_SEQUENCE.length());
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

    private void parseSegment(final StringTokenizer stringTokenizer) throws ParseException {
        final String nodeName = stringTokenizer.nextToken();

        stringTokenizer.nextToken(); // Ignore base sequence
        stringTokenizer.nextToken(); // Ignore asterisk

        final String genomeMetadata = stringTokenizer.nextToken();
        if (!genomeMetadata.startsWith(GENOME_HEADER_SEQUENCE)) {
            return;
        }

        final String genomeListString = genomeMetadata.substring(GENOME_HEADER_SEQUENCE.length());
        final StringTokenizer bodyTokenizer = new StringTokenizer(genomeListString, ";");

        while (bodyTokenizer.hasMoreTokens()) {
            final String nextGenome = bodyTokenizer.nextToken();

            if (nextGenome.equals("")) {
                continue;
            }

            if (!genomeBaseCounts.containsKey(nextGenome)) {
                throw new ParseException("Unkown genome '" + nextGenome + "' on segment " + nodeName);
            }

            final int previousDistance = genomeBaseCounts.get(nextGenome);
        }
    }
}
