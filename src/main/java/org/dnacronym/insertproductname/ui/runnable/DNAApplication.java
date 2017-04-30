package org.dnacronym.insertproductname.ui.runnable;

import com.sun.javafx.application.LauncherImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.dnacronym.insertproductname.ui.controllers.MainController;


/**
 * Main class of the application. Launches a {@link DNAPreloader}, and afterwards a {@link DNAApplication}.
 *
 * @see LauncherImpl#launchApplication(Class, Class, String[])
 */
public class DNAApplication extends javafx.application.Application {

    public static final String TITLE = "DNA";

    private static final String APPLICATION_VIEW = "/ui/view/main_view.fxml";

    /**
     * Main method of application. Uses {@link LauncherImpl} to launch a {@link DNAPreloader} before launches the
     * {@link DNAApplication}.
     *
     * @param args Arguments of application.
     * @see LauncherImpl
     */
    public static void main(final String[] args) {
        LauncherImpl.launchApplication(DNAApplication.class, DNAPreloader.class, args);
    }

    @Override
    public final void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle(TITLE);
        primaryStage.setMaximized(true);

        final MainController mainController = new MainController(primaryStage);

        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(APPLICATION_VIEW));
        final Parent parent = fxmlLoader.load();
        fxmlLoader.setController(mainController);

        final Scene rootScene = new Scene(parent);
        primaryStage.setScene(rootScene);
        primaryStage.show();
    }
}
