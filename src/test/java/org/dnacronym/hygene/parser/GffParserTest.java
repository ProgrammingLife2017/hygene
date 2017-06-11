package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.FeatureAnnotation;
import org.dnacronym.hygene.models.SubFeatureAnnotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

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
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE);
        assertThat(featureAnnotation.getSeqId()).isEqualTo("ctg123");
    }

    @Test
    void testMetaDataSize() throws ParseException, IOException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE);
        assertThat(featureAnnotation.getMetaData()).hasSize(1);
    }

    @Test
    void testFeaturesSize() throws ParseException, IOException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE);
        assertThat(featureAnnotation.getSubFeatureAnnotations()).hasSize(23);
    }

    /**
     * These tests test that the created {@link FeatureAnnotation} corresponds with the canonical Gene encoded in the
     * file, as prescribed by the <a href="https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md">
     * GFF v3 specification</a>.
     */

    @Test
    void testGeneChildren() throws IOException, ParseException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE);

        final SubFeatureAnnotation geneAnnotation = featureAnnotation.getSubFeatureAnnotations().get(0);

        assertThat(geneAnnotation.getAttributes().get("ID")[0]).isEqualTo("gene00001");

        final List<SubFeatureAnnotation> geneAnnotationChildren = geneAnnotation.getChildren();
        assertThat(geneAnnotationChildren.get(0).getAttributes().get("ID")[0]).isEqualTo("tfbs00001");
        assertThat(geneAnnotationChildren.get(1).getAttributes().get("ID")[0]).isEqualTo("mRNA00001");
        assertThat(geneAnnotationChildren.get(2).getAttributes().get("ID")[0]).isEqualTo("mRNA00002");
        assertThat(geneAnnotationChildren.get(3).getAttributes().get("ID")[0]).isEqualTo("mRNA00003");
    }

    @Test
    void testTranscripts() throws IOException, ParseException {
        featureAnnotation = gffParser.parse(DEFAULT_GFF_FILE);

        final SubFeatureAnnotation transcriptedProtean1 = featureAnnotation.getSubFeatureAnnotations().get(10);
        final SubFeatureAnnotation transcriptedProtean2 = featureAnnotation.getSubFeatureAnnotations().get(11);
        final SubFeatureAnnotation transcriptedProtean3 = featureAnnotation.getSubFeatureAnnotations().get(12);
        final SubFeatureAnnotation transcriptedProtean4 = featureAnnotation.getSubFeatureAnnotations().get(13);

        assertThat(transcriptedProtean1.getStart()).isEqualTo(1201);
        assertThat(transcriptedProtean1.getEnd()).isEqualTo(1500);
        assertThat(transcriptedProtean2.getStart()).isEqualTo(3000);
        assertThat(transcriptedProtean2.getEnd()).isEqualTo(3902);
        assertThat(transcriptedProtean3.getStart()).isEqualTo(5000);
        assertThat(transcriptedProtean3.getEnd()).isEqualTo(5500);
        assertThat(transcriptedProtean4.getStart()).isEqualTo(7000);
        assertThat(transcriptedProtean4.getEnd()).isEqualTo(7600);
    }

    /**
     * {@link ParseException}s.
     */

    @Test
    void testOpenNonExistentFile() {
        final Throwable throwable = catchThrowable(() -> gffParser.parse("asdf.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testNoHeader() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/no_header.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testMissingColumn() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/missing_column.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testAttributeFormatWrong() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/wrong_attribute_format.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testUnknownAttribute() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/unknown_attribute.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testUnescapedGreaterThan() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/id_unescaped_greater_than.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidPhase() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/too_large_phase.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testTooLargeScore() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/too_large_phase.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidScore() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_score.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidStrand() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_strand.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testTooLargePhase() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/too_large_phase.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testMultipleSeqId() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/multiple_seq_id.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidStart() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_start.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testInvalidEnd() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/invalid_end.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testNonExistentParent() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/non_existent_parent.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }

    @Test
    void testMultipleID() {
        final Throwable throwable = catchThrowable(() ->
                gffParser.parse("src/test/resources/gff/multiple_id.gff"));

        assertThat(throwable).isInstanceOf(ParseException.class);
    }
}
