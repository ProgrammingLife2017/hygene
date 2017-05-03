package org.dnacronym.insertproductname.parser;

import org.dnacronym.insertproductname.parser.factories.AssemblyParserFactory;
import org.dnacronym.insertproductname.parser.factories.GFAParserFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


class GFAFileTest {

    @Test
    void testGFAFileObjectCanBeConstructed() {
        final GFAFile gfaFile = new GFAFile("name_of_the_file.gfa", "contents_of_the_file");

        assertThat(gfaFile.getFileName()).isEqualTo("name_of_the_file.gfa");
        assertThat(gfaFile.getContents()).isEqualTo("contents_of_the_file");
    }

    @Test
    void testReadFile() throws IOException {
        GFAFile gfaFile = GFAFile.read("src/test/resources/gfa/simple.gfa");

        assertThat(gfaFile.getContents()).isEqualTo(String.format(
                "H\tVN:Z:1.0%n"
                + "S\t11\tACCTT%n"
                + "S\t12\tTCAAGG%n"
                + "L\t11\t+\t12\t-\t4M%n"
        ));
    }

    @Test
    void testCannotReadNonExistingFile() {
        final Throwable e = catchThrowable(() -> GFAFile.read("random-file-name"));

        assertThat(e).isInstanceOf(IOException.class);
    }

    @Test
    void testParseFile() throws IOException, ParseException {
        GFAParser gfaParser = spy(GFAParser.class);
        GFAParserFactory.setInstance(gfaParser);
        AssemblyParser assemblyParser = spy(AssemblyParser.class);
        AssemblyParserFactory.setInstance(assemblyParser);

        new GFAFile("filename", "S\t12\tTCAAGG").parse();

        verify(gfaParser).parse("S\t12\tTCAAGG");
        verify(assemblyParser).parse(any(Assembly.class));
    }
}
