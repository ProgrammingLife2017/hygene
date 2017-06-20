package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.graph.annotation.AnnotationCollection;
import org.dnacronym.hygene.graph.annotation.Annotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests of {@link GffFile}.
 */
final class GffFileTest {
    private static final String DEFAULT_GFF_FILE = "src/test/resources/gff/simple.gff";
    private AnnotationCollection annotationCollection;
    private GffFile gffFile;


    @BeforeEach
    void beforeEach() {
        gffFile = new GffFile(DEFAULT_GFF_FILE);
    }


    @Test
    void testGetName() {
        assertThat(gffFile.getFileName()).isEqualTo(DEFAULT_GFF_FILE);
    }

    @Test
    void testGetFeatureBeforeParsing() {
        final Throwable throwable = catchThrowable(() -> gffFile.getAnnotationCollection());

        assertThat(throwable).isInstanceOf(IllegalStateException.class);
    }

    /**
     * These tests test that the created {@link AnnotationCollection} corresponds with the canonical Gene encoded in the
     * file, as prescribed by the <a href="https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md">
     * GFF v3 specification</a>.
     */

    @Test
    void testTranscript() throws GffParseException {
        annotationCollection = gffFile.parse(ProgressUpdater.DUMMY);

        final Annotation transcriptedProtean1 = annotationCollection.getAnnotations().get(10);
        final Annotation transcriptedProtean2 = annotationCollection.getAnnotations().get(11);
        final Annotation transcriptedProtean3 = annotationCollection.getAnnotations().get(12);
        final Annotation transcriptedProtean4 = annotationCollection.getAnnotations().get(13);

        assertThat(transcriptedProtean1.getStart()).isEqualTo(1201);
        assertThat(transcriptedProtean1.getEnd()).isEqualTo(1500);
        assertThat(transcriptedProtean2.getStart()).isEqualTo(3000);
        assertThat(transcriptedProtean2.getEnd()).isEqualTo(3902);
        assertThat(transcriptedProtean3.getStart()).isEqualTo(5000);
        assertThat(transcriptedProtean3.getEnd()).isEqualTo(5500);
        assertThat(transcriptedProtean4.getStart()).isEqualTo(7000);
        assertThat(transcriptedProtean4.getEnd()).isEqualTo(7600);
    }
}
