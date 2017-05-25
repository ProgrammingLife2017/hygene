package org.dnacronym.hygene.ui.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;


/**
 * Deals with translating user input into something the {@link GraphVisualizer} can use and understand.
 */
public final class GraphMovementCalculator {
    private static final double DEFAULT_PANNING_SENSITIVITY = 0.005;
    private static final double DEFAULT_ZOOMING_SENSITIVITY = 0.05;

    private final GraphVisualizer graphVisualizer;

    private final DoubleProperty panningSensitivityProperty;
    private final DoubleProperty zoomingSensitivityProperty;

    /**
     * {@code x} position where the panning started.
     */
    private double centerX;
    private double lastX;
    private boolean draggingRight;
    private boolean dragging;

    /**
     *
     */
    private boolean stopScrollingOut;


    /**
     * Create instance of {@link GraphMovementCalculator}.
     * <p>
     * The given {@link GraphDimensionsCalculator} is used to calculate the amount of onscreen nodes, and determines how
     * far the user can zoom out.
     *
     * @param graphVisualizer {@link GraphVisualizer} to drag
     */
    public GraphMovementCalculator(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
        this.panningSensitivityProperty = new SimpleDoubleProperty(DEFAULT_PANNING_SENSITIVITY);
        this.zoomingSensitivityProperty = new SimpleDoubleProperty(DEFAULT_ZOOMING_SENSITIVITY);
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
     * be used as reference to check whether the user is dragging their mouse. If the user drags out of bounds, the
     * lowest/highest possible value is set, and dragging is set to {@code false}.
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
        final int newCenterNodeId = (int) (currentCenterNodeId
                + Math.round(panningSensitivityProperty.get() * translation));

        graphVisualizer.getCenterNodeIdProperty().set(Math.min(
                Math.max(newCenterNodeId, 0),
                graphVisualizer.getNodeCountProperty().get() - 1));

        dragging = newCenterNodeId < 0 || newCenterNodeId > graphVisualizer.getNodeCountProperty().get();

    }

    /**
     * When the user scrolls.
     * <p>
     * Scrolling up corresponds to zooming in, and scrolling down corresponds to zooming out. The user can zoom in no
     * more than 1 hops. Scrolling out is ignored when the diameter of the onscreen graph is equal to the diameter
     * of the actual graph.
     *
     * @param deltaY delta in the y direction
     */
    public void onScroll(final double deltaY) {
        if (stopScrollingOut && deltaY > 0) {
            return;
        }

        final int oldHops = graphVisualizer.getHopsProperty().get();
        final int newHops = (int) Math.round(oldHops + deltaY * getZoomingSensitivityProperty().get());

        graphVisualizer.getHopsProperty().set(Math.max(newHops, 1));

        stopScrollingOut = graphVisualizer.getOnScreenNodeCountProperty().get()
                > graphVisualizer.getNodeCountProperty().get();
    }

    /**
     * Property which determines the sensitivity of dragging.
     * <p>
     * A higher value results in a drag changing the center node id by a larger amount.
     *
     * @return property which determines the drag sensitivity
     */
    public DoubleProperty getPanningSensitivityProperty() {
        return panningSensitivityProperty;
    }

    /**
     * Property which determines the sensitivty of zooming.
     * <p>
     * A higher value results in zooming changing the hops by a larger amount.
     *
     * @return property which determines the zoom sensitivity
     */
    public DoubleProperty getZoomingSensitivityProperty() {
        return zoomingSensitivityProperty;
    }
}
