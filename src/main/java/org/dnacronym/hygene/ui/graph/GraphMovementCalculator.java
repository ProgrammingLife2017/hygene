package org.dnacronym.hygene.ui.graph;

import javax.inject.Inject;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


/**
 * Deals with translating user input into something the {@link GraphVisualizer} can use and understand.
 * <p>
 * The translated coordinates are passed to the {@link GraphDimensionsCalculator}, which updates the positions of
 * onscreen nodes drawn by the {@link GraphVisualizer}.
 *
 * @see GraphDimensionsCalculator
 */
public final class GraphMovementCalculator {
    private static final double DEFAULT_PANNING_SENSITIVITY = 0.005;
    private static final double DEFAULT_ZOOMING_SENSITIVITY = 10;
    private static final double RADIUS_ZOOMING_FACTOR = 20000.0;

    private final GraphDimensionsCalculator graphDimensionsCalculator;

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
     * Create instance of {@link GraphMovementCalculator}.
     * <p>
     * The given {@link GraphDimensionsCalculator} is used to calculate the amount of onscreen nodes, and determines how
     * far the user can zoom out.
     *
     * @param graphDimensionsCalculator the {@link GraphDimensionsCalculator} used to query
     */
    @Inject
    public GraphMovementCalculator(final GraphDimensionsCalculator graphDimensionsCalculator) {
        this.graphDimensionsCalculator = graphDimensionsCalculator;
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
    public void onMouseDragEntered(final double x) {
        centerX = x;
        dragging = false;
    }

    /**
     * The new x every time the mouse is dragged.
     * <p>
     * If the drag direction changes, and the user is currently dragging, it resets the drag by calling
     * {@link #onMouseDragEntered(double)} again.
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
            onMouseDragEntered(x);
        }

        draggingRight = lastX > x;
        dragging = true;
        lastX = x;

        final double currentCenterNodeId = graphDimensionsCalculator.getCenterNodeIdProperty().get();

        final double translation = centerX - x;
        final int newCenterNodeId = (int) (currentCenterNodeId
                + Math.round(panningSensitivityProperty.get() * translation));

        graphDimensionsCalculator.getCenterNodeIdProperty().set(newCenterNodeId);
        dragging = newCenterNodeId > 0 && newCenterNodeId < graphDimensionsCalculator.getNodeCountProperty().get();
    }

    /**
     * When the user scrolls.
     * <p>
     * A positive delta corresponds to zooming in, and a negative delta to to zooming out.
     *
     * @param deltaY delta in the y direction
     */
    public void onScroll(final double deltaY) {
        final int deltaRange = (int) Math.round(deltaY * getZoomingSensitivityProperty().get()
                * (graphDimensionsCalculator.getViewRadiusProperty().get() / RADIUS_ZOOMING_FACTOR));

        graphDimensionsCalculator.getViewRadiusProperty().set(
                graphDimensionsCalculator.getViewRadiusProperty().get() + deltaRange
        );
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
     * Property which determines the sensitivity of zooming.
     * <p>
     * A higher value results in zooming changing the hops by a larger amount.
     *
     * @return property which determines the zoom sensitivity
     */
    public DoubleProperty getZoomingSensitivityProperty() {
        return zoomingSensitivityProperty;
    }
}
