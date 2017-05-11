package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.NodeMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

final class MetadataParserTest {
    private MetadataParser parser;


    @BeforeEach
    void beforeEach() {
        parser = new MetadataParser();
    }


    @Test
    void testParseNodeMetadata() throws ParseException {
        NodeMetadata nodeMetadata = parser.parseNodeMetadata(replaceSpacesWithTabs("%n%nS 12 TCAAGG"), 3);

        assertThat(nodeMetadata.getOriginalNodeId()).isEqualTo("12");
        assertThat(nodeMetadata.getSequence()).isEqualTo("TCAAGG");
    }

    @Test
    void testParseEdgeMetadata() throws ParseException {
        EdgeMetadata edgeMetadata = parser.parseEdgeMetadata(replaceSpacesWithTabs("%nL 12 + 24 - 4M"), 2);

        assertThat(edgeMetadata.getFromOrient()).isEqualTo("+");
        assertThat(edgeMetadata.getToOrient()).isEqualTo("-");
        assertThat(edgeMetadata.getOverlap()).isEqualTo("4M");
    }

    @Test
    void testParseMetadataInvalidLineNumber() throws ParseException {
        Throwable e = catchThrowable(() -> parser.parseNodeMetadata("", 0));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Line 0 is not found");
    }

    @Test
    void testParseMetadataInvalidLineNumber2() throws ParseException {
        Throwable e = catchThrowable(() -> parser.parseNodeMetadata("", 5));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Line 5 is not found");
    }

    @Test
    void testParseNodeMetadataWithInvalidLineBecauseTheSequenceIsMissing() throws ParseException {
        Throwable e = catchThrowable(() -> parser.parseNodeMetadata(replaceSpacesWithTabs("S 12"), 1));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Not enough parameters for segment on line 1");
    }

    @Test
    void testParseEdgeMetadataWithInvalidLineBecauseTheOrientIsMissing() throws ParseException {
        Throwable e = catchThrowable(() -> parser.parseEdgeMetadata(replaceSpacesWithTabs("L 12 + 24"), 1));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Not enough parameters for link on line 1");
    }

    @Test
    void testParseNodeMetadataWithAnEdgeLine() throws ParseException {
        Throwable e = catchThrowable(() -> parser.parseNodeMetadata(replaceSpacesWithTabs("L 12 + 24 - 4M"), 1));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Expected line 1 to start with S");
    }

    @Test
    void testParseEdgeMetadataWithANodeLine() throws ParseException {
        Throwable e = catchThrowable(() -> parser.parseEdgeMetadata(replaceSpacesWithTabs("S 12 ACTG"), 1));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Expected line 1 to start with L");
    }


    private String replaceSpacesWithTabs(final String string) {
        return String.format(string.replaceAll(" ", "\t"));
    }
}
