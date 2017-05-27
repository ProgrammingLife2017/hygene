package org.dnacronym.hygene.ui.settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * Settings controller for the logger.
 */
public final class LoggingSettingsViewController extends AbstractSettingsController {
    private static final Logger LOGGER = LogManager.getLogger(LoggingSettingsViewController.class);
    private static final List<String> LOG_LEVELS =
            Arrays.stream(Level.values()).map(Level::name).collect(Collectors.toList());

    @FXML
    private ChoiceBox<String> choiceBox;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        choiceBox.getItems().addAll(LOG_LEVELS);
        choiceBox.setValue(LogManager.getRootLogger().getLevel().toString());
    }

    /**
     * When the user changes the log level and new {@link Runnable} command is added to
     * {@link Settings}. Command run when the user applies the change in setting.
     *
     * @param event action event
     */
    @FXML
    public void onLogLevelChanged(final ActionEvent event) {
        getSettings().addRunnable(() -> {
            final String logLevel = choiceBox.getSelectionModel().getSelectedItem();

            final Logger logger = LogManager.getRootLogger();
            Configurator.setLevel(logger.getName(), Level.toLevel(logLevel));

            LOGGER.info("Log level was set to: " + Level.toLevel(logLevel));
        });

        event.consume();
    }

    /**
     * Sets {@link LoggingSettingsViewController#choiceBox} to a new value.
     *
     * @param choiceBox the new value for {@link LoggingSettingsViewController#choiceBox}
     */
    void setChoiceBox(final ChoiceBox<String> choiceBox) {
        this.choiceBox = choiceBox;
    }

    /**
     * Gets a list of log levels.
     *
     * @return String[] the LogLevels
     */
    static List<String> getLogLevels() {
        return LOG_LEVELS;
    }
}
