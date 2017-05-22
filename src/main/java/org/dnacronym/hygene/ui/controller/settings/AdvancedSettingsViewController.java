package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.Settings;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the border.
 */
public final class AdvancedSettingsViewController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(AdvancedSettingsViewController.class);

    private @MonotonicNonNull GraphVisualizer graphVisualizer;
    private @MonotonicNonNull Settings settings;

    @FXML
    private @MonotonicNonNull CheckBox displayLaneBorders;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setGraphVisualizer(Hygene.getInstance().getGraphVisualizer());
            setSettings(Hygene.getInstance().getSettings());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize AdvancedSettingsViewController.", e);
            return;
        }

        if (displayLaneBorders != null && graphVisualizer != null) {
            displayLaneBorders.setSelected(graphVisualizer.getDisplayBordersProperty().get());
        }
    }

    /**
     * Set the {@link GraphVisualizer} for use by the controller.
     *
     * @param graphVisualizer {@link GraphVisualizer} for use by the controller
     */
    void setGraphVisualizer(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
    }

    /**
     * Set the {@link Settings} for use by the controller.
     *
     * @param settings {@link Settings} for use by the controller
     */
    void setSettings(final Settings settings) {
        this.settings = settings;
    }

    /**
     * When user clicks on show lane borders {@link CheckBox}.
     */
    @FXML
    void showLaneBordersClicked() {
        if (settings != null) {
            settings.addCallable(() -> {
                if (displayLaneBorders != null && graphVisualizer != null) {
                    final boolean newValue = displayLaneBorders.isSelected();
                    graphVisualizer.getDisplayBordersProperty().setValue(newValue);
                }
            });
        }
    }
}
