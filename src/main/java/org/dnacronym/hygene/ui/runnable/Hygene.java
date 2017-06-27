package org.dnacronym.hygene.ui.runnable;

import com.gluonhq.ignite.guice.GuiceContext;
import javax.inject.Inject;

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
import org.dnacronym.hygene.ui.bookmark.BookmarkStore;
import org.dnacronym.hygene.ui.graph.GraphStore;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;


/**
 * Main class of the application. Launches a {@link HygenePreloader}, and afterwards a {@link Hygene}.
 *
 * @see LauncherImpl#launchApplication(Class, Class, String[])
 */
public final class Hygene extends Application {
    private static final Logger LOGGER = LogManager.getLogger(Hygene.class);
    private static Hygene hygeneApplication;

    private final GuiceContext context = new GuiceContext(this, () -> Arrays.asList(new GuiceModule()));

    @Inject
    private FXMLLoader fxmlLoader;

    static final String TITLE = "Hygene";
    private static final String APPLICATION_VIEW = "/ui/main_view.fxml";
    private static final String APPLICATION_ICON = "/icons/hygene_logo_small.png";

    @Inject
    private GraphStore graphStore;
    @Inject
    private BookmarkStore bookmarkStore;

    private Stage primaryStage;

    /**
     * Get an instance of an {@link Hygene}.
     *
     * @return instance of {@link Hygene}
     * @throws UIInitialisationException if the UI has not be initialized
     */
    public static synchronized Hygene getInstance() throws UIInitialisationException {
        if (hygeneApplication == null) {
            throw new UIInitialisationException("Instance of Application not set.");
        }
        return hygeneApplication;
    }

    /**
     * Set the {@link Hygene} instance.
     *
     * @param application {@link Hygene} instance
     */
    protected static synchronized void setInstance(final Hygene application) {
        hygeneApplication = application;
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
    public void start(final Stage primaryStage) throws IOException, UIInitialisationException, SQLException {
        context.init();

        this.primaryStage = primaryStage;
        setInstance(this);

        final URL resource = getClass().getResource(APPLICATION_VIEW);
        fxmlLoader.setLocation(resource);
        final Parent parent = fxmlLoader.load();

        primaryStage.setTitle(TITLE);
        primaryStage.setMaximized(true);


        primaryStage.setOnCloseRequest(e -> {
            bookmarkStore.writeBookmarksToFile();
            Platform.exit();
        });

        final Scene rootScene = new Scene(parent);
        primaryStage.setScene(rootScene);

        final Image hygeneIcon = new Image(String.valueOf(getClass().getResource(APPLICATION_ICON)));
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
                    graphStore.loadGfaFile(new File(fileName),
                            (progress, message) -> LOGGER.info(progress + "% - " + message));
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
     * Gets the primary {@link Stage} of the application.
     *
     * @return primary {@link Stage} of the application
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Returns the Guice context.
     *
     * @return the Guice context
     */
    public GuiceContext getContext() {
        return context;
    }
}
