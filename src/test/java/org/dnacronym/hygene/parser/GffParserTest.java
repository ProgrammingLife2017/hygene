package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.FeatureAnnotation;
import org.dnacronym.hygene.models.SubFeatureAnnotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testSeqId() throws ParseException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        assertThat(featureAnnotation.getSequenceId()).isEqualTo("ctg123");
    }

    @Test
    void testMetaDataSize() throws ParseException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        assertThat(featureAnnotation.getMetadata()).hasSize(2);
    }

    @Test
    void testFeaturesSize() throws ParseException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        assertThat(featureAnnotation.getSubFeatureAnnotations()).hasSize(23);
    }

    @Test
    void testFirstSubFeatureCorrect() throws ParseException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        final SubFeatureAnnotation subFeatureAnnotation = featureAnnotation.getSubFeatureAnnotations().get(0);

        assertThat(subFeatureAnnotation.getSource()).isEqualTo("source");
        assertThat(subFeatureAnnotation.getType()).isEqualTo("gene");
        assertThat(subFeatureAnnotation.getStart()).isEqualTo(1000);
        assertThat(subFeatureAnnotation.getEnd()).isEqualTo(9000);
        assertThat(subFeatureAnnotation.getScore()).isEqualTo(2.2);
        assertThat(subFeatureAnnotation.getStrand()).isEqualTo("+");
        assertThat(subFeatureAnnotation.getPhase()).isEqualTo(1);
        assertThat(subFeatureAnnotation.getAttributes().get("ID")[0]).isEqualTo("gene00001");
        assertThat(subFeatureAnnotation.getAttributes().get("Name")[0]).isEqualTo("EDEN");
    }

    @Test
    void testSecondSubFeatureCorrect() throws ParseException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        final SubFeatureAnnotation subFeatureAnnotation = featureAnnotation.getSubFeatureAnnotations().get(1);

        assertThat(subFeatureAnnotation.getSource()).isEqualTo(".");
        assertThat(subFeatureAnnotation.getType()).isEqualTo("TF_binding_site");
        assertThat(subFeatureAnnotation.getStart()).isEqualTo(1000);
        assertThat(subFeatureAnnotation.getEnd()).isEqualTo(1012);
        assertThat(subFeatureAnnotation.getScore()).isEqualTo(-1);
        assertThat(subFeatureAnnotation.getStrand()).isEqualTo("+");
        assertThat(subFeatureAnnotation.getPhase()).isEqualTo(-1);
        assertThat(subFeatureAnnotation.getAttributes().get("ID")[0]).isEqualTo("tfbs00001");
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
    void testUnescapedGreaterThan() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/id_unescaped_greater_than.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidPhase() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_phase.gff", ProgressUpdater.DUMMY));

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
    void testMultipleIDValue() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/multiple_id_value.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testMultipleIDKey() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/multiple_id_key.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }
}
