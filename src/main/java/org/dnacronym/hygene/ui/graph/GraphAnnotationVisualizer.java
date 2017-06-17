package org.dnacronym.hygene.ui.graph;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.node.Segment;

import java.util.List;


/**
 * Visualizes all {@link org.dnacronym.hygene.models.FeatureAnnotation}s stored in {@link GraphAnnotation} in the graph
 * view.
 */
public final class GraphAnnotationVisualizer {
    private static final int ANNOTATION_MARKER_HEIGHT = 15;

    private final GraphDimensionsCalculator graphDimensionsCalculator;

    private GraphicsContext graphicsContext;
    private double canvasWidth;


    /**
     * Creates an instance of {@link GraphAnnotationVisualizer}.
     *
     * @param graphDimensionsCalculator the {@link GraphDimensionsCalculator} used to calculate the position of the
     *                                  annotations onscreen
     */
    public GraphAnnotationVisualizer(final GraphDimensionsCalculator graphDimensionsCalculator) {
        this.graphDimensionsCalculator = graphDimensionsCalculator;
    }


    /**
     * Sets the {@link GraphicsContext} used for drawing.
     *
     * @param graphicsContext the {@link GraphicsContext} used for drawing
     */
    public void setGraphicsContext(final GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    /**
     * Sets the canvas width.
     *
     * @param canvasWidth the canvas width
     */
    public void setCanvasWidth(final double canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    /**
     * Draws a marker at the top of the graph denoting the start end of a
     * {@link org.dnacronym.hygene.models.FeatureAnnotation}.
     * <p>
     * If the {@link GenomePoint}s list is smaller than 2, nothing is drawn, as there are no clear indicators for start
     * and end nodes.
     *
     * @param seqId        the sequence id of the current annotation
     * @param genomePoints the list of {@link GenomePoint}s which denote the start and end of a
     *                     {@link org.dnacronym.hygene.models.FeatureAnnotation}
     * @param nodes        the list of current onscreen {@link Node}s
     */
    public void draw(final String seqId, final List<GenomePoint> genomePoints, final List<Node> nodes) {
        if (genomePoints.size() < 2) {
            return;
        }

        final int startNodeId = genomePoints.get(0).getNodeId();
        final int startOffset = genomePoints.get(0).getBaseOffsetInNode();
        final int endNodeId = genomePoints.get(1).getNodeId();
        final int endOffset = genomePoints.get(1).getBaseOffsetInNode();

        double startX = 0;
        double endX = canvasWidth;

        int minOnscreenId = Integer.MAX_VALUE;
        int maxOnscreenId = 0;
        boolean endpointFound = false;

        for (final Node node : nodes) {
            if (!(node instanceof Segment)) {
                continue;
            }
            final Segment segment = (Segment) node;

            maxOnscreenId = Math.max(maxOnscreenId, segment.getId());
            minOnscreenId = Math.min(minOnscreenId, segment.getId());

            if (segment.getId() == startNodeId) {
                startX = graphDimensionsCalculator.computeXPosition(node)
                        + (double) startOffset / node.getLength() * graphDimensionsCalculator.computeWidth(node);
            }
            if (segment.getId() == endNodeId) {
                endX = graphDimensionsCalculator.computeXPosition(node)
                        + (double) endOffset / node.getLength() * graphDimensionsCalculator.computeWidth(node);
                endpointFound = true;
            }
        }

        if (!endpointFound && endNodeId < minOnscreenId) {
            endX = 0;
        }

        graphicsContext.setFill(Color.LIGHTBLUE);
        graphicsContext.fillRect(startX, 0, endX - startX, ANNOTATION_MARKER_HEIGHT);
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText(seqId, startX, ANNOTATION_MARKER_HEIGHT);
    }
}
