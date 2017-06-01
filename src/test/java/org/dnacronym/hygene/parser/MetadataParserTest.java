package org.dnacronym.hygene.parser;

import com.google.common.collect.ImmutableMap;
import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.NodeMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


final class MetadataParserTest {
    private MetadataParser parser;


    @BeforeEach
    void beforeEach() {
        parser = new MetadataParser();
    }


    @Test
    void testParseNodeMetadata() throws ParseException {
        final NodeMetadata nodeMetadata = parser.parseNodeMetadata(createGfaFile("%n%nS 12 TCAAGG"), 3);

        assertThat(nodeMetadata.getName()).isEqualTo("12");
        assertThat(nodeMetadata.getSequence()).isEqualTo("TCAAGG");
    }

    @Test
    void testParseNodeMetadataOfMultipleNodes() throws ParseException {
        final Map<Integer, NodeMetadata> nodesMetadata = parser.parseNodeMetadata(
                createGfaFile("%n%nS 12 TCAAGG%n%n%nS 12 TAG%nS 12 CAT%nS 12 SANITYCHECK"),
                ImmutableMap.of(1, 3, 2, 6, 3, 7)
        );

        assertThat(nodesMetadata.get(1).getSequence()).isEqualTo("TCAAGG");
        assertThat(nodesMetadata.get(2).getSequence()).isEqualTo("TAG");
        assertThat(nodesMetadata.get(3).getSequence()).isEqualTo("CAT");
    }

    /**
     * Test that the reader is reset correctly between multiple reads.
     */
    @Test
    void testParseNodeMetadataReaderReset() throws ParseException {
        final NodeMetadata nodeMetadata = parser.parseNodeMetadata(createGfaFile("%n%nS 12 TCAAGG"), 3);
        final NodeMetadata nodeMetadata2 = parser.parseNodeMetadata(createGfaFile("%n%nS 12 TCAAGG"), 3);

        assertThat(nodeMetadata.getName()).isEqualTo("12");
        assertThat(nodeMetadata2.getName()).isEqualTo("12");
    }

    @Test
    void testParseNodeMetaOfMultipleNodesInWrongOrder() {
        final Throwable e = catchThrowable(() -> parser.parseNodeMetadata(
                createGfaFile("%n%nS 12 TCAAGG%n%n%nS 12 TAG%nS 12 CAT%nS 12 SANITYCHECK"),
                ImmutableMap.of(2, 7, 1, 3)
        ));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Line 3 cannot be lower than the previous line number.");
    }

    @Test
    void testParseEdgeMetadata() throws ParseException {
        final EdgeMetadata edgeMetadata = parser.parseEdgeMetadata(createGfaFile("%nL 12 + 24 - 4M"), 2);

        assertThat(edgeMetadata.getFromOrient()).isEqualTo("+");
        assertThat(edgeMetadata.getToOrient()).isEqualTo("-");
        assertThat(edgeMetadata.getOverlap()).isEqualTo("4M");
    }

    @Test
    void testParseMetadataInvalidLineNumber() throws ParseException {
        final Throwable e = catchThrowable(() -> parser.parseNodeMetadata(createGfaFile(""), 0));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Line 0 is not a valid line number");
    }

    @Test
    void testParseMetadataInvalidLineNumber2() throws ParseException {
        final Throwable e = catchThrowable(() -> parser.parseNodeMetadata(createGfaFile(""), 5));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Line 5 is not found");
    }

    @Test
    void testParseNodeMetadataWithInvalidLineBecauseTheSequenceIsMissing() throws ParseException {
        final Throwable e = catchThrowable(() -> parser.parseNodeMetadata(createGfaFile("S 12"), 1));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Not enough parameters for segment on line 1");
    }

    @Test
    void testParseEdgeMetadataWithInvalidLineBecauseTheOrientIsMissing() throws ParseException {
        final Throwable e = catchThrowable(() -> parser.parseEdgeMetadata(createGfaFile("L 12 + 24"), 1));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Not enough parameters for link on line 1");
    }

    @Test
    void testParseNodeMetadataWithAnEdgeLine() throws ParseException {
        final Throwable e = catchThrowable(() -> parser.parseNodeMetadata(createGfaFile("L 12 + 24 - 4M"), 1));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Expected line 1 to start with S");
    }

    @Test
    void testParseEdgeMetadataWithANodeLine() throws ParseException {
        final Throwable e = catchThrowable(() -> parser.parseEdgeMetadata(createGfaFile("S 12 ACTG"), 1));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Expected line 1 to start with L");
    }

    private GfaFile createGfaFile(final String gfa) throws ParseException {
        final byte[] gfaBytes = replaceSpacesWithTabs(gfa).getBytes(StandardCharsets.UTF_8);
        final GfaFile gfaFile = mock(GfaFile.class);
        when(gfaFile.readFile()).thenAnswer(invocationOnMock ->
                new BufferedReader(new InputStreamReader(new ByteArrayInputStream(gfaBytes)))
        );
        return gfaFile;
    }

    private String replaceSpacesWithTabs(final String string) {
        return String.format(string.replaceAll(" ", "\t"));
    }
}
