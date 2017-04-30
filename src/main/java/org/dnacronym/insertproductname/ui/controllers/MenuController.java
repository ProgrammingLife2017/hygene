package org.dnacronym.insertproductname.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the menu bar of the application. Each menu item has its own action.
 */
public class MenuController extends MainController implements Initializable {

    private static final String FILE_CHOOSER_TITLE = "Choose DNA Graph";

    /**
     * Construct a new main controller. Used by controllers that inherit from this to directly access the
     * stage.
     *
     * @param stage Primary stage of the application.
     */
    MenuController(final Stage stage) {
        super(stage);
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
    }

    /**
     * Open action. Opens a {@link javafx.stage.FileChooser}.
     *
     * @param event Event associated with the action.
     */
    @FXML
    protected final void openAction(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(FILE_CHOOSER_TITLE);
        fileChooser.showOpenDialog(getStage());
    }
}
