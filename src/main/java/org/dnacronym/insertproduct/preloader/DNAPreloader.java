package org.dnacronym.insertproduct.preloader;

import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dnacronym.insertproduct.runnable.DNAApplication;

/**
 * Pre loader of {@link DNAApplication}.
 * <p>
 * View of preloader is located at {@value PRELOADER_VIEW}.
 */
public class DNAPreloader extends Preloader {

    private static final String PRELOADER_VIEW = "/view/dna_preloader_view.fxml";

    @FXML
    private ProgressBar progress;

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle(DNAApplication.TITLE);
        stage.initStyle(StageStyle.UNDECORATED);

        Parent root = FXMLLoader.load(getClass().getResource(PRELOADER_VIEW));

        progress = new ProgressBar();

        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Update {@link #progress} of the application.
     *
     * @param pn Progress notification, which contains a progress.
     */
    @Override
    public void handleProgressNotification(ProgressNotification pn) {
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
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }
}
