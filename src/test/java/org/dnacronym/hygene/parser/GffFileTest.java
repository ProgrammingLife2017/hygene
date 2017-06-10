package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.SequenceAnnotation;
import org.dnacronym.hygene.parser.factories.GffParserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;


/**
 * Unit tests of {@link GffFile}.
 */
final class GffFileTest {
    private static final String GFF_TEST_FILE = "src/test/resources/gff/simple.gff";

    private String currentFileName;


    @AfterEach
    void beforeEach() {
        GffParserFactory.setInstance(null);
    }


    @Test
    void testGffFileObjectCanBeConstructed() {
        currentFileName = "name_of_the_file.gff";
        final GffFile gfaFile = new GffFile(currentFileName);

        assertThat(gfaFile.getFileName()).isEqualTo(currentFileName);
    }

    @Test
    void testCannotReadNonExistingFile() {
        currentFileName = "random-file-name";
        final Throwable e = catchThrowable(() -> new GffFile(currentFileName).parse());

        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testReadFile() throws ParseException {
        final GffFile gfaFile = new GffFile(GFF_TEST_FILE);
        final SequenceAnnotation genomeIndex = gfaFile.parse();

        assertThat(genomeIndex.getSeqId()).isEqualTo("ctg123");
    }
}
