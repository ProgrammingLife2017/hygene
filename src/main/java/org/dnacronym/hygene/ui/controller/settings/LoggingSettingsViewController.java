package org.dnacronym.hygene.ui.controller.settings;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
public final class LoggingSettingsViewController extends AbstractSettingsController implements Initializable {
    protected static final Logger LOGGER = LogManager.getLogger(LoggingSettingsViewController.class);
    final String[] LOG_LEVELS = {"ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF"};

    @FXML
    private ChoiceBox<String> choiceBox;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        choiceBox.getItems().addAll(LOG_LEVELS);
        choiceBox.setValue(LogManager.getRootLogger().getLevel().toString());
    }

    /**
     * When the user changes the log level.
     *
     * @param inputMethodEvent
     */
    @FXML
    public void onLogLevelChanged(Event inputMethodEvent) {
        settings.addRunnable(() -> {
            Logger logger = LogManager.getRootLogger();
            Configurator.setLevel(logger.getName(), Level.DEBUG);

            String logLevel = choiceBox.getSelectionModel().getSelectedItem();
            System.out.println(("Log level was set to: " + Level.toLevel(logLevel)));

        });
        System.out.println("CHANGED LOG");
    }
}
