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

    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        progress.setProgress(pn.getProgress());
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }
}
