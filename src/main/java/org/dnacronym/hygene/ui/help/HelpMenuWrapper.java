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

/**
 * Custom stage used for creating the console window in the GUI.
 */
public class HelpMenuWrapper {
    private static final String TITLE = "Help";
    private static final String HELP_MENU_VIEW = "/ui/view/help_view.fxml";

    private Stage stage;


    /**
     * Constructor for {@link HelpMenuWrapper}.
     *
     * @throws IOException               if there was an error locating or reading the FXML
     * @throws UIInitialisationException if there was an error initializing the provided FXML resource
     */
    public HelpMenuWrapper() throws IOException, UIInitialisationException {
        stage = new Stage();

        stage.setTitle(TITLE);

        final URL resource = Files.getInstance().getResourceUrl(HELP_MENU_VIEW);
        final Parent parent = FXMLLoader.load(resource);

        if (parent == null) {
            throw new UIInitialisationException("Could not initialize the provided FXML resource.");
        }

        final Scene rootScene = new Scene(parent);
        stage.setScene(rootScene);

        loadData();

        Platform.runLater(stage::show);
    }

    /**
     * Gets the {@link Stage} in {@link HelpMenuWrapper}.
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
     * Load the data
     */
    public final void loadData() {
        HelpArticleLoader.loadHelpData();
    }
}
