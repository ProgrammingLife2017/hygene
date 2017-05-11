package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.NodeMetadata;
import org.dnacronym.hygene.parser.factories.MetadataParserFactory;
import org.dnacronym.hygene.parser.factories.NewGfaParserFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


class NewGfaFileTest {
    private static final String GFA_TEST_FILE = "src/test/resources/gfa/simple.gfa";
    private static final String SIMPLE_GFA_CONTENTS = String.format("H\tVN:Z:1.0%n"
            + "S\t11\tACCTT%n"
            + "S\t12\tTCAAGG%n"
            + "L\t11\t+\t12\t-\t4M%n");

    @Test
    void testGfaFileObjectCanBeConstructed() {
        final NewGfaFile gfaFile = new NewGfaFile("name_of_the_file.gfa");

        assertThat(gfaFile.getFileName()).isEqualTo("name_of_the_file.gfa");
    }

    @Test
    void testCannotReadNonExistingFile() {
        final Throwable e = catchThrowable(() -> new NewGfaFile("random-file-name").parse());

        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testGetGraphWithoutParsing() {
        final Throwable e = catchThrowable(() -> new NewGfaFile("random-file-name").getGraph());

        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testReadFile() throws ParseException {
        NewGfaFile gfaFile = new NewGfaFile(GFA_TEST_FILE);

        assertThat(gfaFile.readFile()).isEqualTo(SIMPLE_GFA_CONTENTS);
    }

    @Test
    void testParseFile() throws IOException, ParseException {
        NewGfaParser gfaParser = spy(NewGfaParser.class);
        NewGfaParserFactory.setInstance(gfaParser);

        NewGfaFile gfaFile = new NewGfaFile(GFA_TEST_FILE);
        gfaFile.parse();

        verify(gfaParser).parse(any(NewGfaFile.class));
        assertThat(gfaFile.getGraph()).isNotNull();
    }

    @Test
    void testParseNodeMetaData() throws IOException, ParseException {
        MetadataParser metadataParser = spy(MetadataParser.class);
        MetadataParserFactory.setInstance(metadataParser);

        NewGfaFile gfaFile = new NewGfaFile(GFA_TEST_FILE);
        NodeMetadata nodeMetadata = gfaFile.parseNodeMetadata(2);

        verify(metadataParser).parseNodeMetadata(SIMPLE_GFA_CONTENTS, 2);
        assertThat(nodeMetadata.getSequence()).isEqualTo("ACCTT");
    }

    @Test
    void testParseEdgeMetadata() throws IOException, ParseException {
        MetadataParser metadataParser = spy(MetadataParser.class);
        MetadataParserFactory.setInstance(metadataParser);

        NewGfaFile gfaFile = new NewGfaFile(GFA_TEST_FILE);
        EdgeMetadata edgeMetadata = gfaFile.parseEdgeMetadata(4);

        verify(metadataParser).parseEdgeMetadata(SIMPLE_GFA_CONTENTS, 4);
        assertThat(edgeMetadata.getToOrient()).isEqualTo("-");
    }
}
