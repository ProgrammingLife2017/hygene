package org.dnacronym.hygene.ui.visualizer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dnacronym.hygene.models.SequenceGraph;

/**
 * A simple canvas that allows drawing of primitive shapes.
 */
public class GraphPrimitives extends Pane {
    private Canvas canvas;
    private GraphicsContext graphicsContext;

    public GraphPrimitives() {
        canvas = new Canvas();
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        graphicsContext = canvas.getGraphicsContext2D();

        this.getChildren().add(canvas);
    }

    public final void drawLine(final int startX, final int startY, final int endX, final int endY, final Color fill) {
        graphicsContext.setFill(fill);
        graphicsContext.strokeLine(startX, startY, endX, endY);
    }

    public final void drawRectangle(final int topLeftX, final int topLeftY,
                                    final int bottomRightX, final int bottomRightY,
                                    final Color fill, final String sequence) {
        graphicsContext.setFill(fill);
        graphicsContext.fillRect(topLeftX, topLeftY, bottomRightX - topLeftX, bottomRightY - topLeftY);

        graphicsContext.fillText(sequence, bottomRightX, topLeftY, topLeftX - bottomRightX);
    }

    public final void clear() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Populate the graphs primitives with the given sequence graph.
     *
     * @param sequenceGraph {@link SequenceGraph} to populate canvas with.
     */
    public void visualise(@NonNull SequenceGraph sequenceGraph) {

    }
}
