package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.parser.factories.SequenceAlignmentGraphParserFactory;
import org.dnacronym.hygene.parser.factories.GfaParserFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


class GfaFileTest {

    @Test
    void testGfaFileObjectCanBeConstructed() {
        final GfaFile gfaFile = new GfaFile("name_of_the_file.gfa", "contents_of_the_file");

        assertThat(gfaFile.getFileName()).isEqualTo("name_of_the_file.gfa");
        assertThat(gfaFile.getContents()).isEqualTo("contents_of_the_file");
    }

    @Test
    void testReadFile() throws IOException {
        GfaFile gfaFile = GfaFile.read("src/test/resources/gfa/simple.gfa");

        assertThat(gfaFile.getContents()).isEqualTo(String.format(
                "H\tVN:Z:1.0%n"
                + "S\t11\tACCTT%n"
                + "S\t12\tTCAAGG%n"
                + "L\t11\t+\t12\t-\t4M%n"
        ));
    }

    @Test
    void testCannotReadNonExistingFile() {
        final Throwable e = catchThrowable(() -> GfaFile.read("random-file-name"));

        assertThat(e).isInstanceOf(IOException.class);
    }

    @Test
    void testParseFile() throws IOException, ParseException {
        GfaParser gfaParser = spy(GfaParser.class);
        GfaParserFactory.setInstance(gfaParser);
        SequenceAlignmentGraphParser sequenceAlignmentGraphParser = spy(SequenceAlignmentGraphParser.class);
        SequenceAlignmentGraphParserFactory.setInstance(sequenceAlignmentGraphParser);

        new GfaFile("filename", "S\t12\tTCAAGG").parse();

        verify(gfaParser).parse("S\t12\tTCAAGG");
        verify(sequenceAlignmentGraphParser).parse(any(SequenceAlignmentGraph.class));
    }
}
