package org.dnacronym.hygene.parser;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;


/**
 * Parses GFA to a {@code SequenceAlignmentGraph}.
 *
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class GfaParser {
    /**
     * Parses a GFA-compliant {@code String} to a {@code SequenceAlignmentGraph}.
     *
     * @param gfa a GFA-compliant {@code String}
     * @return a {@code SequenceAlignmentGraph}
     * @throws ParseException if the given {@code String} is not GFA-compliant
     */
    public SequenceAlignmentGraph parse(final String gfa) throws ParseException {
        final SequenceAlignmentGraph graph = new SequenceAlignmentGraph();
        final String[] lines = gfa.split("\\R");

        for (int offset = 0; offset < lines.length; offset++) {
            parseLine(graph, lines[offset], offset);
        }

        return graph;
    }


    /**
     * Parses a line of a GFA-compliant {@code String} and adds it to the {@code SequenceAlignmentGraph}.
     *
     * @param graph  the {@code SequenceAlignmentGraph} to which this line should be added
     * @param line   a line of a GFA-compliant {@code String}
     * @param offset the current line number
     * @throws ParseException if the given {@code String}s are not GFA-compliant
     */
    private void parseLine(final SequenceAlignmentGraph graph, final String line, final int offset)
            throws ParseException {
        final StringTokenizer st = new StringTokenizer(line, "\t");
        if (!st.hasMoreTokens()) {
            return;
        }

        final String recordType = st.nextToken();
        switch (recordType) {
            case "H":
            case "C":
            case "P":
                break;

            case "S":
                graph.addSegment(parseSegment(st, offset));
                break;

            case "L":
                graph.addLink(parseLink(st, offset));
                break;

            default:
                throw new ParseException("Unknown record type `" + recordType + "` on line " + offset);
        }
    }

    /**
     * Parses a line to a {@code Segment}.
     *
     * @param st     the {@code StringTokenizer} in which each token is a GFA field
     * @param offset the current line number, used for debugging
     * @return a {@code Segment}
     * @throws ParseException if the {@code Segment} is not GFA-compliant
     */
    private Segment parseSegment(final StringTokenizer st, final int offset) throws ParseException {
        try {
            final String name = st.nextToken();
            final String sequence = st.nextToken();

            return new Segment(name, sequence);
        } catch (final NoSuchElementException e) {
            throw new ParseException("Not enough parameters for segment on line" + offset, e);
        }
    }

    /**
     * Parses a line to a {@code Link}.
     *
     * @param st     the {@code StringTokenizer} in which each token is a GFA field
     * @param offset the current line number, used for debugging
     * @return a {@code Link}
     * @throws ParseException if the {@code Link} is not GFA-compliant
     */
    private Link parseLink(final StringTokenizer st, final int offset) throws ParseException {
        try {
            final String from = st.nextToken();
            st.nextToken();
            final String to = st.nextToken();
            st.nextToken();
            final int overlap = parseCigarString(st.nextToken(), offset);

            return new Link(from, to, overlap);
        } catch (final NoSuchElementException e) {
            throw new ParseException("Not enough parameters for link on line " + offset, e);
        }
    }

    /**
     * Parses a CIGAR-compliant {@code String}.
     *
     * @param cigar  a CIGAR-compliant {@code String}
     * @param offset the current line number, used for debugging
     * @return the overlap in indicated by the CIGAR string.
     * @throws ParseException if the CIGAR string is invalid
     * @see <a href="http://genome.sph.umich.edu/wiki/SAM#What_is_a_CIGAR.3F">What is a CIGAR?</a>
     */
    private int parseCigarString(final String cigar, final int offset) throws ParseException {
        if (cigar.length() == 0) {
            return 0;
        }

        try {
            return Integer.parseInt(cigar.replaceAll("M", ""));
        } catch (final NumberFormatException e) {
            throw new ParseException("Link cigar string could not be parsed on line " + offset, e);
        }
    }
}
