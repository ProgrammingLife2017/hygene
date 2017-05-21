package org.dnacronym.hygene.ui.controller.settings;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.Settings;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the three buttons of the settings window.
 */
public final class SettingsButtonController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(SettingsButtonController.class);

    private @MonotonicNonNull Settings settings;

    @FXML
    private @MonotonicNonNull ButtonBar buttonBar;

    @FXML
    private @MonotonicNonNull Button apply;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            settings = Hygene.getInstance().getSettings();
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize SettingsButtonController.", e);
            return;
        }

        if (apply != null) {
            apply.disableProperty().bind(Bindings.isEmpty(settings.getCommands()));
        }
    }

    /**
     * When the user clicks the "OK" button execute all actions and close the window.
     */
    @FXML
    void okAction() {
        if (settings != null) {
            settings.executeAll();
        }

        if (buttonBar != null) {
            final Stage stage = (Stage) buttonBar.getScene().getWindow();
            if (stage != null) {
                stage.hide();
            }
        }
    }

    /**
     * When the user clicks "Cancel" clear all commands and close the window.
     */
    @FXML
    void cancelAction() {
        if (settings != null) {
            settings.clearAll();
        }

        if (buttonBar != null) {
            final Stage stage = (Stage) buttonBar.getScene().getWindow();
            if (stage != null) {
                stage.hide();
            }
        }
    }

    /**
     * Execute and clear all commands.
     */
    @FXML
    void applyAction() {
        if (settings != null) {
            settings.executeAll();
        }
    }
}
