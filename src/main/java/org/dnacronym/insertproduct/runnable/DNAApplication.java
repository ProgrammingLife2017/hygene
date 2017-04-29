package org.dnacronym.insertproduct.runnable;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.dnacronym.insertproduct.preloader.DNAPreloader;

public class DNAApplication extends Application {

    public static final String TITLE = "DNA";

    private static final String APPLICATION_VIEW = "/view/main_view.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(APPLICATION_VIEW));

        primaryStage.setTitle(TITLE);
        primaryStage.setMaximized(true);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(DNAApplication.class, DNAPreloader.class, args);
    }
}
