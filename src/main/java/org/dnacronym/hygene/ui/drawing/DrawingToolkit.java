package org.dnacronym.hygene.ui.drawing;

import javafx.scene.canvas.GraphicsContext;


/**
 * These toolkit's are used for drawing.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod") // This class doesn't need abstract methods
public abstract class DrawingToolkit {
    static final double ANNOTATION_HEIGHT = 4;
    static final double ANNOTATION_DASH_LENGTH = 10;

    private GraphicsContext graphicsContext;


    /**
     * Prevent direct instantiation of {@link DrawingToolkit}.
     */
    DrawingToolkit() {
        // This class shouldn't be instantiated directly
    }


    /**
     * Sets the {@link GraphicsContext} used for drawing by the toolkit.
     *
     * @param graphicsContext the {@link GraphicsContext} to use for drawing
     */
    public final void setGraphicsContext(final GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    /**
     * Returns the {@link GraphicsContext} used by this toolkit.
     *
     * @return the {@link GraphicsContext} used by this toolkit
     */
    protected final GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }
}
