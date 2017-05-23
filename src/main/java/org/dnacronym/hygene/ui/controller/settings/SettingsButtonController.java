package org.dnacronym.hygene.ui.controller.settings;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

    @FXML
    private Button apply;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setSettings(Hygene.getInstance().getSettings());
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
     * When the user clicks the "OK" button execute all actions and close the window.
     *
     * @param actionEvent {@link ActionEvent} associated with this action
     */
    @FXML
    void okAction(final ActionEvent actionEvent) {
        settings.executeAll();

        final Node source = (Node) actionEvent.getSource();
        source.getScene().getWindow().hide();

        actionEvent.consume();
    }

    /**
     * When the user clicks "Cancel" clear all commands and close the window.
     *
     * @param actionEvent {@link ActionEvent} associated with this action
     */
    @FXML
    void cancelAction(final ActionEvent actionEvent) {
        settings.clearAll();

        final Node source = (Node) actionEvent.getSource();
        source.getScene().getWindow().hide();

        actionEvent.consume();
    }

    /**
     * Execute and clear all commands.
     *
     * @param actionEvent {@link ActionEvent} associated with this action
     */
    @FXML
    void applyAction(final ActionEvent actionEvent) {
        settings.executeAll();
        actionEvent.consume();
    }
}
