package org.dnacronym.hygene.ui.about;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;


/**
 * Simple view for creating new bookmarks.
 */
public class AboutView {
    private static final String ABOUT_VIEW = "/ui/about/about_view.fxml";

    private final Stage stage;


    /**
     * Creates an instance of a {@link AboutView}.
     *
     * @throws IOException if unable to load the controller
     */
    @Inject
    public AboutView(final FXMLLoader fxmlLoader) throws UIInitialisationException, IOException {
        stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);

        final URL resource = getClass().getResource(ABOUT_VIEW);
        fxmlLoader.setLocation(resource);
        final Scene rootScene = new Scene(fxmlLoader.load());

        rootScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.hide();
            }
        });

        stage.setScene(rootScene);

        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                stage.hide();
            }
        });
    }


    /**
     * Show the stage, which blocks the underlying view.
     *
     * @throws UIInitialisationException if the UI has not been initialized
     */
    public void show() throws UIInitialisationException {
        if (stage.getOwner() == null) {
            final Stage primaryStage = Hygene.getInstance().getPrimaryStage();

            stage.initOwner(primaryStage);
            stage.getIcons().add(primaryStage.getIcons().get(0));
        }

        stage.show();
    }
}
