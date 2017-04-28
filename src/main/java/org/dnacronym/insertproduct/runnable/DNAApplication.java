package org.dnacronym.insertproduct.runnable;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import org.dnacronym.insertproduct.preloader.DNAPreloader;

public class DNAApplication extends Application {

    private static final String TITLE = "DNA";

    private static final String APPLICATION_VIEW = "/view/main_view.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(DNAApplication.class, DNAPreloader.class, args);
    }
}
