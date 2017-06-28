package org.dnacronym.hygene.ui.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.dnacronym.hygene.graph.layout.FafospLayerer;

import javax.inject.Inject;


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

    private static final int MIN_ZOOM_FACTOR = 4;
    private static final int MAX_ZOOM_FACTOR = 1000;

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

        final long currentViewPoint = graphDimensionsCalculator.getViewPointProperty().get();
        final double translation = (centerX - x) * 100;
        final long newViewPoint = currentViewPoint + Math.round(panningSensitivityProperty.get() * translation);

        graphDimensionsCalculator.getViewPointProperty().set(newViewPoint);
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
        final int newViewRadius = graphDimensionsCalculator.getViewRadiusProperty().get() + deltaRange;
        if (newViewRadius < (long) FafospLayerer.LAYER_WIDTH * MIN_ZOOM_FACTOR
                || newViewRadius > (long) FafospLayerer.LAYER_WIDTH * MAX_ZOOM_FACTOR) {
            return;
        }
        graphDimensionsCalculator.getViewRadiusProperty().set(newViewRadius);
        graphDimensionsCalculator.updateLastScrollTime();
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
