package org.dnacronym.hygene.ui.runnable;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.core.Files;
import org.dnacronym.hygene.ui.store.GraphStore;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;

/**
 * Main class of the application. Launches a {@link HygenePreloader}, and afterwards a {@link Hygene}.
 *
 * @see LauncherImpl#launchApplication(Class, Class, String[])
 */
public final class Hygene extends Application {
    static final String TITLE = "Hygene";
    private static final String APPLICATION_VIEW = "/ui/view/main_view.fxml";
    private static Logger logger = LogManager.getLogger(Hygene.class);
    private static @MonotonicNonNull Hygene hygene;

    private @MonotonicNonNull GraphStore graphStore;
    private @MonotonicNonNull GraphVisualizer graphVisualizer;

    private @MonotonicNonNull Stage primaryStage;

    /**
     * Get an instance of an {@link Hygene}.
     *
     * @return instance of {@link Hygene}.
     * @throws UIInitialisationException if the UI has not be initialized.
     */
    public static synchronized Hygene getInstance() throws UIInitialisationException {
        if (hygene == null) {
            throw new UIInitialisationException("Instance of Application not set.");
        }
        return hygene;
    }

    /**
     * Set the {@link Hygene} instance.
     *
     * @param application {@link Hygene} instance.
     */
    protected static synchronized void setInstance(final Hygene application) {
        hygene = application;
    }

    /**
     * Main method of application.
     * <p>
     * Uses {@link LauncherImpl} to launch a {@link HygenePreloader} before it launches the
     * {@link Hygene}.
     *
     * @param args arguments of application.
     * @see LauncherImpl
     */
    public static void main(final String[] args) {
        LauncherImpl.launchApplication(Hygene.class, HygenePreloader.class, args);
    }

    @Override
    public void init() {
        graphStore = new GraphStore();
        graphVisualizer = new GraphVisualizer();
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
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
     * Gets the {@link GraphStore} of the {@link Hygene}.
     *
     * @return {@link GraphStore} of the {@link Hygene}.
     * @throws UIInitialisationException if the the UI was not initialized, meaning the {@link GraphStore} was not set
     *                                   in {@link #init()}.
     * @see GraphStore
     * @see #init()
     */
    public GraphStore getGraphStore() throws UIInitialisationException {
        if (graphStore == null) {
            throw new UIInitialisationException("GraphStore not present.");
        }
        return graphStore;
    }

    /**
     * Gets the {@link GraphVisualizer} of the {@link Hygene}.
     *
     * @return {@link GraphVisualizer} of the {@link Hygene}.
     * @throws UIInitialisationException if the UI was not initialized, meaning the {@link GraphVisualizer} was not set
     *                                   in {@link #init()}.
     * @see #init()
     */
    public GraphVisualizer getGraphVisualizer() throws UIInitialisationException {
        if (graphVisualizer == null) {
            throw new UIInitialisationException("GraphVisualiser not present.");
        }
        return graphVisualizer;
    }

    /**
     * Get an instance of the Hygene.
     * <p>
     * If there is not an instance, then it will throw a {@link UIInitialisationException} as opposed to creating a
     * new one.
     *
     * @return instance of the {@link Hygene}.
     * @throws UIInitialisationException if the UI was not initialized, meaning the {@link Stage} was not set in {@link
     *                                   #start(Stage)}.
     * @see #start(Stage)
     */
    public Stage getPrimaryStage() throws UIInitialisationException {
        if (primaryStage == null) {
            throw new UIInitialisationException("Stage not present.");
        }
        return primaryStage;
    }
}
