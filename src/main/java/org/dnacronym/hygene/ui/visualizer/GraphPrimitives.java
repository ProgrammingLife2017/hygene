package org.dnacronym.hygene.ui.visualizer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dnacronym.hygene.models.SequenceGraph;


/**
 * A simple canvas that allows drawing of primitive shapes.
 * When passing a {@link SequenceGraph}, it will draw it using primitives.
 */
public class GraphPrimitives extends Pane {
    private Canvas canvas;
    private GraphicsContext graphicsContext;


    /**
     * Create a new {@link GraphPrimitives} instance.
     */
    public GraphPrimitives() {
        canvas = new Canvas();
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        graphicsContext = canvas.getGraphicsContext2D();

        this.getChildren().add(canvas);
    }


    /**
     * Draw line onscreen.
     *
     * @param startX x position of the start of the line.
     * @param startY y position of the start of the line.
     * @param endX   x position of the end of the line.
     * @param endY   y position of the end of the line.
     * @param color  color of the line.
     */
    public final void drawLine(final double startX, final double startY,
                               final double endX, final double endY, final Color color) {
        graphicsContext.setFill(color);
        graphicsContext.strokeLine(startX, startY, endX, endY);
    }

    /**
     * Draw a rectangle onscreen. The width and height should be given.
     *
     * @param startX x position of the leftmost position of the rectangle.
     * @param startY y position of the bottom of the rectangle.
     * @param width  width of the rectangle.
     * @param height height of the rectangle.
     * @param color  color of the rectangle.
     */
    public final void drawRectangle(final double startX, final double startY,
                                    final double width, final double height, final Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(startX, startY, startX + width, startY - height);
    }

    /**
     * Clear the canvas.
     */
    private final void clear() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Populate the graphs primitives with the given sequence graph.
     * First clears the graph before drawing.
     *
     * @param sequenceGraph {@link SequenceGraph} to populate canvas with.
     */
    public void visualise(@NonNull SequenceGraph sequenceGraph) {
        clear();

        final double bandCount = 1;

        final double stepSize = canvas.getHeight() / bandCount;
        final double offSet = stepSize / 2;
    }
}
