package org.dnacronym.hygene.ui.runnable;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
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
    static final String TITLE = "Hygene";
    private static final String APPLICATION_VIEW = "/ui/view/main_view.fxml";
    private static Logger logger = org.apache.logging.log4j.LogManager.getLogger(DNAApplication.class);
    private static @MonotonicNonNull DNAApplication dnaApplication;

    private @MonotonicNonNull GraphStore graphStore;

    private @MonotonicNonNull Stage primaryStage;

    /**
     * Get an instance of an {@link DNAApplication}.
     *
     * @return instance of {@link DNAApplication}.
     * @throws UIInitialisationException if the UI has not be initialized.
     */
    public static synchronized DNAApplication getInstance() throws UIInitialisationException {
        if (dnaApplication == null) {
            throw new UIInitialisationException("Instance of Application not set.");
        }
        return dnaApplication;
    }

    /**
     * Set the {@link DNAApplication} instance.
     *
     * @param application {@link DNAApplication} instance.
     */
    protected static synchronized void setInstance(final DNAApplication application) {
        dnaApplication = application;
    }

    /**
     * Main method of application.
     * <p>
     * Uses {@link LauncherImpl} to launch a {@link DNAPreloader} before it launches the
     * {@link DNAApplication}.
     *
     * @param args arguments of application.
     * @see LauncherImpl
     */
    public static void main(final String[] args) {
        LauncherImpl.launchApplication(DNAApplication.class, DNAPreloader.class, args);
    }

    @Override
    public final void init() {
        graphStore = new GraphStore();
    }

    @Override
    public final void start(final Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        setInstance(this);

        primaryStage.setTitle(TITLE);
        primaryStage.setMaximized(true);

        final URL resource = Files.getInstance().getResourceUrl(APPLICATION_VIEW);
        final Parent parent = FXMLLoader.load(resource);
        if (parent == null) {
            throw new UIInitialisationException("Root of Application could not be found.");
        }

        final Scene rootScene = new Scene(parent);
        primaryStage.setScene(rootScene);
        primaryStage.show();

        logger.info("Launching Hygene GUI");
    }

    /**
     * Gets the {@link GraphStore} of the {@link DNAApplication}.
     *
     * @return {@link GraphStore} of the {@link DNAApplication}.
     * @throws UIInitialisationException if the the UI was not initialized, meaning the {@link GraphStore} was not set
     *                                   in {@link #init()}.
     * @see GraphStore
     * @see #init()
     */
    public final GraphStore getGraphStore() throws UIInitialisationException {
        if (graphStore == null) {
            throw new UIInitialisationException("GraphStore not present.");
        }
        return graphStore;
    }

    /**
     * Get an instance of the DNAApplication.
     * <p>
     * If there is not an instance, then it will throw a {@link UIInitialisationException} as opposed to creating a
     * new one.
     *
     * @return instance of the {@link DNAApplication}.
     * @throws UIInitialisationException if the UI was not initialized, meaning the {@link Stage} was not set in {@link
     *                                   #start(Stage)}.
     * @see #start(Stage)
     */
    public final Stage getPrimaryStage() throws UIInitialisationException {
        if (primaryStage == null) {
            throw new UIInitialisationException("Stage not present.");
        }
        return primaryStage;
    }
}
