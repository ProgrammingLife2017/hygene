package org.dnacronym.hygene.ui.drawing;

import javafx.scene.canvas.GraphicsContext;


/**
 * These toolkit's are used for drawing.
 */
public abstract class DrawingToolkit {
    static final double ANNOTATION_HEIGHT = 5;
    static final double ANNOTATION_DASH_LENGTH = 10;

    GraphicsContext graphicsContext;


    /**
     * Sets the {@link GraphicsContext} used for drawing by the toolkit.
     *
     * @param graphicsContext the {@link GraphicsContext} to use for drawing
     */
    public final void setGraphicsContext(final GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }
}
