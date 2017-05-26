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
import org.dnacronym.hygene.ui.bookmark.SimpleBookmarkStore;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphMovementCalculator;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.setting.Settings;

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

    static final String TITLE = "Hygene";
    private static final String APPLICATION_VIEW = "/ui/main_view.fxml";
    private static final String APPLICATION_ICON = "/icons/hygene_logo_small.png";

    private GraphStore graphStore;
    private SimpleBookmarkStore simpleBookmarkStore;

    private GraphVisualizer graphVisualizer;
    private GraphMovementCalculator graphMovementCalculator;
    private GraphDimensionsCalculator graphDimensionsCalculator;

    private Settings settings;

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

        graphDimensionsCalculator = new GraphDimensionsCalculator();
        graphVisualizer = new GraphVisualizer(graphStore, graphDimensionsCalculator);
        graphMovementCalculator = new GraphMovementCalculator(graphVisualizer);

        simpleBookmarkStore = new SimpleBookmarkStore(graphStore, graphVisualizer);
        settings = new Settings(graphStore);
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
     */
    public void formatTitle(final String filePath) {
        primaryStage.setTitle(TITLE + " - [" + filePath + "]");
    }

    /**
     * Gets the {@link GraphStore} of the application.
     *
     * @return {@link GraphStore} of the application
     */
    public GraphStore getGraphStore() {
        return graphStore;
    }

    /**
     * Gets the {@link GraphVisualizer} of the application.
     *
     * @return {@link GraphVisualizer} of the application
     */
    public GraphVisualizer getGraphVisualizer() {
        return graphVisualizer;
    }

    /**
     * Gets the {@link Settings} of the application.
     *
     * @return {@link Settings} of the application
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Gets the {@link GraphMovementCalculator} of the application.
     *
     * @return {@link GraphMovementCalculator} of the {application
     */
    public GraphMovementCalculator getGraphMovementCalculator() {
        return graphMovementCalculator;
    }

    /**
     * Gets the {@link GraphDimensionsCalculator} of the application.
     *
     * @return {@link GraphDimensionsCalculator} of the application
     */
    public GraphDimensionsCalculator getGraphDimensionsCalculator() {
        return graphDimensionsCalculator;
    }

    /**
     * Gets the {@link SimpleBookmarkStore} of the application.
     *
     * @return {@link SimpleBookmarkStore} of the application
     */
    public SimpleBookmarkStore getSimpleBookmarkStore() {
        return simpleBookmarkStore;
    }

    /**
     * Gets the primary {@link Stage} of the application.
     *
     * @return primary {@link Stage} of the application
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
