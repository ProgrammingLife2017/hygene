package org.dnacronym.insertproduct.ui.runnable;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class of the application. Launches a {@link DNAPreloader}, and afterwards a {@link DNAApplication}.
 *
 * @see LauncherImpl#launchApplication(Class, Class, String[])
 */
public class DNAApplication extends Application {

    public static final String TITLE = "DNA";

    private static final String APPLICATION_VIEW = "/ui/view/main_view.fxml";

    public static void main(String[] args) {
        LauncherImpl.launchApplication(DNAApplication.class, DNAPreloader.class, args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle(TITLE);
        primaryStage.setMaximized(true);

        final Parent root = FXMLLoader.load(getClass().getResource(APPLICATION_VIEW));
        final Scene rootScene = new Scene(root);

        primaryStage.setScene(rootScene);
        primaryStage.show();
    }
}
