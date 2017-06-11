package org.dnacronym.hygene.parser;


import org.dnacronym.hygene.models.FeatureAnnotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Unit tests of {@link GffParser}.
 */
final class GffParserTest {
    private static final GffFile DEFAULT_GFF_FILE = new GffFile("src/test/resources/gff/simple.gff");

    private GffParser gffParser;
    private FeatureAnnotation featureAnnotation;


    @BeforeEach
    void beforeEach() throws ParseException {
        gffParser = new GffParser();
    }


    /**
     * Happy paths.
     */

    @Test
    void testSeqId() throws ParseException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE);
        assertThat(featureAnnotation.getSeqId()).isEqualTo("ctg123");
    }

    @Test
    void testMetaDataSize() throws ParseException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE);
        assertThat(featureAnnotation.getMetaData()).hasSize(1);
    }

    @Test
    void testFeaturesSize() throws ParseException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE);
        assertThat(featureAnnotation.getFeatureAnnotations()).hasSize(23);
    }

    /**
     * Parse exceptions.
     */

    @Test
    void testNoHeader() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse(new GffFile("src/test/resources/gff/no_header.gff")));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingColumn() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse(new GffFile("src/test/resources/gff/missing_column.gff")));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testAttributeFormatWrong() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse(new GffFile("src/test/resources/gff/wrong_attribute_format.gff")));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testUnknownAttribute() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse(new GffFile("src/test/resources/gff/unknown_attribute.gff")));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testUnescapedGreaterThan() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse(new GffFile("src/test/resources/gff/id_unescaped_greater_than.gff")));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidPhase() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse(new GffFile("src/test/resources/gff/too_large_phase.gff")));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testTooLargeScore() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse(new GffFile("src/test/resources/gff/too_large_phase.gff")));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidScore() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse(new GffFile("src/test/resources/gff/invalid_score.gff")));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidStrand() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse(new GffFile("src/test/resources/gff/invalid_strand.gff")));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testTooLargePhase() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse(new GffFile("src/test/resources/gff/too_large_phase.gff")));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }
}
