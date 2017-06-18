package org.dnacronym.hygene.ui.settings;

import javafx.fxml.Initializable;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphMovementCalculator;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;

import javax.inject.Inject;


/**
 * Abstract Settings Controller.
 */
public abstract class SettingsController implements Initializable {
    @Inject
    private Settings settings;
    @Inject
    private GraphVisualizer graphVisualizer;
    @Inject
    private GraphMovementCalculator graphMovementCalculator;
    @Inject
    private GraphDimensionsCalculator graphDimensionsCalculator;


    /**
     * Gets the {@link Settings} for use by the controller.
     *
     * @return the {@link Settings} for use by the controller
     */
    public final Settings getSettings() {
        return settings;
    }

    /**
     * Set the {@link Settings} for use by the controller.
     *
     * @param settings {@link Settings} for use by the controller
     */
    final void setSettings(final Settings settings) {
        this.settings = settings;
    }

    /**
     * Gets the {@link GraphVisualizer} for use by the controller.
     *
     * @return the {@link GraphVisualizer} for use by the controller
     */
    public final GraphVisualizer getGraphVisualizer() {
        return graphVisualizer;
    }

    /**
     * Set the {@link GraphVisualizer} for use by the controller.
     *
     * @param graphVisualizer {@link GraphVisualizer} for use by the controller
     */
    final void setGraphVisualizer(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
    }

    /**
     * Gets the {@link GraphMovementCalculator} for use by the controller.
     *
     * @return the {@link GraphMovementCalculator} for use by the controller
     */
    final GraphMovementCalculator getGraphMovementCalculator() {
        return graphMovementCalculator;
    }

    /**
     * Sets the {@link GraphMovementCalculator} for use by the controller.
     *
     * @param graphMovementCalculator {@link GraphMovementCalculator} for use by the controller
     */
    final void setGraphMovementCalculator(final GraphMovementCalculator graphMovementCalculator) {
        this.graphMovementCalculator = graphMovementCalculator;
    }

    /**
     * Sets the {@link GraphDimensionsCalculator} for use by the controller.
     *
     * @param graphDimensionsCalculator the {@link GraphDimensionsCalculator} for use by the controller
     */
    final void setGraphDimensionsCalculator(final GraphDimensionsCalculator graphDimensionsCalculator) {
        this.graphDimensionsCalculator = graphDimensionsCalculator;
    }

    /**
     * Gets the {@link GraphDimensionsCalculator} for use by the controller.
     *
     * @return the {@link GraphDimensionsCalculator} for use by the controller
     */
    final GraphDimensionsCalculator getGraphDimensionsCalculator() {
        return graphDimensionsCalculator;
    }
}
