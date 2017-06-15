package org.dnacronym.hygene.ui.graph;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Segment;

import java.util.List;


/**
 *
 */
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
     * Draws a marker at the top of the graph denoting the start end of a
     * {@link org.dnacronym.hygene.models.FeatureAnnotation}.
     * <p>
     * If the {@link GenomePoint}s list is smaller than 2, nothing is drawn, as there are no clear indicators for start
     * and end nodes.
     *
     * @param genomePoints the list of {@link GenomePoint}s which denote the start and end of a
     *                     {@link org.dnacronym.hygene.models.FeatureAnnotation}
     * @param nodes        the list of current onscreen {@link NewNode}s
     */
    public void draw(final List<GenomePoint> genomePoints, final List<NewNode> nodes) {
        if (genomePoints.size() < 2) {
            return;
        }

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

        graphicsContext.setFill(Color.LIGHTBLUE);
        graphicsContext.fillRect(startX, 0, endX - startX, ANNOTATION_MARKER_HEIGHT);
    }
}
