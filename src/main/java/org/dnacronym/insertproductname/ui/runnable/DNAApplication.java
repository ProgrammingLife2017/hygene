package org.dnacronym.insertproductname.ui.runnable;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.dnacronym.insertproductname.core.Files;
import org.dnacronym.insertproductname.ui.UIInitialisationException;


/**
 * Main class of the application. Launches a {@link DNAPreloader}, and afterwards a {@link DNAApplication}.
 *
 * @see LauncherImpl#launchApplication(Class, Class, String[])
 */
public class DNAApplication extends Application {

    public static final String TITLE = "DNA";

    private static final String APPLICATION_VIEW = "/ui/view/main_view.fxml";

    /**
     * Main method of application. Uses {@link LauncherImpl} to launch a {@link DNAPreloader} before it launches the
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

        final Parent parent = FXMLLoader.load(Files.getInstance().getResourceUrl(APPLICATION_VIEW));

        if (parent == null) {
            throw new UIInitialisationException("The UI could not be initialised.");
        }

        final Scene rootScene = new Scene(parent);
        primaryStage.setScene(rootScene);
        primaryStage.show();
    }
}
