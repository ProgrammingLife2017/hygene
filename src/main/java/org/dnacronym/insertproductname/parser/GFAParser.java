package org.dnacronym.insertproductname.parser;

import java.text.ParseException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


/**
 * Parses GFA to an {@code Assembly}.
 *
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class GFAParser {
    /**
     * Parses a GFA-compliant {@code String} to an {@code Assembly}.
     *
     * @param gfa a GFA-compliant {@code String}
     * @return an {@code Assembly}
     * @throws ParseException if the given {@code String} is not GFA-compliant
     */
    public Assembly parse(final String gfa) throws ParseException {
        final Assembly assembly = new Assembly();
        final String[] lines = gfa.split("\\R");

        for (int offset = 0; offset < lines.length; offset++) {
            parse(assembly, lines[offset], offset);
        }

        return assembly;
    }


    /**
     * Parses a line of a GFA-compliant {@code String} and adds it to the {@code Assembly}.
     *
     * @param assembly the {@code Assembly} to which this line should be added
     * @param line     a line of a GFA-compliant {@code String}
     * @param offset   the current line number
     * @throws ParseException if the given {@code String}s are not GFA-compliant
     */
    private void parse(final Assembly assembly, final String line, final int offset) throws ParseException {
        final StringTokenizer st = new StringTokenizer(line, "\t");
        if (!st.hasMoreTokens()) {
            return;
        }

        final String recordType = st.nextToken();
        switch (recordType) {
            case "H":
                break;

            case "S":
                assembly.addSegment(parseSegment(st, offset));
                break;

            case "L":
                assembly.addLink(parseLink(assembly, st, offset));
                break;

            default:
                throw new ParseException("Unknown record type `" + recordType + "`", offset);
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
        if (st == null) {
            throw new IllegalArgumentException("Segment StringTokenizer cannot be null.");
        }

        final String name;
        final String sequence;

        try {
            name = st.nextToken();
            sequence = st.nextToken();
        } catch (final NoSuchElementException e) {
            throw new ParseException("Not enough parameters for segment", offset);
        }

        return new Segment(name, sequence);
    }

    /**
     * Parses a line to a {@code Link}.
     *
     * @param assembly the {@code Assembly} in which the {@code Segment}s are linked
     * @param st       the {@code StringTokenizer} in which each token is a GFA field
     * @param offset   the current line number, used for debugging
     * @return a {@code Link}
     * @throws ParseException if the {@code Link} is not GFA-compliant
     */
    private Link parseLink(final Assembly assembly, final StringTokenizer st, final int offset) throws ParseException {
        if (assembly == null) {
            throw new IllegalArgumentException("Assembly cannot be null.");
        }
        if (st == null) {
            throw new IllegalArgumentException("Segment StringTokenizer cannot be null.");
        }

        final String from, to;
        final boolean fromOrient, toOrient;
        final int overlap;

        try {
            from = st.nextToken();
            fromOrient = "+".equals(st.nextToken());
            to = st.nextToken();
            toOrient = "-".equals(st.nextToken());
            overlap = parseCigarString(st.nextToken(), offset);
        } catch (final NoSuchElementException e) {
            throw new ParseException("Not enough parameters for link", offset);
        }

        return new Link(from, fromOrient, to, toOrient, overlap);
    }

    /**
     * Parses a CIGAR-compliant {@code String}.
     *
     * @param cigar  a CIGAR-compliant {@code String}
     * @param offset the current line number, used for debugging
     * @return the overlap in indicated by the CIGAR string.
     * @see <a href="http://genome.sph.umich.edu/wiki/SAM#What_is_a_CIGAR.3F">What is a CIGAR?</a>
     */
    private int parseCigarString(final String cigar, final int offset) throws ParseException {
        if (cigar == null || cigar.length() == 0) {
            return 0;
        }

        final int overlap;
        try {
            overlap = Integer.parseInt(cigar.replaceAll("M", ""));
        } catch (final NumberFormatException e) {
            throw new ParseException("Link cigar string could not be parsed", offset);
        }
        return overlap;
    }
}
