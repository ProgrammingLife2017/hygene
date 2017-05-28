package org.dnacronym.hygene.ui.help;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.core.Files;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * The controller for the help menu.
 */
public final class HelpMenuController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(HelpMenuController.class);
    private static final String LEFT_MENU_BTN = "/ui/help/help_sidebar_btn.fxml";

    @FXML
    private VBox sidebar;

    @FXML
    private TextArea contentArea;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        generateSidebarMenu();
    }

    /**
     * Generate sidebar menu.
     */
    void generateSidebarMenu() {
        List<Button> leftMenuButton = HelpMenuView.getHelpMenuArticles().stream()
                .map(this::generateSidebarButton).collect(Collectors.toList());

        sidebar.getChildren().addAll(leftMenuButton);
    }

    /**
     * Generate a new sidebar button.
     *
     * @param article the article
     * @return the button
     */
    Button generateSidebarButton(final HelpArticle article) {
        Button button;
        try {
            final URL resource = Files.getInstance().getResourceUrl(LEFT_MENU_BTN);
            button = FXMLLoader.load(resource);
        } catch (final IOException e) {
            button = new Button();
            LOGGER.error(e);
        }

        button.setText(article.getTitle());
        button.setOnMouseClicked(event -> contentArea.setText(article.getContent()));

        return button;
    }
}
