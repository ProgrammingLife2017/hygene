package org.dnacronym.hygene.ui.graph;

import javafx.scene.canvas.GraphicsContext;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Segment;

import java.util.List;


/**
 *
 */
@SuppressWarnings("PMD")
public final class GraphAnnotationVisualizer {
    private static final int ANNOTATION_MARKER_HEIGHT = 10;

    private final GraphDimensionsCalculator graphDimensionsCalculator;

    private GraphicsContext graphicsContext;
    private double canvasWidth;


    /**
     * @param graphDimensionsCalculator
     */
    public GraphAnnotationVisualizer(final GraphDimensionsCalculator graphDimensionsCalculator) {
        this.graphDimensionsCalculator = graphDimensionsCalculator;
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
     * @param genomePoints
     */
    public void draw(final List<GenomePoint> genomePoints, final List<NewNode> nodes) {
        final int startNodeId = genomePoints.get(0).getNodeId();
        final int startOffset = genomePoints.get(0).getBaseOffsetInNode();
        final int endNodeId = genomePoints.get(1).getNodeId();
        final int endOffset = genomePoints.get(1).getBaseOffsetInNode();

        double startX = 0;
        double endX = 0;
        for (final NewNode newNode : nodes) {
            if (newNode instanceof Segment && ((Segment) newNode).getId() == startNodeId) {
                startX = graphDimensionsCalculator.computeXPosition(newNode)
                        + (double) startOffset / newNode.getLength() * graphDimensionsCalculator.computeWidth(newNode);
            }
            if (newNode instanceof Segment && ((Segment) newNode).getId() == endNodeId) {
                endX = graphDimensionsCalculator.computeXPosition(newNode)
                        + (double) endOffset / newNode.getLength() * graphDimensionsCalculator.computeWidth(newNode);
            }
        }

        if (endX < startX) { // no end node found
            endX = canvasWidth;
        }

        graphicsContext.fillRect(startX, 0, endX - startX, ANNOTATION_MARKER_HEIGHT);
    }
}
