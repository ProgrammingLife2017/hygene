package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.FeatureAnnotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests of {@link GffParser}.
 */
final class GffParserTest {
    private static final String DEFAULT_GFF_FILE = "src/test/resources/gff/simple.gff";

    private GffParser gffParser;
    private FeatureAnnotation featureAnnotation;


    @BeforeEach
    void beforeEach() throws ParseException {
        gffParser = new GffParser();
    }


    /**
     * Default behaviour.
     */

    @Test
    void testSeqId() throws ParseException, IOException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        assertThat(featureAnnotation.getSeqId()).isEqualTo("ctg123");
    }

    @Test
    void testMetaDataSize() throws ParseException, IOException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        assertThat(featureAnnotation.getMetaData()).hasSize(1);
    }

    @Test
    void testFeaturesSize() throws ParseException, IOException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        assertThat(featureAnnotation.getSubFeatureAnnotations()).hasSize(23);
    }

    /**
     * {@link ParseException}s.
     */

    @Test
    void testOpenNonExistentFile() {
        final Throwable throwable = catchThrowable(() -> gffParser.parse("asdf.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testNoHeader() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/no_header.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingColumn() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/missing_column.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testAttributeFormatWrong() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/wrong_attribute_format.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testUnknownAttribute() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/unknown_attribute.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testUnescapedGreaterThan() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/id_unescaped_greater_than.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidPhase() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/too_large_phase.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testTooLargeScore() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/too_large_phase.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidScore() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_score.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidStrand() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_strand.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testTooLargePhase() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/too_large_phase.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testMultipleSeqId() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/multiple_seq_id.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidStart() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_start.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidEnd() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_end.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testNonExistentParent() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/non_existent_parent.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testMultipleID() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/multiple_id.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }
}
