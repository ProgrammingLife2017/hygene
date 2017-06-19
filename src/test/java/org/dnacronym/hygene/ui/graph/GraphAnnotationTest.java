package org.dnacronym.hygene.ui.graph;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.graph.annotation.AnnotationCollection;
import org.dnacronym.hygene.parser.GffFile;
import org.dnacronym.hygene.ui.genomeindex.GenomeNavigation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link GraphAnnotation}.
 */
class GraphAnnotationTest {
    private GenomeNavigation genomeNavigation;
    private ObjectProperty<GenomeIndex> genomeIndexProperty;
    private GraphStore graphStore;
    private AnnotationCollection annotationCollection;
    private ObjectProperty<GffFile> gffFileProperty;
    private GraphAnnotation graphAnnotation;


    @BeforeEach
    void beforeEach() {
        genomeNavigation = mock(GenomeNavigation.class);
        final GenomeIndex genomeIndex = mock(GenomeIndex.class);
        genomeIndexProperty = new SimpleObjectProperty<>(genomeIndex);
        when(genomeNavigation.getGenomeIndexProperty()).thenReturn(genomeIndexProperty);

        final GffFile gffFile = mock(GffFile.class);
        gffFileProperty = new SimpleObjectProperty<>(gffFile);
        annotationCollection = mock(AnnotationCollection.class);
        when(gffFile.getAnnotationCollection()).thenReturn(annotationCollection);
        graphStore = mock(GraphStore.class);
        when(graphStore.getGffFileProperty()).thenReturn(gffFileProperty);

        graphAnnotation = new GraphAnnotation(genomeNavigation, graphStore);
    }


    /**
     * Tests that a range start greater than the range end causes an {@link IllegalArgumentException}.
     */
    @Test
    void testRangeStartAfterEnd() {
        assertThrows(IllegalArgumentException.class, () -> graphAnnotation.getAnnotationsInRange(95, 9));
    }

    /**
     * Tests that a negative range start causes an {@link IllegalArgumentException}.
     */
    @Test
    void testNegativeRangeStart() {
        assertThrows(IllegalArgumentException.class, () -> graphAnnotation.getAnnotationsInRange(-2, 33));
    }

    /**
     * Tests that an empty range is returned if the annotation collection is empty.
     */
    @Test
    void testEmptyAnnotationCollection() {
        assertThat(graphAnnotation.getAnnotationsInRange(7, 58)).isEmpty();
    }

    /**
     * Tests that an empty range is returned if the annotation collection is null.
     */
    @Test
    void testNullAnnotationCollection() {
        annotationCollection = null;
        graphAnnotation = new GraphAnnotation(genomeNavigation, graphStore);

        assertThat(gffFileProperty.get().getAnnotationCollection()).isNull();
        assertThat(graphAnnotation.getAnnotationsInRange(11, 24)).isEmpty();
    }

    @Test
    void testSingleAnnotationMiss() {
        //
    }

    @Test
    void testSingleAnnotationHit() {
        //
    }
}
