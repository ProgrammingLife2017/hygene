package org.dnacronym.hygene.ui.controller.settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the basic settings view.
 */
public final class LoggingSettingsViewController extends AbstractSettingsController {
    private static final Logger LOGGER = LogManager.getLogger(LoggingSettingsViewController.class);
    private static final String[] LOG_LEVELS = {"ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF"};

    @FXML
    private ChoiceBox<String> choiceBox;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);
        choiceBox.getItems().addAll(LOG_LEVELS);
        choiceBox.setValue(LogManager.getRootLogger().getLevel().toString());
    }

    /**
     * When the user changes the log level and new {@link Runnable} command is added to
     * {@link org.dnacronym.hygene.ui.store.Settings}. Command run when the user applies the change in settings.
     *
     * @param event action event
     */
    @FXML
    public void onLogLevelChanged(final ActionEvent event) {
        getSettings().addRunnable(() -> {
            String logLevel = choiceBox.getSelectionModel().getSelectedItem();

            Logger logger = LogManager.getRootLogger();
            Configurator.setLevel(logger.getName(), Level.toLevel(logLevel));

            LOGGER.info("Log level was set to: " + Level.toLevel(logLevel));
        });
    }
}
