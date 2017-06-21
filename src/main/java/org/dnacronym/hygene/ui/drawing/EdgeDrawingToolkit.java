package org.dnacronym.hygene.ui.drawing;

import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.List;


/**
 * Toolkit used to draw edges.
 * <p>
 * This includes drawing an edge, drawing the paths that go through and edge, and drawing annotations below an edge.
 */
public final class EdgeDrawingToolkit extends DrawingToolkit {
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
        drawEdgeGenomes(fromX, fromY, toX, toY, edgeWidth, Collections.singletonList(color));
    }

    /**
     * Draws a single edge from a given to point to a destination point.
     * <p>
     * The genome colors are spread evenly across the height of the edge. They are drawn as lanes along the edge.
     *
     * @param fromX      the x position of the origin of the edge
     * @param fromY      the y position of the origin of the edge
     * @param toX        the x position of the destination of the edge
     * @param toY        the y position of the destination of the edge
     * @param edgeWidth  the width of the edge
     * @param pathColors the colors of the paths going through the edge
     */
    public void drawEdgeGenomes(final double fromX, final double fromY, final double toX, final double toY,
                                final double edgeWidth, final List<Color> pathColors) {
        final double laneHeight = edgeWidth / pathColors.size();
        getGraphicsContext().setLineWidth(laneHeight);

        double laneFromOffset = fromY;
        double laneToOffset = toY;
        for (final Color color : pathColors) {
            getGraphicsContext().setStroke(color);
            getGraphicsContext().strokeLine(fromX, laneFromOffset, toX, laneToOffset);

            laneFromOffset += laneHeight;
            laneToOffset += laneHeight;
        }
    }

    /**
     * Draws annotations below an edge.
     * <p>
     * Annotations have the given colors, and are dashed.
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
        getGraphicsContext().setLineDashes(ANNOTATION_DASH_LENGTH);
        getGraphicsContext().setLineWidth(getAnnotationHeight());

        double annotationFromOffset = fromY + edgeWidth + getAnnotationHeight() + getAnnotationHeight() / 2;
        double annotationToOffset = toY + edgeWidth + getAnnotationHeight() + getAnnotationHeight() / 2;
        for (final Color color : annotationColors) {
            getGraphicsContext().setStroke(color);
            getGraphicsContext().strokeLine(fromX, annotationFromOffset, toX, annotationToOffset);

            annotationFromOffset += getAnnotationHeight();
            annotationToOffset += getAnnotationHeight();
        }

        getGraphicsContext().setLineDashes(ANNOTATION_DASH_DEFAULT);
    }
}
