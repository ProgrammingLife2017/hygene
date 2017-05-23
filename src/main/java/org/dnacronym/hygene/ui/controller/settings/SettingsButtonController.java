package org.dnacronym.hygene.ui.controller.settings;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private Settings settings;
    private Stage stage;

    @FXML
    private ButtonBar buttonBar;

    @FXML
    private Button apply;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setSettings(Hygene.getInstance().getSettings());
            setStage((Stage) buttonBar.getScene().getWindow());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize SettingsButtonController.", e);
            return;
        }

        apply.disableProperty().bind(Bindings.isEmpty(settings.getCommands()));
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
     * Set the {@link Stage} for use by the controller.
     *
     * @param stage {@link Stage} for use by the controller
     */
    void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * When the user clicks the "OK" button execute all actions and close the window.
     */
    @FXML
    void okAction() {
        settings.executeAll();
        stage.hide();
    }

    /**
     * When the user clicks "Cancel" clear all commands and close the window.
     */
    @FXML
    void cancelAction() {
        settings.clearAll();
        stage.hide();
    }

    /**
     * Execute and clear all commands.
     */
    @FXML
    void applyAction() {
        settings.executeAll();
    }
}
