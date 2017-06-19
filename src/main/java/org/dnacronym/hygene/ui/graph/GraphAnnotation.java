package org.dnacronym.hygene.ui.graph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.graph.annotation.AnnotationCollection;
import org.dnacronym.hygene.ui.genomeindex.GenomeNavigation;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Stores all {@link AnnotationCollection} of the {@link org.dnacronym.hygene.parser.GffFile}s in memory for quick
 * retrieval.
 */
@SuppressWarnings("PMD.ImmutableField") // The values are set via event listeners, so they should not be immutable
public final class GraphAnnotation {
    private static final Logger LOGGER = LogManager.getLogger(GraphAnnotation.class);

    private GenomeIndex genomeIndex;

    private Map<AnnotationCollection, List<GenomePoint>> genomeIndexMap = new HashMap<>();
    /**
     * List that allows faster iteration over all {@link AnnotationCollection}s.
     */
    private List<AnnotationCollection> annotationCollections = new ArrayList<>();

    /**
     * Creates an instance of {@link GraphAnnotation}.
     *
     * @param genomeNavigation the {@link GenomeNavigation} used to retrieve
     *                         {@link org.dnacronym.hygene.coordinatesystem.GenomeIndex}es
     * @param graphStore       the {@link GraphStore} whose {@link org.dnacronym.hygene.parser.GffFile}s are used to
     *                         update the {@link AnnotationCollection}s
     */
    @Inject
    public GraphAnnotation(final GenomeNavigation genomeNavigation, final GraphStore graphStore) {
        genomeIndex = genomeNavigation.getGenomeIndexProperty().get();
        genomeNavigation.getGenomeIndexProperty().addListener((observable, oldValue, newValue) -> {
            genomeIndex = newValue;
            genomeIndexMap.clear();
        });

        graphStore.getGffFileProperty().addListener((observable, oldValue, newValue) -> {
            final AnnotationCollection annotationCollection = newValue.getAnnotationCollection();
            addFeatureAnnotation(annotationCollection);
        });
    }


    /**
     * Adds a {@link AnnotationCollection}, and add {@link GenomePoint}s which denote the start and end points of this
     * annotation in the graph.
     *
     * @param annotationCollection the {@link AnnotationCollection} to add
     * @throws SQLException if an error occurred whilst creating {@link GenomePoint}s
     */
    public void addFeatureAnnotation(final AnnotationCollection annotationCollection) {
        final String genomeName = annotationCollection.getSequenceId();
        final int startOffset = annotationCollection.getAnnotations().get(0).getStart();
        final int endOffset = annotationCollection.getAnnotations().get(0).getEnd();

        final List<GenomePoint> genomePoints = new ArrayList<>(2);

        try {
            genomeIndex.getGenomePoint(genomeName, startOffset).ifPresent(genomePoints::add);
            genomeIndex.getGenomePoint(genomeName, endOffset).ifPresent(genomePoints::add);

            genomeIndexMap.put(annotationCollection, genomePoints);
            annotationCollections.add(annotationCollection);
        } catch (final SQLException e) {
            LOGGER.error("Unable to add an annotation.", e);
        }
    }

    /**
     * Returns the {@link Map} of {@link AnnotationCollection}s and {@link GenomePoint}s denoting the start and end
     * points of each {@link AnnotationCollection}.
     *
     * @return the {@link Map} of {@link AnnotationCollection}s and {@link GenomePoint}s denoting the start and end
     * points of each {@link AnnotationCollection}.
     */
    public Map<AnnotationCollection, List<GenomePoint>> getGenomeIndexMap() {
        return genomeIndexMap;
    }

    /**
     * Returns the {@link List} of {@link AnnotationCollection}s stored internally.
     *
     * @return the {@link List} of {@link AnnotationCollection}s stored internally
     */
    public List<AnnotationCollection> getAnnotationCollections() {
        return annotationCollections;
    }
}
