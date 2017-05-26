package org.dnacronym.hygene.ui.runnable;

import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dnacronym.hygene.core.Files;

import java.io.IOException;
import java.net.URL;


/**
 * Preloader of application.
 * <p>
 * The view is located at {@value PRELOADER_VIEW}.
 */
public final class HygenePreloader extends Preloader {
    private static final String PRELOADER_VIEW = "/ui/dna_preloader_view.fxml";

    private Stage stage;

    @FXML
    private ProgressBar progress;


    @Override
    public void start(final Stage primaryStage) throws IOException, UIInitialisationException {
        stage = primaryStage;
        primaryStage.setTitle(Hygene.TITLE);
        primaryStage.initStyle(StageStyle.UNDECORATED);

        progress = new ProgressBar();

        final URL resource = Files.getInstance().getResourceUrl(PRELOADER_VIEW);
        final Parent root = FXMLLoader.load(resource);
        if (root == null) {
            throw new UIInitialisationException("Root of Preloader could not be found.");
        }

        final Scene rootScene = new Scene(root);
        primaryStage.setScene(rootScene);
        primaryStage.show();
    }

    /**
     * Update {@link #progress} of the application.
     *
     * @param pn Progress notification, which contains a progress
     */
    @Override
    public void handleProgressNotification(final ProgressNotification pn) {
        progress.setProgress(pn.getProgress());
    }

    /**
     * Notify the preloader of the state of the application. If {@link StateChangeNotification#getType()} is
     * {@link StateChangeNotification.Type#BEFORE_START}, {@link #stage} of preloader is hidden.
     *
     * @param evt State change notification, notifying the preloader what it should do
     * @see Stage#hide
     */
    @Override
    public void handleStateChangeNotification(final StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START && stage != null) {
            stage.hide();
        }
    }
}
