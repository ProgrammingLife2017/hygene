package org.dnacronym.hygene.ui.graph;

import javafx.scene.canvas.GraphicsContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Segment;
import org.dnacronym.hygene.models.FeatureAnnotation;
import org.dnacronym.hygene.parser.GffFile;
import org.dnacronym.hygene.persistence.FileDatabase;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;


/**
 *
 */
@SuppressWarnings("PMD")
public final class GraphAnnotationVisualizer {
    private static final Logger LOGGER = LogManager.getLogger(GraphAnnotationVisualizer.class);
    private static final int ANNOTATION_MARKER_HEIGHT = 10;

    private final GraphStore graphStore;
    private final GraphDimensionsCalculator graphDimensionsCalculator;
    private GenomeIndex genomeIndex;

    private GraphicsContext graphicsContext;
    private double canvasWidth;


    /**
     * @param graphStore
     * @param graphDimensionsCalculator
     */
    public GraphAnnotationVisualizer(final GraphStore graphStore,
                                     final GraphDimensionsCalculator graphDimensionsCalculator) {
        this.graphStore = graphStore;
        this.graphDimensionsCalculator = graphDimensionsCalculator;

        graphStore.getGfaFileProperty().addListener((observable, oldGfaFile, newGfaFile) -> {
            try {
                genomeIndex = new GenomeIndex(newGfaFile, new FileDatabase(newGfaFile.getFileName()));
            } catch (final SQLException | IOException e) {
                LOGGER.error(e);
            }
        });
    }


    /**
     * @param graphicsContext
     */
    public void setGraphicsContext(final GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    /**
     * @param canvasWidth
     */
    public void setCanvasWidth(final double canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    /**
     * @param nodes
     * @throws SQLException
     */
    public void draw(final Collection<NewNode> nodes) {
        if (genomeIndex == null || graphicsContext == null) {
            return;
        }

        final GenomePoint[] genomePoints = new GenomePoint[2];

        for (final GffFile gffFile : graphStore.getGffFiles()) {
            final FeatureAnnotation featureAnnotation = gffFile.getFeatureAnnotation();

            final String genome = featureAnnotation.getSequenceId();
            final int startOffset = featureAnnotation.getSubFeatureAnnotations().get(0).getStart();
            final int endOffset = featureAnnotation.getSubFeatureAnnotations().get(0).getEnd();

            final boolean[] present = {false};
            try {
                genomeIndex.getGenomePoint(genome, startOffset)
                        .ifPresent(genomePointStart -> {
                            genomePoints[0] = genomePointStart;
                            present[0] = true;
                        });
                genomeIndex.getGenomePoint(genome, endOffset)
                        .ifPresent(genomePointEnd -> {
                            genomePoints[1] = genomePointEnd;
                            present[0] = true;
                        });
                if (!present[0]) {
                    continue;
                }

                final GenomePoint genomePointStart = genomePoints[0];
                final GenomePoint genomePointEnd = genomePoints[1];

                final int startNodeId = genomePointStart.getNodeId();
                final int endNodeId = genomePointEnd.getNodeId();

                final Segment[] filteredNodes = (Segment[]) nodes.stream().filter(node ->
                        node instanceof Segment
                                && (((Segment) node).getId() == startNodeId
                                || ((Segment) node).getId() == endNodeId)).toArray();

                if (filteredNodes.length == 1 && filteredNodes[0].getId() == startNodeId) {
                    drawFromStart(filteredNodes[0], genomePointStart.getBaseOffsetInNode());
                } else if (filteredNodes.length == 1) {
                    drawTillEnd(filteredNodes[0], genomePointEnd.getBaseOffsetInNode());
                } else if (filteredNodes.length == 2 && filteredNodes[0].getId() == endNodeId) {
                    drawWhole(filteredNodes[0], genomePointStart.getBaseOffsetInNode(),
                            filteredNodes[1], genomePointEnd.getBaseOffsetInNode());
                }
            } catch (final SQLException e) {
                LOGGER.error("Unable to get genome point of '" + genome + "'.", e);
            }
        }
    }

    /**
     * @param endSegment
     * @param baseOffsetInNode
     */
    private void drawTillEnd(final Segment endSegment, final int baseOffsetInNode) {
        final double endX = graphDimensionsCalculator.computeXPosition(endSegment)
                + (double) baseOffsetInNode / endSegment.getLength()
                * graphDimensionsCalculator.computeWidth(endSegment);

        graphicsContext.fillRect(endX, 0, canvasWidth, ANNOTATION_MARKER_HEIGHT);
    }

    /**
     * @param startSegment
     * @param baseOffsetInNode
     */
    private void drawFromStart(final Segment startSegment, final int baseOffsetInNode) {
        final double endX = graphDimensionsCalculator.computeXPosition(startSegment)
                + (double) baseOffsetInNode / startSegment.getLength()
                * graphDimensionsCalculator.computeWidth(startSegment);

        graphicsContext.fillRect(endX, 0, canvasWidth, ANNOTATION_MARKER_HEIGHT);
    }

    /**
     * @param startSegment
     * @param baseOffsetInStartNode
     * @param endSegment
     * @param baseOffsetInEndNode
     */
    private void drawWhole(final Segment startSegment, final int baseOffsetInStartNode,
                           final Segment endSegment, final int baseOffsetInEndNode) {
        final double startX = graphDimensionsCalculator.computeXPosition(startSegment)
                + (double) baseOffsetInStartNode / startSegment.getLength()
                * graphDimensionsCalculator.computeWidth(startSegment);
        final double endX = graphDimensionsCalculator.computeXPosition(endSegment)
                + (double) baseOffsetInEndNode / endSegment.getLength()
                * graphDimensionsCalculator.computeWidth(endSegment);

        graphicsContext.fillRect(startX, 0, endX - startX, ANNOTATION_MARKER_HEIGHT);
    }
}
