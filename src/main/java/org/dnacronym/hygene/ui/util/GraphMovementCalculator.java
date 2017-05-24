package org.dnacronym.hygene.ui.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;


/**
 * Deals with translating user input into something the {@link GraphVisualizer} can use and understand.
 */
public final class GraphMovementCalculator {
    private static final double DEFAULT_SENSITIVITY = 0.005;

    private final GraphVisualizer graphVisualizer;
    private final DoubleProperty sensitivityProperty;

    private double centerX;

    private double lastX;
    private boolean draggingRight;
    private boolean dragging;


    /**
     * Create instance of {@link GraphMovementCalculator}.
     *
     * @param graphVisualizer {@link GraphVisualizer} to drag
     */
    public GraphMovementCalculator(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
        this.sensitivityProperty = new SimpleDoubleProperty(DEFAULT_SENSITIVITY);
    }


    /**
     * Center the x to use as offset when dragging.
     * <p>
     * Sets the dragging variable to {@code false}. Only once the user continues to drag will it be set to true.
     *
     * @param x x as offset when dragging
     */
    public void onMousePressed(final double x) {
        centerX = x;
        dragging = false;
    }

    /**
     * The new x every time the mouse is dragged.
     * <p>
     * If the drag direction changes, and the user is currently dragging, it resets the drag by calling
     * {@link #onMousePressed(double)} again.
     * <p>
     * If not, the method checks if the user is dragging right or left, and makes sure that the dragging variable is set
     * to {@code true}. It also stores the current {@code x} position for the next time this method is called, which can
     * be used as reference to check whether the user is dragging their mouse.
     *
     * @param x new x of mouse when dragged
     */
    public void onMouseDragged(final double x) {
        final boolean startedDraggingOppositeDirection = lastX < x && draggingRight || lastX > x && !draggingRight;
        if (dragging && startedDraggingOppositeDirection) {
            onMousePressed(x);
        }

        draggingRight = lastX > x;
        dragging = true;
        lastX = x;

        final double currentCenterNodeId = graphVisualizer.getCenterNodeIdProperty().get();

        final double translation = centerX - x;
        final int newCenterNodeId = (int) (currentCenterNodeId + Math.round(sensitivityProperty.get() * translation));
        graphVisualizer.getCenterNodeIdProperty().set(newCenterNodeId);
    }

    /**
     * Property which determines the sensitivity of dragging.
     * <p>
     * A higher value results in a drag changing the center node id by a larger amount.
     *
     * @return property which determines the drag sensitivity
     */
    public DoubleProperty getSensitivityProperty() {
        return sensitivityProperty;
    }
}
