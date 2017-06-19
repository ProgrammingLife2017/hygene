package org.dnacronym.hygene.ui.settings;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the three buttons of the settings window.
 */
public final class SettingsButtonController implements Initializable {
    @Inject
    private Settings settings;

    @FXML
    private Button apply;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        apply.disableProperty().bind(Bindings.isEmpty(settings.getCommands()));
    }

    /**
     * When the user clicks "OK" execute all actions and close the window.
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
     * When the user clicks "Apply" execute all commands.
     *
     * @param actionEvent {@link ActionEvent} associated with this action
     */
    @FXML
    void applyAction(final ActionEvent actionEvent) {
        settings.executeAll();
        actionEvent.consume();
    }
}
