package org.dnacronym.hygene.ui.graph;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.coordinatesystem.DynamicGenomeIndex;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.graph.annotation.Annotation;
import org.dnacronym.hygene.graph.annotation.AnnotationCollection;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.ui.genomeindex.GenomeMappingView;
import org.dnacronym.hygene.ui.progressbar.StatusBar;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Stores a {@link AnnotationCollection} from a {@link org.dnacronym.hygene.parser.GffFile} in the context of a
 * {@link GraphStore}.
 */
@SuppressWarnings("PMD.ImmutableField") // The values are set via event listeners, so they should not be immutable
public final class GraphAnnotation {
    private static final Logger LOGGER = LogManager.getLogger(GraphAnnotation.class);
    private static final int PROGRESS_UPDATE_INTERVAL = 100;

    private final Map<Annotation, Integer> startPoints;
    private final Map<Annotation, Integer> endPoints;

    @Inject
    private GenomeMappingView genomeMappingView;
    @Inject
    private StatusBar statusBar;

    private GfaFile gfaFile;
    private DynamicGenomeIndex dynamicGenomeIndex;

    private String mappedGenome;
    private StringProperty sequenceIdProperty;

    private @Nullable AnnotationCollection annotationCollection;


    /**
     * Constructs a new {@link GraphAnnotation}.
     *
     * @param graphStore the {@link GraphStore} whose {@link org.dnacronym.hygene.parser.GffFile}s are used to
     *                   update the {@link AnnotationCollection}s
     */
    @Inject
    public GraphAnnotation(final GraphStore graphStore) {
        this.startPoints = new HashMap<>();
        this.endPoints = new HashMap<>();
        this.sequenceIdProperty = new SimpleStringProperty();

        Optional.ofNullable(graphStore.getGffFileProperty().get()).ifPresent(gffFile -> {
            annotationCollection = gffFile.getAnnotationCollection();
            if (annotationCollection == null) {
                return;
            }

            sequenceIdProperty.set(annotationCollection.getSequenceId());

            if (genomeMappingView != null) {
                try {
                    genomeMappingView.showAndWait();
                } catch (final UIInitialisationException e) {
                    LOGGER.error("Unable to showAndWait genome mapping view.", e);
                }
            }
        });

        graphStore.getGfaFileProperty().addListener((observable, oldValue, newValue) -> gfaFile = newValue);
        graphStore.getGffFileProperty().addListener((observable, oldValue, newValue) -> {
            annotationCollection = newValue.getAnnotationCollection();
            sequenceIdProperty.set(annotationCollection.getSequenceId());

            if (genomeMappingView != null) {
                try {
                    genomeMappingView.showAndWait();
                } catch (final UIInitialisationException e) {
                    LOGGER.error("Unable to showAndWait genome mapping view.", e);
                }
            }
        });
    }

    /**
     * Sets the mapped genome.
     * <p>
     * This genome represents what the genome of the current loaded GFF file should map onto in the GFA file. This also
     * prompts the internal {@link DynamicGenomeIndex} to re-index the genomes based on the given genome and the current
     * GFA file.<br>
     * Afterwards prompts a recalculation of annotation points.
     *
     * @param mappedGenome the genome in the GFA the GFF genome should map onto
     */
    public void setMappedGenome(final String mappedGenome) throws IOException {
        this.mappedGenome = mappedGenome;

        dynamicGenomeIndex = new DynamicGenomeIndex(gfaFile, mappedGenome);
        dynamicGenomeIndex.buildIndex();

        recalculateAnnotationPoints();
    }

    /**
     * Returns the genome onto which the user chose to map the annotations of the GFF file.
     *
     * @return the genome onto which the user chose to map the annotations of the GFF file
     */
    public String getMappedGenome() {
        return mappedGenome;
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
    @SuppressWarnings("squid:S3346") // False positive, asserts do not have side-effects
    public List<Annotation> getAnnotationsInRange(final int rangeStart, final int rangeEnd) {
        if (rangeStart > rangeEnd) {
            throw new IllegalArgumentException("Range start must be no larger than range end.");
        }
        if (rangeStart < 0) {
            throw new IllegalArgumentException("Range cannot start below 0.");
        }
        if (annotationCollection == null) {
            return new ArrayList<>();
        }

        return annotationCollection.getAnnotations().stream()
                .filter(annotation -> {
                    if (!startPoints.containsKey(annotation) || !endPoints.containsKey(annotation)) {
                        return false;
                    }

                    final int annotationStart = startPoints.get(annotation);
                    final int annotationEnd = endPoints.get(annotation);

                    return rangeStart < annotationEnd && rangeEnd >= annotationStart;
                })
                .collect(Collectors.toList());
    }

    /**
     * Returns the property which decides the sequence id of the currently loaded GFF file.
     *
     * @return the property which decides the sequence id of the currently loaded GFF file
     */
    public StringProperty getAnnotationsSequenceId() {
        return sequenceIdProperty;
    }

    /**
     * Adds a {@link AnnotationCollection}, and add {@link GenomePoint}s which denote the start and end points of this
     * annotation in the graph.
     * <p>
     * If the mappedGenome is not set ({@code null} or empty), it will divert to using the sequence id
     * directly of the {@link AnnotationCollection}.
     */
    private void recalculateAnnotationPoints() {
        startPoints.clear();
        endPoints.clear();

        if (dynamicGenomeIndex == null || annotationCollection == null || statusBar == null) {
            return;
        }

        statusBar.monitorTask(progressUpdater -> {
            final int[] position = {0};
            final int total = annotationCollection.getAnnotations().size();
            annotationCollection.getAnnotations().forEach(annotation -> {
                if (position[0] % PROGRESS_UPDATE_INTERVAL == 0) {
                    progressUpdater.updateProgress(
                            Math.round(100f * position[0] / total),
                            "Placing annotation " + position[0] + "/" + total
                    );
                }

                final int startOffset = annotation.getStart();
                final int endOffset = annotation.getEnd();

                final int startNodeId = dynamicGenomeIndex.getNodeByBase(startOffset);
                final int endNodeId = dynamicGenomeIndex.getNodeByBase(endOffset);

                if (startNodeId != -1 && endNodeId != -1) {
                    annotation.setStartNodeId(startNodeId);
                    annotation.setEndNodeId(endNodeId);

                    startPoints.put(annotation, startNodeId);
                    endPoints.put(annotation, endNodeId);
                }

                position[0]++;
            });
        });
    }
}
