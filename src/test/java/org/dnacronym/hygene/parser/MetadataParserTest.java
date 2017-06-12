package org.dnacronym.hygene.parser;

import com.google.common.collect.ImmutableMap;
import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.NodeMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


final class MetadataParserTest {
    private MetadataParser parser;
    private RandomAccessFile randomAccessFile;


    @BeforeEach
    void beforeEach() {
        parser = new MetadataParser();
    }


    @Test
    void testParseNodeMetadata() throws ParseException, IOException {
        final GfaFile gfaFile = createGfaFile("%n%nS 12 TCAAGG * ORI:Z:test.fasta;");
        when(randomAccessFile.readLine()).thenReturn(replaceSpacesWithTabs("S 12 TCAAGG * ORI:Z:test.fasta;"));

        final NodeMetadata nodeMetadata = parser.parseNodeMetadata(gfaFile, 0);

        assertThat(nodeMetadata.getName()).isEqualTo("12");
        assertThat(nodeMetadata.getSequence()).isEqualTo("TCAAGG");
        assertThat(nodeMetadata.getGenomes()).contains("test.fasta");
    }

    @Test
    void testParseNodeMetadataOfMultipleNodes() throws ParseException, IOException {
        final GfaFile gfaFile = createGfaFile("%n%nS 12 TCAAGG * ORI:Z:test.fasta;"
                + "%n%n%nS 12 TAG * ORI:Z:test.fasta;"
                + "%nS 12 CAT * ORI:Z:test.fasta;"
                + "%nS 12 SANITYCHECK * ORI:Z:test.fasta;");
        when(randomAccessFile.readLine()).thenReturn(
                replaceSpacesWithTabs("S 12 TCAAGG * ORI:Z:test.fasta;"),
                replaceSpacesWithTabs("S 12 TAG * ORI:Z:test.fasta;"),
                replaceSpacesWithTabs("S 12 CAT * ORI:Z:test.fasta;")
        );

        final Map<Integer, NodeMetadata> nodesMetadata = parser.parseNodeMetadata(
                gfaFile,
                ImmutableMap.of(1, 3L, 2, 6L, 3, 7L)
        );

        assertThat(nodesMetadata.get(1).getSequence()).isEqualTo("TCAAGG");
        assertThat(nodesMetadata.get(2).getSequence()).isEqualTo("TAG");
        assertThat(nodesMetadata.get(3).getSequence()).isEqualTo("CAT");
    }

    @Test
    void testParseEdgeMetadata() throws ParseException {
        final EdgeMetadata edgeMetadata = parser.parseEdgeMetadata(createGfaFile("%nL 12 + 24 - 4M"), 2);

        assertThat(edgeMetadata.getFromOrient()).isEqualTo("+");
        assertThat(edgeMetadata.getToOrient()).isEqualTo("-");
        assertThat(edgeMetadata.getOverlap()).isEqualTo("4M");
    }

    @Test
    void testParseNodeMetadataWithInvalidLineBecauseTheSequenceIsMissing() throws ParseException, IOException {
        final GfaFile gfaFile = createGfaFile("S 12");
        when(randomAccessFile.readLine()).thenReturn(replaceSpacesWithTabs("S 12"));

        final Throwable e = catchThrowable(() -> parser.parseNodeMetadata(gfaFile, 1));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Not enough parameters for segment at position 1");
    }

    @Test
    void testParseNodeMetadataWithInvalidLineBecauseTheGenomeIsMissing() throws ParseException {
        Throwable e = catchThrowable(() -> parser.parseNodeMetadata(createGfaFile("S 12 AC *"), 1));

        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testParseNodeMetadataWithInvalidLineBecauseTheGenomePrefixIsIncorrect() throws ParseException {
        Throwable e = catchThrowable(() -> parser.parseNodeMetadata(createGfaFile("S 12 AC * ORY:Z:test.fasta;"), 1));

        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testParseEdgeMetadataWithInvalidLineBecauseTheOrientIsMissing() throws ParseException {
        final Throwable e = catchThrowable(() -> parser.parseEdgeMetadata(createGfaFile("L 12 + 24"), 1));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Not enough parameters for link on line 1");
    }

    @Test
    void testParseNodeMetadataWithAnEdgeLine() throws ParseException, IOException {
        final GfaFile gfaFile = createGfaFile("L 12 + 24 - 4M");
        when(randomAccessFile.readLine()).thenReturn(replaceSpacesWithTabs("L 12 + 24 - 4M"));

        final Throwable e = catchThrowable(() -> parser.parseNodeMetadata(gfaFile, 1));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Expected line at position 1 to start with S");
    }

    @Test
    void testParseEdgeMetadataWithANodeLine() throws ParseException, IOException {
        final GfaFile gfaFile = createGfaFile("S 12 ACTG");
        when(randomAccessFile.readLine()).thenReturn(replaceSpacesWithTabs("S 12 ACTG"));

        final Throwable e = catchThrowable(() -> parser.parseEdgeMetadata(gfaFile, 1));

        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("Expected line at position 1 to start with L");
    }


    private GfaFile createGfaFile(final String gfa) throws ParseException {
        final byte[] gfaBytes = replaceSpacesWithTabs(gfa).getBytes(StandardCharsets.UTF_8);
        final GfaFile gfaFile = mock(GfaFile.class);
        when(gfaFile.readFile()).thenAnswer(invocationOnMock ->
                new BufferedReader(new InputStreamReader(new ByteArrayInputStream(gfaBytes)))
        );
        randomAccessFile = mock(RandomAccessFile.class);
        when(gfaFile.getRandomAccessFile()).thenReturn(randomAccessFile);
        return gfaFile;
    }

    private String replaceSpacesWithTabs(final String string) {
        return String.format(string.replaceAll(" ", "\t"));
    }
}
