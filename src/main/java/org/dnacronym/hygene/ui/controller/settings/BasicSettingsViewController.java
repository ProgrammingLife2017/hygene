package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
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
 * Controller for the basic settings view.
 */
public final class BasicSettingsViewController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(BasicSettingsViewController.class);

    private @MonotonicNonNull Settings settings;
    private @MonotonicNonNull GraphVisualizer graphVisualizer;

    @FXML
    private @MonotonicNonNull Slider nodeHeight;
    @FXML
    private @MonotonicNonNull ColorPicker edgeColors;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            settings = Hygene.getInstance().getSettings();
            graphVisualizer = Hygene.getInstance().getGraphVisualizer();
        } catch (UIInitialisationException e) {
            LOGGER.error("Unable to initialize BasicSettingsViewController.", e);
            return;
        }

        if (nodeHeight != null && edgeColors != null) {
            nodeHeight.setValue(graphVisualizer.getNodeHeightProperty().get());
            edgeColors.setValue(graphVisualizer.getEdgeColorProperty().get());
        }
    }

    /**
     * When user finishes sliding the node height {@link Slider}.
     */
    @FXML
    void nodeHeightSliderDone() {
        if (settings != null) {
            settings.addCallable(() -> {
                if (nodeHeight != null && graphVisualizer != null) {
                    final double newValue = nodeHeight.getValue();
                    graphVisualizer.getNodeHeightProperty().setValue(newValue);
                }
            });
        }
    }

    /**
     * When the user finishes picking the color for edges in the {@link ColorPicker}.
     */
    @FXML
    void edgeColorDone() {
        if (settings != null) {
            settings.addCallable(() -> {
                if (edgeColors != null && graphVisualizer != null) {
                    final Color newValue = edgeColors.getValue();
                    graphVisualizer.getEdgeColorProperty().setValue(newValue);
                }
            });
        }
    }
}
