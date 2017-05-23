package org.dnacronym.hygene.ui.help;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller for the help menu.
 */
@SuppressWarnings("initialization")
public class HelpMenuController implements Initializable {
    @FXML
    private VBox leftMenu;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        Button btn1 = new Button();
        Button btn2 = new Button();
        Button btn3 = new Button();
        leftMenu.getChildren().addAll(btn1, btn2, btn3);
    }
}
