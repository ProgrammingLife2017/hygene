package org.dnacronym.insertproductname.ui.runnable;

import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * Preloader of {@link DNAApplication}.
 * <p>
 * The view is located at {@value PRELOADER_VIEW}.
 */
public class DNAPreloader extends Preloader {

    private static final String PRELOADER_VIEW = "/ui/view/dna_preloader_view.fxml";

    @FXML
    private ProgressBar progress;

    private Stage stage;

    @Override
    public final void start(final Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle(DNAApplication.TITLE);
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
    public final void handleProgressNotification(final ProgressNotification pn) {
        progress.setProgress(pn.getProgress());
    }

    /**
     * Notify the preloader of the state of the application. If {@link StateChangeNotification#getType()} is
     * {@link StateChangeNotification.Type#BEFORE_START}, {@link #stage} of preloader is hidden.
     *
     * @param evt State change notification, notifying the preloader what it should do.
     * @see Stage#hide
     */
    @Override
    public final void handleStateChangeNotification(final StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }
}
