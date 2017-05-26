package org.dnacronym.hygene.ui.runnable;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.core.Files;
import org.dnacronym.hygene.ui.store.GraphStore;
import org.dnacronym.hygene.ui.store.Settings;
import org.dnacronym.hygene.ui.store.SimpleBookmarkStore;
import org.dnacronym.hygene.ui.util.GraphMovementCalculator;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;


/**
 * Main class of the application. Launches a {@link HygenePreloader}, and afterwards a {@link Hygene}.
 *
 * @see LauncherImpl#launchApplication(Class, Class, String[])
 */
public final class Hygene extends Application {
    private static final Logger LOGGER = LogManager.getLogger(Hygene.class);
    private static Hygene hygene;

    private static final String APPLICATION_VIEW = "/ui/view/main_view.fxml";
    private static final String APPLICATION_ICON = "/ui/icons/hygene_logo_small.png";

    static final String TITLE = "Hygene";

    private GraphStore graphStore;
    private GraphVisualizer graphVisualizer;
    private Settings settings;
    private GraphMovementCalculator graphMovementCalculator;
    private SimpleBookmarkStore simpleBookmarkStore;

    private Stage primaryStage;

    /**
     * Get an instance of an {@link Hygene}.
     *
     * @return instance of {@link Hygene}
     * @throws UIInitialisationException if the UI has not be initialized
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
     * @param application {@link Hygene} instance
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
     * @param args arguments of application
     * @see LauncherImpl
     */
    public static void main(final String[] args) {
        LauncherImpl.launchApplication(Hygene.class, HygenePreloader.class, args);
    }

    @Override
    public void init() throws IOException, SQLException {
        graphStore = new GraphStore();
        graphVisualizer = new GraphVisualizer(graphStore);
        settings = new Settings(graphStore);
        simpleBookmarkStore = new SimpleBookmarkStore(graphStore, graphVisualizer);
        graphMovementCalculator = new GraphMovementCalculator(graphVisualizer);
    }

    @Override
    public void start(final Stage primaryStage) throws IOException, UIInitialisationException {
        this.primaryStage = primaryStage;
        setInstance(this);

        primaryStage.setTitle(TITLE);
        primaryStage.setMaximized(true);

        final URL resource = Files.getInstance().getResourceUrl(APPLICATION_VIEW);
        final Parent parent = FXMLLoader.load(resource);

        primaryStage.setOnCloseRequest(e -> {
            simpleBookmarkStore.writeBookmarksToFile();
            Platform.exit();
        });

        final Scene rootScene = new Scene(parent);
        primaryStage.setScene(rootScene);

        final Image hygeneIcon = new Image(String.valueOf(Files.getInstance().getResourceUrl(APPLICATION_ICON)));
        primaryStage.getIcons().add(hygeneIcon);

        primaryStage.show();

        parseArguments();

        LOGGER.info("Launching Hygene GUI");
    }

    /**
     * Parse the command line arguments.
     *
     * @throws UIInitialisationException when GUI was not you initialized
     * @throws IOException               when file could not be opened
     */
    private void parseArguments() throws UIInitialisationException {
        if (getParameters() != null && getParameters().getNamed() != null) {
            final Map<String, String> parameters = getParameters().getNamed();

            final String fileName = parameters.get("file");
            if (fileName != null) {
                try {
                    getGraphStore().load(new File(fileName), progress -> LOGGER.info(progress + "%"));
                } catch (final IOException e) {
                    LOGGER.error(String.format("File %s could not be found.", fileName), e);
                }
            }
        }
    }

    /**
     * Format the title of the application to include the information given.
     * <p>
     * The title is formatted as {@value TITLE} - [filePath].
     *
     * @param filePath filepath to set the in the title of the application
     * @throws UIInitialisationException if the UI was not initialized, meaning the {@link Stage} was not set in {@link
     *                                   #start(Stage)}
     */
    public void formatTitle(final String filePath) throws UIInitialisationException {
        primaryStage.setTitle(TITLE + " - [" + filePath + "]");
    }

    /**
     * Gets the {@link GraphStore} of the {@link Hygene}.
     *
     * @return {@link GraphStore} of the {@link Hygene}
     * @throws UIInitialisationException if the the UI was not initialized, meaning the {@link GraphStore} was not set
     *                                   in {@link #init()}
     * @see GraphStore
     * @see #init()
     */
    public GraphStore getGraphStore() throws UIInitialisationException {
        return graphStore;
    }

    /**
     * Gets the {@link GraphVisualizer} of the {@link Hygene}.
     *
     * @return {@link GraphVisualizer} of the {@link Hygene}
     * @throws UIInitialisationException if the UI was not initialized, meaning the {@link GraphVisualizer} was not set
     *                                   in {@link #init()}
     * @see GraphVisualizer
     * @see #init()
     */
    public GraphVisualizer getGraphVisualizer() throws UIInitialisationException {
        return graphVisualizer;
    }

    /**
     * Gets the {@link Settings} of the {@link Hygene}.
     *
     * @return {@link Settings} of the {@link Hygene}
     * @throws UIInitialisationException if the UI was not initialized, meaning the {@link Settings} was not set
     *                                   in {@link #init()}
     */
    public Settings getSettings() throws UIInitialisationException {
        return settings;
    }

    /**
     * Gets the {@link SimpleBookmarkStore} of {@link Hygene}.
     *
     * @return {@link SimpleBookmarkStore} of the {@link Hygene}
     * @see SimpleBookmarkStore
     * @see #init()
     */
    public SimpleBookmarkStore getSimpleBookmarkStore() {
        return simpleBookmarkStore;
    }

    /**
     * Gets the {@link GraphMovementCalculator} of the {@link Hygene}.
     *
     * @return {@link GraphMovementCalculator} of the {@link Hygene}
     */
    public GraphMovementCalculator getGraphMovementCalculator() {
        return graphMovementCalculator;
    }

    /**
     * Get an instance of the Hygene.
     * <p>
     * If there is not an instance, then it will throw a {@link UIInitialisationException} as opposed to creating a
     * new one.
     *
     * @return instance of the {@link Hygene}
     * @throws UIInitialisationException if the UI was not initialized, meaning the {@link Stage} was not set in {@link
     *                                   #start(Stage)}
     * @see #start(Stage)
     */
    public Stage getPrimaryStage() throws UIInitialisationException {
        return primaryStage;
    }
}
