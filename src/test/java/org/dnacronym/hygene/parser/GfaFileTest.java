package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.NodeMetadata;
import org.dnacronym.hygene.parser.factories.MetadataParserFactory;
import org.dnacronym.hygene.parser.factories.NewGfaParserFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


/**
 * Tests the behavior of the {@link GfaFile} class.
 */
class GfaFileTest {
    private static final String GFA_TEST_FILE = "src/test/resources/gfa/simple.gfa";
    private static final String SIMPLE_GFA_CONTENTS = String.format("H\tVN:Z:1.0%n"
            + "S\t11\tACCTT%n"
            + "S\t12\tTCAAGG%n"
            + "L\t11\t+\t12\t-\t4M%n");


    @AfterAll
    static void resetFactories() {
        NewGfaParserFactory.setInstance(null);
        MetadataParserFactory.setInstance(null);
    }


    @Test
    void testGfaFileObjectCanBeConstructed() {
        final GfaFile gfaFile = new GfaFile("name_of_the_file.gfa");

        assertThat(gfaFile.getFileName()).isEqualTo("name_of_the_file.gfa");
    }

    @Test
    void testCannotReadNonExistingFile() {
        final Throwable e = catchThrowable(() -> new GfaFile("random-file-name").parse());

        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testGetGraphWithoutParsing() {
        final Throwable e = catchThrowable(() -> new GfaFile("random-file-name").getGraph());

        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testReadFile() throws ParseException {
        GfaFile gfaFile = new GfaFile(GFA_TEST_FILE);

        assertThat(bufferedReaderToString(gfaFile.readFile())).isEqualTo(SIMPLE_GFA_CONTENTS);
    }

    @Test
    void testParseFile() throws IOException, ParseException {
        NewGfaParser gfaParser = spy(NewGfaParser.class);
        NewGfaParserFactory.setInstance(gfaParser);

        GfaFile gfaFile = new GfaFile(GFA_TEST_FILE);
        gfaFile.parse();

        verify(gfaParser).parse(gfaFile);
        assertThat(gfaFile.getGraph()).isNotNull();
    }

    @Test
    void testParseNodeMetaData() throws IOException, ParseException {
        MetadataParser metadataParser = spy(MetadataParser.class);
        MetadataParserFactory.setInstance(metadataParser);

        GfaFile gfaFile = new GfaFile(GFA_TEST_FILE);
        NodeMetadata nodeMetadata = gfaFile.parseNodeMetadata(2);

        verify(metadataParser).parseNodeMetadata(gfaFile, 2);
        assertThat(nodeMetadata.getSequence()).isEqualTo("ACCTT");
    }

    @Test
    void testParseEdgeMetadata() throws IOException, ParseException {
        MetadataParser metadataParser = spy(MetadataParser.class);
        MetadataParserFactory.setInstance(metadataParser);

        GfaFile gfaFile = new GfaFile(GFA_TEST_FILE);
        EdgeMetadata edgeMetadata = gfaFile.parseEdgeMetadata(4);

        verify(metadataParser).parseEdgeMetadata(gfaFile, 4);
        assertThat(edgeMetadata.getToOrient()).isEqualTo("-");
    }

    private String bufferedReaderToString(final BufferedReader reader) {
        final String newline = String.format("%n");
        return reader.lines().collect(Collectors.joining(newline)) + newline;
    }
}
