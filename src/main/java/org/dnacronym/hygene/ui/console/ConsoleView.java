package org.dnacronym.hygene.ui.console;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;


/**
 * Custom stage used for creating the console window in the GUI.
 */
public final class ConsoleView {
    private static final String TITLE = "Console";
    private static final String CONSOLE_VIEW = "/ui/console/console_view.fxml";

    private final Stage stage;


    /**
     * Constructor for {@link ConsoleView}.
     *
     * @param fxmlLoader an {@link FXMLLoader} instance for instantiating the new window
     * @throws IOException               if there was an error locating or reading the FXML
     * @throws UIInitialisationException if there was an error initializing the provided FXML resource
     */
    @Inject
    public ConsoleView(final FXMLLoader fxmlLoader) throws IOException, UIInitialisationException {
        stage = new Stage();

        stage.setTitle(TITLE);

        final URL resource = getClass().getResource(CONSOLE_VIEW);
        fxmlLoader.setLocation(resource);
        final Parent parent = fxmlLoader.load();
        final Scene rootScene = new Scene(parent);
        stage.setScene(rootScene);
    }


    /**
     * Gets the {@link Stage} in {@link ConsoleView}.
     *
     * @return the {@link Stage}
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Brings the console window back to the from of the screen.
     */
    public void bringToFront() {
        if (stage.isShowing()) {
            stage.toFront();
        } else {
            Platform.runLater(stage::show);
        }
    }
}
