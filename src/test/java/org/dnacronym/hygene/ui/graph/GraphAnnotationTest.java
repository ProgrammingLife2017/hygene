package org.dnacronym.hygene.ui.graph;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.graph.annotation.AnnotationCollection;
import org.dnacronym.hygene.parser.GffFile;
import org.dnacronym.hygene.ui.genomeindex.GenomeNavigation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        graphAnnotation = new GraphAnnotation(graphStore);
    }


    /**
     * Tests that a range start greater than the range end causes an {@link IllegalArgumentException}.
     */
    @Test
    void testRangeStartAfterEnd() {
        assertThatThrownBy(() -> graphAnnotation.getAnnotationsInRange(95, 9))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests that a negative range start causes an {@link IllegalArgumentException}.
     */
    @Test
    void testNegativeRangeStart() {
        assertThatThrownBy(() -> graphAnnotation.getAnnotationsInRange(-2, 33))
                .isInstanceOf(IllegalArgumentException.class);
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
        graphAnnotation = new GraphAnnotation(graphStore);

        assertThat(graphAnnotation.getAnnotationsInRange(11, 24)).isEmpty();
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
     * Tests that the sequence id of the {@link AnnotationCollection} is correctly retrieved.
     */
    @Test
    void testGetSequenceId() {
        assertThat(annotationCollection.getSequenceId()).isEqualTo("sequence");
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
