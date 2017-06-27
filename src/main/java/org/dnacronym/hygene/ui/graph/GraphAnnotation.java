package org.dnacronym.hygene.ui.graph;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.graph.annotation.Annotation;
import org.dnacronym.hygene.graph.annotation.AnnotationCollection;
import org.dnacronym.hygene.ui.dialogue.WarningDialogue;
import org.dnacronym.hygene.ui.genomeindex.GenomeMappingView;
import org.dnacronym.hygene.ui.genomeindex.GenomeNavigation;
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
    private GenomeNavigation genomeNavigation;
    @Inject
    private StatusBar statusBar;

    private String mappedGenome;
    private StringProperty sequenceIdProperty;

    private final ObjectProperty<AnnotationCollection> annotationCollectionProperty;


    /**
     * Constructs a new {@link GraphAnnotation}.
     *
     * @param graphStore the injected {@link GraphStore} instance
     */
    @Inject
    public GraphAnnotation(final GraphStore graphStore) {
        this.startPoints = new HashMap<>();
        this.endPoints = new HashMap<>();
        this.sequenceIdProperty = new SimpleStringProperty();

        annotationCollectionProperty = new SimpleObjectProperty<>();

        Optional.ofNullable(graphStore.getGffFileProperty().get()).ifPresent(gffFile -> {
            annotationCollectionProperty.set(gffFile.getAnnotationCollection());
            if (annotationCollectionProperty.get() == null) {
                return;
            }

            sequenceIdProperty.set(annotationCollectionProperty.get().getSequenceId());

            if (genomeMappingView != null) {
                try {
                    genomeMappingView.showAndWait();
                } catch (final UIInitialisationException e) {
                    LOGGER.error("Unable to show genome mapping view.", e);
                }
            }
        });

        graphStore.getGffFileProperty().addListener((observable, oldValue, newValue) -> {
            annotationCollectionProperty.set(null);
            startPoints.clear();
            endPoints.clear();

            if (newValue == null) {
                return;
            }

            annotationCollectionProperty.set(newValue.getAnnotationCollection());
            sequenceIdProperty.set(annotationCollectionProperty.get().getSequenceId());

            if (genomeMappingView != null) {
                try {
                    genomeMappingView.showAndWait();
                } catch (final UIInitialisationException e) {
                    LOGGER.error("Unable to show genome mapping view.", e);
                }
            }
        });
    }

    /**
     * Sets the mapped genome.
     * <p>
     * This genome represents what the genome of the current loaded GFF file should map onto in the GFA file. This also
     * prompts the internal {@link GenomeIndex} to re-index the genomes based on the given genome and the current
     * GFA file.<br>
     * Afterwards recalculates the annotation start and end points.
     *
     * @param mappedGenome the genome in the GFA the GFF genome should map onto
     * @throws IOException if unable to build an index for the given mapped genome
     */
    public void setMappedGenome(final String mappedGenome) throws IOException {
        this.mappedGenome = mappedGenome;

        LOGGER.info("Building an index for " + mappedGenome);

        genomeNavigation.runActionOnIndexedGenome(mappedGenome, genomeIndex -> Platform.runLater(() -> {
            LOGGER.info("Finished building an index for " + mappedGenome);
            recalculateAnnotationPoints(genomeIndex);
        }));
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
        if (annotationCollectionProperty.get() == null) {
            return new ArrayList<>();
        }

        return annotationCollectionProperty.get().getAnnotations().stream()
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
     * Returns the list of annotations going through the given node.
     *
     * @param nodeId the node id
     * @return the list of {@link Annotation}s going through the given node
     */
    public List<Annotation> getAnnotationsOfNode(final int nodeId) {
        if (nodeId < 0) {
            throw new IllegalArgumentException("Node id cant be negative.");
        }
        if (annotationCollection == null) {
            return new ArrayList<>();
        }

        return annotationCollection.getAnnotations().stream()
                .filter(annotation -> nodeId >= annotation.getStartNodeId() && nodeId < annotation.getEndNodeId())
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
     * Returns all annotations.
     *
     * @return all annotations
     */
    public ReadOnlyObjectProperty<AnnotationCollection> getAnnotationCollectionProperty() {
        return annotationCollectionProperty;
    }

    /**
     * Adds a {@link AnnotationCollection}, and add {@link org.dnacronym.hygene.coordinatesystem.GenomePoint}s which
     * denote the start and end points of this annotation in the graph.
     * <p>
     * If the mappedGenome is not set ({@code null} or empty), it will divert to using the sequence id
     * directly of the {@link AnnotationCollection}.
     *
     * @param genomeIndex the {@link GenomeIndex} instance
     */
    private void recalculateAnnotationPoints(final GenomeIndex genomeIndex) {
        startPoints.clear();
        endPoints.clear();

        if (annotationCollectionProperty.get() == null || statusBar == null) {
            return;
        }

        statusBar.monitorTask(progressUpdater -> {
            final int[] position = {1};
            final int total = annotationCollectionProperty.get().getAnnotations().size();
            LOGGER.info("Started placing " + total + " annotations");

            annotationCollectionProperty.get().getAnnotations().forEach(annotation -> {
                if (position[0] % PROGRESS_UPDATE_INTERVAL == 0) {
                    progressUpdater.updateProgress(
                            Math.round(Math.max(5, 100f * position[0] / total)),
                            "Placing annotation " + position[0] + "/" + total
                    );
                }

                final int startOffset = annotation.getStart();
                final int endOffset = annotation.getEnd();

                final int startNodeId = genomeIndex.getNodeByBase(startOffset);
                final int startNodeBaseOffset = genomeIndex.getBaseOffsetWithinNode(startOffset);
                final int endNodeId = genomeIndex.getNodeByBase(endOffset);
                final int endNodeBaseOffset = genomeIndex.getBaseOffsetWithinNode(endOffset);

                if (startNodeId != -1 && endNodeId != -1) {
                    annotation.setStartNodeId(startNodeId);
                    annotation.setStartNodeBaseOffset(startNodeBaseOffset);
                    annotation.setEndNodeId(endNodeId);
                    annotation.setEndNodeBaseOffset(endNodeBaseOffset);

                    startPoints.put(annotation, startNodeId);
                    endPoints.put(annotation, endNodeId);
                }

                position[0]++;
            });

            if (position[0] < total) {
                new WarningDialogue("Unable to place " + (total - position[0]) + " annotations.").show();
            }

            LOGGER.info("Finished placing " + position[0] + " of " + total + " annotations");
            progressUpdater.updateProgress(StatusBar.PROGRESS_MAX, "Finished placing annotations");
        });
    }
}
