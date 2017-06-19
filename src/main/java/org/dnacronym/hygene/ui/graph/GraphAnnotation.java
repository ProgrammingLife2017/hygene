package org.dnacronym.hygene.ui.graph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.graph.annotation.Annotation;
import org.dnacronym.hygene.graph.annotation.AnnotationCollection;
import org.dnacronym.hygene.ui.genomeindex.GenomeNavigation;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Stores all {@link AnnotationCollection} of the {@link org.dnacronym.hygene.parser.GffFile}s in memory for quick
 * retrieval.
 */
@SuppressWarnings("PMD.ImmutableField") // The values are set via event listeners, so they should not be immutable
public final class GraphAnnotation {
    private static final Logger LOGGER = LogManager.getLogger(GraphAnnotation.class);

    private final Map<Annotation, GenomePoint> startPoints;
    private final Map<Annotation, GenomePoint> endPoints;

    private GenomeIndex genomeIndex;
    private AnnotationCollection annotationCollection;


    /**
     * Constructs a new {@link GraphAnnotation}.
     *
     * @param genomeNavigation the {@link GenomeNavigation} used to retrieve
     *                         {@link org.dnacronym.hygene.coordinatesystem.GenomeIndex}es
     * @param graphStore       the {@link GraphStore} whose {@link org.dnacronym.hygene.parser.GffFile}s are used to
     *                         update the {@link AnnotationCollection}s
     */
    @Inject
    public GraphAnnotation(final GenomeNavigation genomeNavigation, final GraphStore graphStore) {
        this.startPoints = new HashMap<>();
        this.endPoints = new HashMap<>();
        this.genomeIndex = genomeNavigation.getGenomeIndexProperty().get();

        genomeNavigation.getGenomeIndexProperty().addListener((observable, oldValue, newValue) -> {
            genomeIndex = newValue;
            recalculateAnnotationPoints();
        });
        graphStore.getGffFileProperty().addListener((observable, oldValue, newValue) -> {
            annotationCollection = newValue.getAnnotationCollection();
            recalculateAnnotationPoints();
        });
    }


    /**
     * Returns a list of the {@link Annotation}s that are in the specified range.
     * <p>
     * This method assumes that node ids are in topological order.
     *
     * @param rangeStart the id of the left-most node
     * @param rangeEnd   the id of the right-most node
     * @return a list of the {@link Annotation}s that are in the specified range
     */
    public List<Annotation> getAnnotationsInRange(final int rangeStart, final int rangeEnd) {
        return annotationCollection.getAnnotations().stream()
                .filter(annotation -> {
                    final int annotationStart = startPoints.get(annotation).getNodeId();
                    final int annotationEnd = endPoints.get(annotation).getNodeId();

                    return rangeStart < annotationEnd && rangeEnd >= annotationStart;
                })
                .collect(Collectors.toList());
    }

    /**
     * Adds a {@link AnnotationCollection}, and add {@link GenomePoint}s which denote the start and end points of this
     * annotation in the graph.
     */
    private void recalculateAnnotationPoints() {
        startPoints.clear();
        endPoints.clear();

        final String genomeName = annotationCollection.getSequenceId();
        annotationCollection.getAnnotations().forEach(annotation -> {
            final int startOffset = annotation.getStart();
            final int endOffset = annotation.getEnd();

            try {
                genomeIndex.getGenomePoint(genomeName, startOffset)
                        .ifPresent(genomePoint -> startPoints.put(annotation, genomePoint));
                genomeIndex.getGenomePoint(genomeName, endOffset)
                        .ifPresent(genomePoint -> endPoints.put(annotation, genomePoint));
            } catch (final SQLException e) {
                LOGGER.error("Could not add an annotation.", e);
            }
        });
    }
}
