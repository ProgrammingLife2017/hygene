package org.dnacronym.hygene.ui.help;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.dnacronym.hygene.core.Files;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * The controller for the help menu.
 */
public class HelpMenuController implements Initializable {
    private final static String LEFT_MENU_BTN = "/ui/view/help/help_sidebar_btn.fxml";

    @FXML
    private VBox sidebar;

    @FXML
    private TextArea contentArea;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        generateLeftMenu();
    }

    public void generateLeftMenu() {
        List<Button> leftMenuBTN = HelpMenuWrapper.getHelpMenuArticles().stream().map(art -> {
            Button btn = generateLeftMenuBTN(art);
            return btn;
        }).collect(Collectors.toList());


        sidebar.getChildren().addAll(leftMenuBTN);
    }

    public Button generateLeftMenuBTN(HelpArticle article) {
        Button btn = new Button();
        try {
            final URL resource = Files.getInstance().getResourceUrl(LEFT_MENU_BTN);
            btn = FXMLLoader.load(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        btn.setText(article.getTitle());
        btn.setOnMouseClicked(event -> {
            System.out.println("clicked");
            contentArea.setText(article.getContent());
        });

        return btn;
    }
}
