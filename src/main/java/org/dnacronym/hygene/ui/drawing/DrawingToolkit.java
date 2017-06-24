package org.dnacronym.hygene.ui.drawing;

import javafx.scene.canvas.GraphicsContext;


/**
 * These toolkits are used for drawing.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod") // This class doesn't need abstract methods
public abstract class DrawingToolkit {
    static final double ANNOTATION_DASH_LENGTH = 10;
    /**
     * Default dash value.
     * <p>
     * When passed to {@link GraphicsContext#setLineDashes(double...)}, it turns off dashing.
     */
    static final int ANNOTATION_DASH_DEFAULT = 0;

    private double annotationHeight = 3;
    private GraphicsContext graphicsContext;


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

    /**
     * Sets the height of annotation lanes.
     *
     * @param annotationHeight the height of annotation lanes
     */
    public final void setAnnotationHeight(final double annotationHeight) {
        this.annotationHeight = annotationHeight;
    }

    /**
     * Returns the height of annotation lanes.
     *
     * @return the height of annotation lanes
     */
    protected final double getAnnotationHeight() {
        return annotationHeight;
    }
}
