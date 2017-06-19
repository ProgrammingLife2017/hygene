package org.dnacronym.hygene.ui.drawing;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.List;


public class EdgeDrawingToolkit implements DrawingToolkit {
    private static final int ANNOTATION_WIDTH = 5;
    private static final int VERTICAL_ANNOTATION_COUNT = 10;

    private GraphicsContext graphicsContext;

    @Override
    public void setGraphicsContext(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    public void fillEdge(final double fromX, final double fromY, final double toX, final double toY,
                         final double edgeWidth, final Color color) {
        fillEdge(fromX, fromY, toX, toY, edgeWidth, Collections.singletonList(color));
    }

    public void fillEdge(final double fromX, final double fromY, final double toX, final double toY,
                         final double edgeWidth, final List<Color> colors) {
        final double lineWidth = edgeWidth / colors.size();
        graphicsContext.setLineWidth(lineWidth);

        double colorFromY = fromY;
        double colorToY = toY;
        for (final Color color : colors) {
            graphicsContext.setStroke(color);
            graphicsContext.strokeLine(fromX, colorFromY, toX, colorToY);

            colorFromY += lineWidth;
            colorToY += lineWidth;
        }
    }

    public void drawEdgeAnnotations(final double fromX, final double fromY, final double toX, final double toY,
                                    final double lineWidth, final List<Color> colors) {
        double annotationFromX = fromX;
        double annotationFromY = fromY;
        final double deltaY = toY - fromY;

        graphicsContext.setLineWidth(ANNOTATION_WIDTH);
        int colorIndex = 0;

        while (annotationFromX <= toX) {
            final Color color = colors.get(colorIndex);

            graphicsContext.setFill(color);
            graphicsContext.strokeLine(annotationFromX, annotationFromY, toX, toY);

            annotationFromX += ANNOTATION_WIDTH;
            annotationFromY = deltaY / VERTICAL_ANNOTATION_COUNT;

            colorIndex = (colorIndex + 1) % colors.size();
        }
    }
}
