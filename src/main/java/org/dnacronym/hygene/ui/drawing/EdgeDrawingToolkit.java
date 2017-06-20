package org.dnacronym.hygene.ui.drawing;

import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.List;


/**
 * Toolkit used to draw edges.
 * <p>
 * This includes drawing an edge, drawing the paths that go through and edge and drawing annotations below an edge.
 */
public class EdgeDrawingToolkit extends DrawingToolkit {
    /**
     * Draws a single edge from a given to point to a destination point.
     *
     * @param fromX     the x position of the origin of the edge
     * @param fromY     the y position of the origin of the edge
     * @param toX       the x position of the destination of the edge
     * @param toY       the y position of the destination of the edge
     * @param edgeWidth the width of the edge
     * @param color     the color of the edge
     */
    public void drawEdge(final double fromX, final double fromY, final double toX, final double toY,
                         final double edgeWidth, final Color color) {
        drawEdgePaths(fromX, fromY, toX, toY, edgeWidth, Collections.singletonList(color));
    }

    /**
     * Draws a single edge from a given to point to a destination point.
     * <p>
     * The path colors are spread evenly across the width of the edge. They are drawn as bands along the edge.
     *
     * @param fromX      the x position of the origin of the edge
     * @param fromY      the y position of the origin of the edge
     * @param toX        the x position of the destination of the edge
     * @param toY        the y position of the destination of the edge
     * @param edgeWidth  the width of the edge
     * @param pathColors the colors of the paths going through the edge
     */
    public void drawEdgePaths(final double fromX, final double fromY, final double toX, final double toY,
                              final double edgeWidth, final List<Color> pathColors) {
        final double lineHeight = edgeWidth / pathColors.size();
        graphicsContext.setLineWidth(lineHeight);

        double pathFromY = fromY;
        double pathToY = toY;
        for (final Color color : pathColors) {
            graphicsContext.setStroke(color);
            graphicsContext.strokeLine(fromX, pathFromY, toX, pathToY);

            pathFromY += lineHeight;
            pathToY += lineHeight;
        }
    }

    /**
     * Draws a single edge from a given to point to a destination point.
     * <p>
     * The path colors are spread evenly across the width of the edge. They are drawn as bands along the edge.<br>
     * Annotations are drawn as strands below the node, each with height {@value ANNOTATION_HEIGHT}.
     *
     * @param fromX            the x position of the origin of the edge
     * @param fromY            the y position of the origin of the edge
     * @param toX              the x position of the destination of the edge
     * @param toY              the y position of the destination of the edge
     * @param edgeWidth        the width of the edge
     * @param annotationColors the colors of the annotations
     */
    public void drawEdgeAnnotations(final double fromX, final double fromY, final double toX, final double toY,
                                    final double edgeWidth, final List<Color> annotationColors) {
        graphicsContext.setLineDashes(ANNOTATION_DASH_LENGTH);
        graphicsContext.setLineWidth(ANNOTATION_HEIGHT);

        double annotationFromY = fromY + edgeWidth + ANNOTATION_HEIGHT + ANNOTATION_HEIGHT / 2;
        double annotationToY = toY + edgeWidth + ANNOTATION_HEIGHT + ANNOTATION_HEIGHT / 2;
        for (final Color color : annotationColors) {
            graphicsContext.setStroke(color);
            graphicsContext.strokeLine(fromX, annotationFromY, toX, annotationToY);

            annotationFromY += ANNOTATION_HEIGHT;
            annotationToY += ANNOTATION_HEIGHT;
        }

        graphicsContext.setLineDashes(1);
    }
}
