package org.dnacronym.insertproductname.ui.runnable;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dnacronym.insertproductname.ui.store.GraphStore;

import java.net.URL;

/**
 * Main class of the application. Launches a {@link DNAPreloader}, and afterwards a {@link DNAApplication}.
 *
 * @see LauncherImpl#launchApplication(Class, Class, String[])
 */
public class DNAApplication extends Application {

    public static final String TITLE = "DNA";

    public static final String APPLICATION_VIEW = "/ui/view/main_view.fxml";

    private static final GraphStore GRAPH_STORE = new GraphStore();

    @MonotonicNonNull
    private static Stage applicationStage;

    /**
     * Get the {@link GraphStore} of the {@link DNAApplication}.
     *
     * @return {@link GraphStore} of the {@link DNAApplication}.
     * @see GraphStore
     */
    public static GraphStore getGraphStore() {
        return GRAPH_STORE;
    }

    /**
     * Get the {@link Stage} of the application.
     *
     * @return {@link Stage} of the application.
     * @throws UIInitialisationException if the UI is not initialized.
     */
    @NonNull
    public static Stage getStage() throws UIInitialisationException {
        if (DNAApplication.applicationStage == null) {
            throw new UIInitialisationException("The UI could not be initialised.");
        }
        return DNAApplication.applicationStage;
    }

    /**
     * Set the {@link Stage} of the {@link Application}.
     *
     * @param stage {@link Stage} of the application.
     */
    protected static void setStage(final Stage stage) {
        applicationStage = stage;
    }

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
        DNAApplication.setStage(primaryStage);

        primaryStage.setTitle(TITLE);
        primaryStage.setMaximized(true);

        final URL resource = getClass().getResource(APPLICATION_VIEW);
        final Parent parent = FXMLLoader.load(resource);
        if (parent == null) {
            throw new UIInitialisationException("The UI could not be initialised.");
        }

        final Scene rootScene = new Scene(parent);
        primaryStage.setScene(rootScene);
        primaryStage.show();
    }
}
