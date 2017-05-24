package org.dnacronym.hygene.ui.controller.settings;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.dnacronym.hygene.ui.store.Settings;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the basic settings view.
 */
public final class LoggingSettingsViewController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(LoggingSettingsViewController.class);

    private Settings settings;
    private GraphVisualizer graphVisualizer;

    @FXML
    private ChoiceBox choiceBox;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        choiceBox.getItems().addAll("ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF");
        choiceBox.setValue(LogManager.getRootLogger().getLevel().toString());

        choiceBox.getSelectionModel().selectedIndexProperty()
                .addListener((ov, oldVal, newVal) -> {
                    Logger logger = LogManager.getRootLogger();
                    Configurator.setLevel(logger.getName(), Level.DEBUG);
                });
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
}
