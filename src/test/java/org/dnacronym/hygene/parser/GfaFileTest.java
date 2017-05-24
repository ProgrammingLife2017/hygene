package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.NodeMetadata;
import org.dnacronym.hygene.parser.factories.MetadataParserFactory;
import org.dnacronym.hygene.parser.factories.NewGfaParserFactory;
import org.dnacronym.hygene.persistence.FileDatabaseDriver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


/**
 * Tests the behavior of the {@link GfaFile} class.
 */
final class GfaFileTest {
    private static final String GFA_TEST_FILE = "src/test/resources/gfa/simple.gfa";
    private static final String SIMPLE_GFA_CONTENTS = String.format("H\tVN:Z:1.0%n"
            + "S\t11\tACCTT%n"
            + "S\t12\tTCAAGG%n"
            + "L\t11\t+\t12\t-\t4M%n");

    private String currentFileName;


    @AfterEach
    void afterEach() throws IOException {
        NewGfaParserFactory.setInstance(null);
        MetadataParserFactory.setInstance(null);
        if (currentFileName != null) {
            Files.deleteIfExists(Paths.get(currentFileName + FileDatabaseDriver.DB_FILE_EXTENSION));
        }
    }

    @AfterAll
    static void resetFactories() {
        NewGfaParserFactory.setInstance(null);
        MetadataParserFactory.setInstance(null);
    }


    @Test
    void testGfaFileObjectCanBeConstructed() {
        currentFileName = "name_of_the_file.gfa";
        final GfaFile gfaFile = new GfaFile(currentFileName);

        assertThat(gfaFile.getFileName()).isEqualTo(currentFileName);
    }

    @Test
    void testCannotReadNonExistingFile() {
        currentFileName = "random-file-name";
        final Throwable e = catchThrowable(() -> new GfaFile(currentFileName).parse(progress -> {
        }));

        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testGetGraphWithoutParsing() {
        currentFileName = "random-file-name";
        final Throwable e = catchThrowable(() -> new GfaFile(currentFileName).getGraph());

        assertThat(e).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testReadFile() throws ParseException {
        currentFileName = GFA_TEST_FILE;
        final GfaFile gfaFile = new GfaFile(GFA_TEST_FILE);

        assertThat(bufferedReaderToString(gfaFile.readFile())).isEqualTo(SIMPLE_GFA_CONTENTS);
    }

    @Test
    void testParseFile() throws IOException, ParseException {
        NewGfaParser gfaParser = spy(NewGfaParser.class);
        NewGfaParserFactory.setInstance(gfaParser);

        currentFileName = GFA_TEST_FILE;
        final GfaFile gfaFile = new GfaFile(GFA_TEST_FILE);
        gfaFile.parse(progress -> {
        });

        verify(gfaParser).parse(eq(gfaFile), any(ProgressUpdater.class));
        assertThat(gfaFile.getGraph()).isNotNull();
    }

    @Test
    void testParseNodeMetaData() throws IOException, ParseException {
        MetadataParser metadataParser = spy(MetadataParser.class);
        MetadataParserFactory.setInstance(metadataParser);

        currentFileName = GFA_TEST_FILE;
        GfaFile gfaFile = new GfaFile(GFA_TEST_FILE);
        NodeMetadata nodeMetadata = gfaFile.parseNodeMetadata(2);

        verify(metadataParser).parseNodeMetadata(gfaFile, 2);
        assertThat(nodeMetadata.getSequence()).isEqualTo("ACCTT");
    }

    @Test
    void testParseEdgeMetadata() throws IOException, ParseException {
        MetadataParser metadataParser = spy(MetadataParser.class);
        MetadataParserFactory.setInstance(metadataParser);

        currentFileName = GFA_TEST_FILE;
        final GfaFile gfaFile = new GfaFile(GFA_TEST_FILE);
        EdgeMetadata edgeMetadata = gfaFile.parseEdgeMetadata(4);

        verify(metadataParser).parseEdgeMetadata(gfaFile, 4);
        assertThat(edgeMetadata.getToOrient()).isEqualTo("-");
    }

    @Test
    void testGetNodeIds() throws ParseException {
        currentFileName = GFA_TEST_FILE;
        final GfaFile gfaFile = new GfaFile(GFA_TEST_FILE);

        gfaFile.parse(progress -> {
        });

        assertThat(gfaFile.getNodeIds()).contains(new AbstractMap.SimpleEntry<>("11", 1));
    }

    private String bufferedReaderToString(final BufferedReader reader) {
        final String newline = String.format("%n");
        return reader.lines().collect(Collectors.joining(newline)) + newline;
    }
}
