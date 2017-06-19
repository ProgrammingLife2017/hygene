package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.graph.annotation.AnnotationCollection;
import org.dnacronym.hygene.graph.annotation.Annotation;
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
    private AnnotationCollection annotationCollection;


    @BeforeEach
    void beforeEach() throws GffParseException {
        gffParser = new GffParser();
    }


    /**
     * Default behaviour.
     */

    @Test
    void testSeqId() throws GffParseException {
        annotationCollection = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        assertThat(annotationCollection.getSequenceId()).isEqualTo("ctg123");
    }

    @Test
    void testMetadataSize() throws GffParseException {
        annotationCollection = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        assertThat(annotationCollection.getMetadata()).hasSize(2);
    }

    @Test
    void testFeaturesSize() throws GffParseException {
        annotationCollection = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        assertThat(annotationCollection.getAnnotations()).hasSize(23);
    }

    @Test
    void testFirstSubFeatureCorrect() throws GffParseException {
        annotationCollection = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        final Annotation annotation = annotationCollection.getAnnotations().get(0);

        assertThat(annotation.getSource()).isEqualTo("source");
        assertThat(annotation.getType()).isEqualTo("gene");
        assertThat(annotation.getStart()).isEqualTo(1000);
        assertThat(annotation.getEnd()).isEqualTo(9000);
        assertThat(annotation.getScore()).isEqualTo(2.2);
        assertThat(annotation.getStrand()).isEqualTo("+");
        assertThat(annotation.getPhase()).isEqualTo(1);
        assertThat(annotation.getAttributes().get("ID")[0]).isEqualTo("gene00001");
        assertThat(annotation.getAttributes().get("Name")[0]).isEqualTo("EDEN");
    }

    @Test
    void testSecondSubFeatureCorrect() throws GffParseException {
        annotationCollection = gffParser.parse(DEFAULT_GFF_FILE, ProgressUpdater.DUMMY);
        final Annotation annotation = annotationCollection.getAnnotations().get(1);

        assertThat(annotation.getSource()).isEqualTo(".");
        assertThat(annotation.getType()).isEqualTo("TF_binding_site");
        assertThat(annotation.getStart()).isEqualTo(1000);
        assertThat(annotation.getEnd()).isEqualTo(1012);
        assertThat(annotation.getScore()).isEqualTo(-1);
        assertThat(annotation.getStrand()).isEqualTo("+");
        assertThat(annotation.getPhase()).isEqualTo(-1);
        assertThat(annotation.getAttributes().get("ID")[0]).isEqualTo("tfbs00001");
    }

    /**
     * {@link GffParseException}s.
     */

    @Test
    void testOpenNonExistentFile() {
        final Throwable throwable = catchThrowable(() -> gffParser.parse("asdf.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }

    @Test
    void testMissingColumn() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/missing_column.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }

    @Test
    void testAttributeFormatWrong() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/wrong_attribute_format.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }

    @Test
    void testUnescapedGreaterThan() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/id_unescaped_greater_than.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }

    @Test
    void testInvalidPhase() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_phase.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }

    @Test
    void testInvalidScore() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_score.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }

    @Test
    void testInvalidStrand() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_strand.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }

    @Test
    void testTooLargePhase() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/too_large_phase.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }

    @Test
    void testInvalidStart() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_start.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }

    @Test
    void testInvalidEnd() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_end.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }

    @Test
    void testNonExistentParent() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/non_existent_parent.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }

    @Test
    void testMultipleIDValue() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/multiple_id_value.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }

    @Test
    void testMultipleIDKey() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/multiple_id_key.gff", ProgressUpdater.DUMMY));

        assertThat(throwable).isInstanceOf(GffParseException.class);
    }
}
