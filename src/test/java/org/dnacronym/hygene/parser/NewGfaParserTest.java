package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.NodeColor;
import org.dnacronym.hygene.models.Edge;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for {@code GfaParser}s.
 */
class NewGfaParserTest {
    private NewGfaParser parser;


    @BeforeEach
    void beforeEach() {
        parser = new NewGfaParser();
    }


    @Test
    void testParseEmpty() throws ParseException {
        final Graph graph = parse("");

        assertThat(graph.getNodeArrays()).isEmpty();
    }

    @Test
    void testUnknownRecordType() {
        final String gfa = "ç 4 + 2 + 1M";

        final Throwable e = catchThrowable(() -> parse(gfa));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testIgnoredRecordTypes() throws ParseException {
        final String gfa = "H header\nC containment\nP path";
        assertThat(parse(gfa)).isNotNull();
    }

    @Test
    void testSegmentWithMissingSequence() {
        final String gfa = "S name ";

        final Throwable e = catchThrowable(() -> parse(gfa));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testLinkWithMissingNode() {
        final String gfa = "L 1 +";

        final Throwable e = catchThrowable(() -> parse(gfa));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testSegmentProperties() throws ParseException {
        final String gfa = "S name1 contents\nS name2 contents\nS name3 contents";
        final Graph graph = parse(gfa);

        assertThat(graph.getLineNumber(2)).isEqualTo(4);
        assertThat(graph.getColor(2)).isEqualTo(NodeColor.BLACK);
    }

    @Test
    void testSizes() throws ParseException {
        final String gfa = "S 1 A\nS 2 B\nL 1 + 2 + 0M";
        final Graph graph = parse(gfa);

        assertThat(graph.getNodeArrays()).hasSize(2);
        assertThat(graph.getNode(0).getOutgoingEdges()).hasSize(1);
        assertThat(graph.getNode(1).getIncomingEdges()).hasSize(1);
    }

    @Test
    void testEdges() throws ParseException {
        final String gfa = "S 1 A\nS 2 B\nL 1 + 2 + 0M\nL 2 + 1 + 0M";
        final Graph graph = parse(gfa);

        Node firstNode = graph.getNode(0);

        assertThat(firstNode.getNumberOfOutgoingEdges()).isEqualTo(1);
        assertThat(firstNode.getOutgoingEdges()).contains(new Edge(0, 1, 4));

        assertThat(firstNode.getNumberOfIncomingEdges()).isEqualTo(1);
        assertThat(firstNode.getIncomingEdges()).contains(new Edge(1, 0, 5));
    }


    private String replaceSpacesWithTabs(final String string) {
        return string.replaceAll(" ", "\t");
    }

    private Graph parse(final String gfa) throws ParseException {
        return parser.parse(replaceSpacesWithTabs(gfa));
    }
}
