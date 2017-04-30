package org.dnacronym.insertproduct.ui.runnable;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Pre loader of {@link Application}.
 * <p>
 * View of preloader is located at {@value PRELOADER_VIEW}.
 */
public class Preloader extends javafx.application.Preloader {

    private static final String PRELOADER_VIEW = "/ui/view/dna_preloader_view.fxml";

    @FXML
    private ProgressBar progress;

    private Stage stage;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle(Application.TITLE);
        stage.initStyle(StageStyle.UNDECORATED);

        progress = new ProgressBar();

        final Parent root = FXMLLoader.load(getClass().getResource(PRELOADER_VIEW));
        final Scene rootScene = new Scene(root);

        stage.setScene(rootScene);
        stage.show();
    }

    /**
     * Update {@link #progress} of the application.
     *
     * @param pn Progress notification, which contains a progress.
     */
    @Override
    public void handleProgressNotification(final ProgressNotification pn) {
        progress.setProgress(pn.getProgress());
    }

    /**
     * Notify the pre loader of the state of the application. If {@link StateChangeNotification#getType()} is
     * {@link StateChangeNotification.Type#BEFORE_START}, {@link #stage} of pre loader is hidden.
     *
     * @param evt State change notification, notifying the pre loader what it should do.
     * @see Stage#hide
     */
    @Override
    public void handleStateChangeNotification(final StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }
}
