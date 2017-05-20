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
    private @MonotonicNonNull CheckBox showLaneBorders;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            graphVisualizer = Hygene.getInstance().getGraphVisualizer();
            settings = Hygene.getInstance().getSettings();
        } catch (UIInitialisationException e) {
            LOGGER.error("Unable to initialize AdvancedSettingsViewController.", e);
        }
    }

    /**
     * When user clicks on show lane borders {@link CheckBox}.
     */
    @FXML
    void showLaneBordersClicked() {
        if (settings != null) {
            settings.addCallable(() -> {
                if (showLaneBorders != null && graphVisualizer != null) {
                    final boolean newValue = showLaneBorders.isSelected();
                    graphVisualizer.getDisplayBordersProperty().setValue(newValue);
                }
            });
        }
    }
}
