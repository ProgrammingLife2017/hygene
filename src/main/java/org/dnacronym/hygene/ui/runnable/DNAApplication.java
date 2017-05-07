package org.dnacronym.hygene.ui.runnable;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.core.Files;
import org.dnacronym.hygene.ui.store.GraphStore;

import java.net.URL;

/**
 * Main class of the application. Launches a {@link DNAPreloader}, and afterwards a {@link DNAApplication}.
 *
 * @see LauncherImpl#launchApplication(Class, Class, String[])
 */
public class DNAApplication extends Application {
    @MonotonicNonNull
    private static DNAApplication dnaApplication;

    protected static final String TITLE = "DNA";
    protected static final String APPLICATION_VIEW = "/ui/view/main_view.fxml";

    protected static final String UI_NOT_INITIALIZED = "The UI could not be initialised.";

    private final GraphStore graphStore = new GraphStore();

    @MonotonicNonNull
    private Stage primaryStage;


    @Override
    public final void start(final Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        setInstance(this);

        primaryStage.setTitle(TITLE);
        primaryStage.setMaximized(true);

        final URL resource = Files.getInstance().getResourceUrl(APPLICATION_VIEW);
        final Parent parent = FXMLLoader.load(resource);
        if (parent == null) {
            throw new UIInitialisationException(UI_NOT_INITIALIZED);
        }

        final Scene rootScene = new Scene(parent);
        primaryStage.setScene(rootScene);
        primaryStage.show();
    }


    /**
     * Get the {@link GraphStore} of the {@link DNAApplication}.
     *
     * @return {@link GraphStore} of the {@link DNAApplication}.
     * @see GraphStore
     */
    public final GraphStore getGraphStore() {
        return graphStore;
    }

    /**
     * Get an instance of the DNAApplication. If there is not an instance, then it will throw a
     * {@link UIInitialisationException} as opposed to creating a new one.
     *
     * @return Instance of the {@link DNAApplication}.
     * @throws UIInitialisationException if the UI has not been initialized.
     */
    public final Stage getPrimaryStage() throws UIInitialisationException {
        if (this.primaryStage == null) {
            throw new UIInitialisationException(UI_NOT_INITIALIZED);
        }
        return this.primaryStage;
    }

    /**
     * Get an instance of an {@link DNAApplication}.
     *
     * @return Instance of {@link DNAApplication}.
     * @throws UIInitialisationException if the UI has not be initialized.
     */
    public static synchronized DNAApplication getInstance() throws UIInitialisationException {
        if (dnaApplication == null) {
            throw new UIInitialisationException(UI_NOT_INITIALIZED);
        }
        return dnaApplication;
    }

    /**
     * Set the {@link DNAApplication} instance.
     *
     * @param application {@link DNAApplication} instance.
     * @throws UIInitialisationException If instance is already set.
     */
    protected static synchronized void setInstance(final DNAApplication application) throws UIInitialisationException {
        dnaApplication = application;
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
}
