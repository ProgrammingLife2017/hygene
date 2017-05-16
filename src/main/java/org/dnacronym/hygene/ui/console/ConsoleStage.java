package org.dnacronym.hygene.ui.console;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.core.Files;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.io.IOException;
import java.net.URL;

/**
 * Custom stage used for creating the console window in the GUI.
 */
public class ConsoleStage {
    private static final Logger LOGGER = LogManager.getLogger(ConsoleStage.class);

    private static final String TITLE = "Console";
    private static final String CONSOLE_VIEW = "/ui/view/console_view.fxml";
    private static final int CONSOLE_WINDOW_WIDTH = 780;
    private static final int CONSOLE_WINDTH_HEIGHT = 600;

    private Stage stage;


    /**
     * Constructor for {@link ConsoleStage}.
     *
     * @throws IOException               if there was an error locating or reading the FXML.
     * @throws UIInitialisationException if there was an error initializing the provided FXML resource.
     */
    public ConsoleStage() throws IOException, UIInitialisationException {
        stage = new Stage();

        stage.setTitle(TITLE);

        final URL resource = Files.getInstance().getResourceUrl(CONSOLE_VIEW);
        final Parent parent = FXMLLoader.load(resource);

        if (parent == null) {
            throw new UIInitialisationException("Could not initialize the provided FXML resource.");
        }

        final Scene rootScene = new Scene(parent, CONSOLE_WINDOW_WIDTH, CONSOLE_WINDTH_HEIGHT);
        stage.setScene(rootScene);

        Platform.runLater(stage::show);
    }
}
