package org.dnacronym.hygene.ui.graph;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.graph.annotation.Annotation;
import org.dnacronym.hygene.graph.annotation.AnnotationCollection;
import org.dnacronym.hygene.parser.GffFile;
import org.dnacronym.hygene.ui.genomeindex.GenomeNavigation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link GraphAnnotation}.
 */
class GraphAnnotationTest {
    private GenomeNavigation genomeNavigation;
    private GenomeIndex genomeIndex;
    private ObjectProperty<GenomeIndex> genomeIndexProperty;

    private GraphStore graphStore;
    private AnnotationCollection annotationCollection;
    private GffFile gffFile;
    private ObjectProperty<GffFile> gffFileProperty;
    private GraphAnnotation graphAnnotation;


    @BeforeEach
    void beforeEach() throws SQLException {
        initGenomeNavigationMock();
        initGraphStoreMock();

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
        when(gffFile.getAnnotationCollection()).thenReturn(null);
        graphAnnotation = new GraphAnnotation(genomeNavigation, graphStore);

        assertThat(graphAnnotation.getAnnotationsInRange(11, 24)).isEmpty();
    }

    /**
     * Tests that an empty range is returned if the only annotation is not in the specified range.
     */
    @Test
    void testSingleAnnotationMiss() {
        final Annotation annotation = new Annotation("source", "type", 23, 85, 38.0, "strand", 90);
        annotationCollection.addAnnotation(annotation);
        graphAnnotation = new GraphAnnotation(genomeNavigation, graphStore);

        assertThat(graphAnnotation.getAnnotationsInRange(12, 21)).isEmpty();
    }

    /**
     * Tests that the only annotation is returned if the annotation is entirely within the specified range.
     */
    @Test
    void testSingleAnnotationHitWithin() {
        final Annotation annotation = new Annotation("source", "type", 63, 47, 9.0, "strand", 19);
        annotationCollection.addAnnotation(annotation);
        graphAnnotation = new GraphAnnotation(genomeNavigation, graphStore);

        assertThat(graphAnnotation.getAnnotationsInRange(3, 93)).containsExactly(annotation);
    }

    /**
     * Tests that the only annotation is returned if the range is entirely within the only annotation.
     */
    @Test
    void testSingleAnnotationHitAround() {
        final Annotation annotation = new Annotation("source", "type", 33, 85, 55.0, "strand", 13);
        annotationCollection.addAnnotation(annotation);
        graphAnnotation = new GraphAnnotation(genomeNavigation, graphStore);

        assertThat(graphAnnotation.getAnnotationsInRange(69, 83)).containsExactly(annotation);
    }

    /**
     * Tests that the only annotation is returned if the range partially overlaps with the annotation on the left side.
     */
    @Test
    void testSingleAnnotationHitLeftMargin() {
        final Annotation annotation = new Annotation("source", "type", 58, 76, 96.0, "strand", 47);
        annotationCollection.addAnnotation(annotation);
        graphAnnotation = new GraphAnnotation(genomeNavigation, graphStore);

        assertThat(graphAnnotation.getAnnotationsInRange(43, 67)).containsExactly(annotation);
    }

    /**
     * Tests that the only annotation is returned if the range partially overlaps with the annotation on the right side.
     */
    @Test
    void testSingleAnnotationHitRightMargin() {
        final Annotation annotation = new Annotation("source", "type", 54, 90, 72.0, "strand", 78);
        annotationCollection.addAnnotation(annotation);
        graphAnnotation = new GraphAnnotation(genomeNavigation, graphStore);

        assertThat(graphAnnotation.getAnnotationsInRange(57, 103)).containsExactly(annotation);
    }

    /**
     * Tests that all annotations in range are returned if there are multiple annotations.
     */
    @Test
    void testMultipleAnnotations() {
        final Annotation annotationA = new Annotation("sourceA", "typeA", 54, 90, 72.0, "strandA", 78);
        final Annotation annotationB = new Annotation("sourceB", "typeB", 14, 97, 20.0, "strandB", 91);
        final Annotation annotationC = new Annotation("sourceC", "typeC", 33, 49, 42.0, "strandC", 41);
        annotationCollection.addAnnotation(annotationA);
        annotationCollection.addAnnotation(annotationB);
        annotationCollection.addAnnotation(annotationC);
        graphAnnotation = new GraphAnnotation(genomeNavigation, graphStore);

        assertThat(graphAnnotation.getAnnotationsInRange(51, 93))
                .containsExactlyInAnyOrder(annotationA, annotationB);
    }

    /**
     * Tests that annotations are properly updated when the genome index updates.
     */
    @Test
    void testUpdateGenomeIndexProperty() {
        genomeIndexProperty.set(mock(GenomeIndex.class));
        genomeIndexProperty.set(genomeIndex);

        assertThat(graphAnnotation.getAnnotationsInRange(12, 42)).isEmpty();
    }

    /**
     * Tests that annotations are properly updated when the genome index updates.
     */
    @Test
    void testUpdateGffFileProperty() {
        final Annotation annotation = new Annotation("source", "type", 33, 63, 71.0, "strand", 26);
        annotationCollection.addAnnotation(annotation);

        gffFileProperty.set(mock(GffFile.class));
        gffFileProperty.set(gffFile);

        assertThat(graphAnnotation.getAnnotationsInRange(34, 42)).contains(annotation);
    }


    /**
     * Mocks the {@link GenomeNavigation} and its dependencies.
     */
    private void initGenomeNavigationMock() throws SQLException {
        genomeNavigation = mock(GenomeNavigation.class);

        genomeIndex = mock(GenomeIndex.class);
        when(genomeIndex.getGenomePoint(anyString(), anyInt()))
                .thenAnswer(invocation -> Optional.of(new GenomePoint(0, 0, invocation.getArgument(1))));

        genomeIndexProperty = new SimpleObjectProperty<>(genomeIndex);
        when(genomeNavigation.getGenomeIndexProperty()).thenReturn(genomeIndexProperty);
    }

    /**
     * Mocks the {@link GraphStore} and its dependencies.
     */
    private void initGraphStoreMock() {
        graphStore = mock(GraphStore.class);

        gffFile = mock(GffFile.class);
        gffFileProperty = new SimpleObjectProperty<>(gffFile);
        when(graphStore.getGffFileProperty()).thenReturn(gffFileProperty);

        annotationCollection = new AnnotationCollection("sequence");
        when(gffFile.getAnnotationCollection()).thenReturn(annotationCollection);
    }
}
