package org.dnacronym.hygene.ui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * The controller of the mianvafasdfawefwe.
 */
public final class MainController implements Initializable {
    @FXML
    private ToggleButton toggleLeftPane;
    @FXML
    private ScrollPane leftPane;
    @FXML
    private ToggleButton toggleRightPane;
    @FXML
    private ScrollPane rightPane;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        leftPane.visibleProperty().bind(toggleLeftPane.selectedProperty());
        leftPane.managedProperty().bind(toggleLeftPane.selectedProperty());
        toggleLeftPane.textProperty().bind(Bindings.when(
                toggleLeftPane.selectedProperty()).then("<").otherwise(">"));

        rightPane.visibleProperty().bind(toggleRightPane.selectedProperty());
        rightPane.managedProperty().bind(toggleRightPane.selectedProperty());
        toggleRightPane.textProperty().bind(Bindings.when(
                toggleRightPane.selectedProperty()).then(">").otherwise("<"));
    }
}
