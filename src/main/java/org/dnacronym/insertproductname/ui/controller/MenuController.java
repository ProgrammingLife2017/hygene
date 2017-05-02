package org.dnacronym.insertproductname.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the menu bar of the application. Handles user interaction with the menu.
 */
public class MenuController implements Initializable {
    private static final String FILE_CHOOSER_TITLE = "Open GFA File";

    private static final FileChooser.ExtensionFilter GFA_EXTENSION = new FileChooser.ExtensionFilter("GFA", ".gfa");

    @FXML
    private MenuItem fileOpen;

    @Override
    public final void initialize(final URL location, final ResourceBundle resources) {
        // TODO implement MenuController initialize method.
    }

    @FXML
    protected final void openAction(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(FILE_CHOOSER_TITLE);
        fileChooser.getExtensionFilters().add(GFA_EXTENSION);

        final File gfaFile = fileChooser.showOpenDialog(fileOpen.getParentPopup().getOwnerWindow());
    }
}
