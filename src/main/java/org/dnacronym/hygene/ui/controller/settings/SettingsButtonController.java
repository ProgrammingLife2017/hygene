package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
public class SettingsButtonController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(SettingsButtonController.class);

    private @MonotonicNonNull Settings settings;

    private @MonotonicNonNull Stage stage;

    @FXML
    private @MonotonicNonNull ButtonBar buttonBar;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            settings = Hygene.getInstance().getSettings();
        } catch (UIInitialisationException e) {
            LOGGER.error("Unable to initialize SettingsButtonController.", e);
            return;
        }

        if (buttonBar != null) {
            stage = (Stage) buttonBar.getScene().getWindow();
        }
    }

    /**
     * When the user clicks the "OK" button execute all actions and close the window.
     */
    @FXML
    void okAction() {
        settings.executeAll();
        if (stage != null) {
            stage.getScene().getWindow().hide();
        }
    }

    /**
     * When the user clicks "Cancel" clear all commands and close the window.
     */
    @FXML
    void cancelAction() {
        settings.clearAll();
        if (stage != null) {
            stage.hide();
        }
    }

    /**
     * Execute and clear all commands.
     */
    @FXML
    void applyAction() {
        settings.executeAll();
    }
}
