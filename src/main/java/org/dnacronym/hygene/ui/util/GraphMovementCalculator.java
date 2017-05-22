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


    /**
     * Create instance of {@link GraphMovementCalculator}.
     *
     * @param graphVisualizer {@link GraphVisualizer} to drag
     */
    public GraphMovementCalculator(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
        sensitivityProperty = new SimpleDoubleProperty(DEFAULT_SENSITIVITY);
    }


    /**
     * Center the x to use as offset when dragging.
     *
     * @param x x as offset when dragging
     */
    public void onMousePressed(final double x) {
        centerX = x;
    }

    /**
     * The new x every time the mouse is dragged.
     *
     * @param x new x of mouse when dragged
     */
    public void onMouseDragged(final double x) {
        final double currentCenterNodeId = graphVisualizer.getCenterNodeIdProperty().get();

        final double translation = x - centerX;
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
