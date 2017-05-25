package org.dnacronym.hygene.ui.help;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.dnacronym.hygene.core.Files;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.io.IOException;
import java.net.URL;
import java.util.List;


/**
 * Custom stage used for creating the console window in the GUI.
 */
public class HelpMenuView {
    private static final String TITLE = "Help";
    private static final String HELP_MENU_VIEW = "/ui/view/help/help_view.fxml";
    private static final List<HelpArticle> HELP_MENU_ARTICLES = (new HelpArticleParser()).parse();

    private Stage stage;


    /**
     * Constructor for {@link HelpMenuView}.
     *
     * @throws IOException               if there was an error locating or reading the FXML
     * @throws UIInitialisationException if there was an error initializing the provided FXML resource
     */
    public HelpMenuView() throws IOException, UIInitialisationException {
        stage = new Stage();

        stage.setTitle(TITLE);

        final URL resource = Files.getInstance().getResourceUrl(HELP_MENU_VIEW);
        final Parent parent = FXMLLoader.load(resource);

        if (parent == null) {
            throw new UIInitialisationException("Could not initialize the provided FXML resource.");
        }

        final Scene rootScene = new Scene(parent);
        stage.setScene(rootScene);

        Platform.runLater(stage::show);
    }


    /**
     * Gets the {@link Stage} in {@link HelpMenuView}.
     *
     * @return the {@link Stage}
     */
    public final Stage getStage() {
        return stage;
    }

    /**
     * Brings the console window back to the from of the screen.
     */
    public final void bringToFront() {
        if (stage.isShowing()) {
            stage.toFront();
        } else {
            Platform.runLater(stage::show);
        }
    }

    /**
     * Gets help menu articles.
     *
     * @return the help menu articles
     */
    public static List<HelpArticle> getHelpMenuArticles() {
        return HELP_MENU_ARTICLES;
    }
}
