package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.NodeMetadata;

import java.util.StringTokenizer;


/**
 * @see <a href="https://github.com/GFA-spec/GFA-spec/">GFA v1 specification</a>
 */
public final class MetadataParser {
    public NodeMetadata parseNodeMetadata(final String gfa, final int lineNumber) throws ParseException {
        String line = getLine(gfa, lineNumber);

        validateLine(line, "S");

        final StringTokenizer st = initializeStringTokenizer(line);

        st.nextToken();
        final String originalNodeId = st.nextToken();
        final String sequence = st.nextToken();

        return new NodeMetadata(originalNodeId, sequence);
    }

    public EdgeMetadata parseEdgeMetadata(final String gfa, final int lineNumber) throws ParseException {
        String line = getLine(gfa, lineNumber);

        validateLine(line, "L");

        final StringTokenizer st = initializeStringTokenizer(line);

        st.nextToken();
        final String fromOrient = st.nextToken();
        st.nextToken();
        final String toOrient = st.nextToken();
        st.nextToken();
        final String overlap = st.nextToken();

        return new EdgeMetadata(fromOrient, toOrient, overlap);
    }

    private String getLine(final String gfa, final int lineNumber) {
        return gfa.split("\\R")[lineNumber-1];
    }

    private void validateLine(String line, String expectedPrefix) throws ParseException {
        if (!line.startsWith(expectedPrefix)) {
            throw new ParseException("Expected line to start with " + expectedPrefix + " but line was " + line);
        }
    }

    private StringTokenizer initializeStringTokenizer(String line) throws ParseException {
        final StringTokenizer st = new StringTokenizer(line, "\t");
        if (!st.hasMoreTokens()) {
            throw new ParseException("Invalid line (line: " + line + ")");
        }
        return st;
    }
}
