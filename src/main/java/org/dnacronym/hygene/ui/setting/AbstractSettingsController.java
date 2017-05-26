package org.dnacronym.hygene.ui.setting;

import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.graph.GraphMovementCalculator;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;


/**
 * Abstract Settings Controller.
 */
public abstract class AbstractSettingsController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(AbstractSettingsController.class);

    private Settings settings;
    private GraphVisualizer graphVisualizer;
    private GraphMovementCalculator graphMovementCalculator;


    /**
     * Initializes a new instance of {@link AbstractSettingsController}.
     */
    AbstractSettingsController() {
        try {
            setGraphVisualizer(Hygene.getInstance().getGraphVisualizer());
            setSettings(Hygene.getInstance().getSettings());
            setGraphMovementCalculator(Hygene.getInstance().getGraphMovementCalculator());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize " + getClass().getSimpleName() + ".", e);
        }
    }


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
    public final GraphMovementCalculator getGraphMovementCalculator() {
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
}
