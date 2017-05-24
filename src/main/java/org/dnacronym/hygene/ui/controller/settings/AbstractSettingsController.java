package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.Settings;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Abstract Settings Controller.
 */
public abstract class AbstractSettingsController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(AbstractSettingsController.class);

    private Settings settings;
    private GraphVisualizer graphVisualizer;

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension") // All subclasses are marked final
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setGraphVisualizer(Hygene.getInstance().getGraphVisualizer());
            setSettings(Hygene.getInstance().getSettings());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize " + getClass().getSimpleName() + ".", e);
            return;
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
     * @return the {@link GraphVisualizer} for use by the controller.
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
}
