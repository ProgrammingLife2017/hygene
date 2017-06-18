package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.graph.annotation.FeatureAnnotation;
import org.dnacronym.hygene.graph.annotation.SubFeatureAnnotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests of {@link GffFile}.
 */
final class GffFileTest {
    private static final String DEFAULT_GFF_FILE = "src/test/resources/gff/simple.gff";
    private FeatureAnnotation featureAnnotation;
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
        final Throwable throwable = catchThrowable(() -> gffFile.getFeatureAnnotation());

        assertThat(throwable).isInstanceOf(IllegalStateException.class);
    }

    /**
     * These tests test that the created {@link FeatureAnnotation} corresponds with the canonical Gene encoded in the
     * file, as prescribed by the <a href="https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md">
     * GFF v3 specification</a>.
     */

    @Test
    void testGeneChildren() throws ParseException {
        featureAnnotation = gffFile.parse(ProgressUpdater.DUMMY);
        final SubFeatureAnnotation geneAnnotation = featureAnnotation.getSubFeatureAnnotations().get(0);

        assertThat(geneAnnotation.getAttributes().get("ID")[0]).isEqualTo("gene00001");

        final List<SubFeatureAnnotation> geneAnnotationChildren = geneAnnotation.getChildren();
        assertThat(geneAnnotationChildren.get(0).getAttributes().get("ID")[0]).isEqualTo("tfbs00001");
        assertThat(geneAnnotationChildren.get(1).getAttributes().get("ID")[0]).isEqualTo("mRNA00001");
        assertThat(geneAnnotationChildren.get(2).getAttributes().get("ID")[0]).isEqualTo("mRNA00002");
        assertThat(geneAnnotationChildren.get(3).getAttributes().get("ID")[0]).isEqualTo("mRNA00003");
    }

    @Test
    void testTranscript() throws ParseException {
        featureAnnotation = gffFile.parse(ProgressUpdater.DUMMY);

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
}
