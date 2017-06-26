package org.dnacronym.hygene.ui.menu;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.dialogue.ErrorDialogue;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.progressbar.ProgressBarView;
import org.dnacronym.hygene.ui.recent.RecentDirectory;
import org.dnacronym.hygene.ui.recent.RecentFiles;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.settings.SettingsView;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Controller for the menu bar of the application. Handles user interaction with the menu.
 */
// TODO split up class
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyMethods"}) // See todo. This will be addressed soon.
public final class MenuController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(MenuController.class);
    private static final String FAILED_TO_LOAD_FILE = "Failed to load %s.";

    private FileChooser gfaFileChooser;
    private FileChooser gffFileChooser;

    @Inject
    private GraphStore graphStore;
    @Inject
    private SettingsView settingsView;

    @FXML
    private Menu recentFilesMenu;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            populateRecentFilesMenu();
            setGfaFileChooser(initFileChooser(GraphStore.GFA_FILE_NAME, GraphStore.GFA_FILE_EXTENSION));
            setGffFileChooser(initFileChooser(GraphStore.GFF_FILE_NAME, GraphStore.GFF_FILE_EXTENSION));
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialize MenuController.", e);
            new ErrorDialogue(e).show();
        }
    }

    /**
     * Opens a {@link FileChooser} for opening a GFA file and sets the parent {@link javafx.stage.Window} as
     * {@link Hygene#getPrimaryStage()#getOwner()}.
     *
     * @param event {@link ActionEvent} associated with the event
     * @throws IOException               if unable to open or parse the file
     * @throws UIInitialisationException if this method was called before {@link Hygene} was instantiated
     */
    @FXML
    void openGfaFileAction(final ActionEvent event) throws IOException, UIInitialisationException {
        final File gfaFile = openFileAction(gfaFileChooser, GraphStore.GFA_FILE_NAME);
        if (gfaFile == null) {
            return;
        }

        loadFile(gfaFile);
    }

    /**
     * Opens a {@link FileChooser} for opening a GFF file and sets the parent {@link javafx.stage.Window} as
     * {@link Hygene#getPrimaryStage()#getOwner()}.
     *
     * @param event {@link ActionEvent} associated with the event
     * @throws IOException               if unable to open or parse the file
     * @throws UIInitialisationException if this method was called before {@link Hygene} was instantiated
     */
    @FXML
    void openGffFileAction(final ActionEvent event) throws IOException, UIInitialisationException {
        final File gffFile = openFileAction(gffFileChooser, GraphStore.GFF_FILE_NAME);
        if (gffFile == null) {
            return;
        }

        final ProgressBarView progressBarView = new ProgressBarView();
        progressBarView.monitorTask(progressUpdater -> {
            if (graphStore == null) {
                LOGGER.error(String.format(FAILED_TO_LOAD_FILE, gffFile.getName()));
                return;
            }

            try {
                graphStore.loadGffFile(gffFile, progressUpdater);
            } catch (final IOException e) {
                LOGGER.error(String.format(FAILED_TO_LOAD_FILE, gffFile.getName()), e);
            }
        });
    }

    /**
     * Opens a {@link FileChooser} and sets the parent {@link javafx.stage.Window} as
     * {@link Hygene#getPrimaryStage()#getOwner()}.
     *
     * @param fileChooser the file chooser that needs to be shown to the user
     * @param type        a descriptive type name of the file that needs to be opened
     * @return a file object or {@code null} if no file was selected
     * @throws IOException               if unable to open or parse the file
     * @throws UIInitialisationException if this method was called before {@link Hygene} was instantiated
     */
    private File openFileAction(final FileChooser fileChooser, final String type)
            throws UIInitialisationException, IOException {
        final Stage primaryStage = Hygene.getInstance().getPrimaryStage();

        final File recentDirectory = RecentDirectory.get(type);
        if (recentDirectory.exists()) {
            fileChooser.setInitialDirectory(recentDirectory);
        }

        final File file = fileChooser.showOpenDialog(primaryStage.getScene().getWindow());
        if (file == null) {
            return null;
        }

        if (file.getParentFile() != null) {
            RecentDirectory.store(type, file.getParentFile());
        }

        return file;
    }

    /**
     * Opens a blocking settings window.
     */
    @FXML
    void settingsAction() {
        settingsView.show();
    }

    /**
     * Quits the application.
     * <p>
     * JavaFX is requested to run the {@link Platform#exit()} method. The launcher thread will then shutdown.
     * If there are no other non-daemon threads that are running.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void exitAction(final ActionEvent actionEvent) {
        Platform.runLater(Platform::exit);
    }

    /**
     * Initializes the file chooser dialog.
     *
     * @param extensionName a descriptive name for the extension
     * @param extension     the desired file extension
     * @return an initialized file chooser with filters set for the given file extension
     */
    FileChooser initFileChooser(final String extensionName, final String extension) {
        final String chooserTitle = "Open " + extensionName + " File";
        final FileChooser.ExtensionFilter gfaFilter =
                new FileChooser.ExtensionFilter(
                        extensionName + " (*." + extension + ")",
                        "*." + extension);
        final FileChooser.ExtensionFilter noFilter = new FileChooser.ExtensionFilter("All files", "*.*");

        final FileChooser chooser = new FileChooser();
        chooser.setTitle(chooserTitle);
        chooser.getExtensionFilters().add(gfaFilter);
        chooser.getExtensionFilters().add(noFilter);

        return chooser;
    }

    /**
     * Populates the sub-menu of recent files with items representing those files.
     *
     * @throws UIInitialisationException if initialisation of the UI fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    // Unique instance per iteration
    void populateRecentFilesMenu() throws UIInitialisationException {
        final List<File> recentFiles;
        try {
            recentFiles = RecentFiles.getAll();
        } catch (final IOException e) {
            throw new UIInitialisationException("Error while reading recent files from data file.", e);
        }

        if (recentFiles.isEmpty() || recentFilesMenu.getItems() == null) {
            return;
        }

        // Remove default information item (telling the user that there are no recent files)
        recentFilesMenu.getItems().clear();

        for (final File file : recentFiles) {
            final MenuItem menuItem = new MenuItem(file.getPath());
            recentFilesMenu.getItems().add(menuItem);

            menuItem.addEventHandler(ActionEvent.ACTION, event -> {
                try {
                    loadFile(file);
                } catch (final IOException | UIInitialisationException e) {
                    LOGGER.error("Failed to load the selected recent file.", e);
                }
            });
        }
    }

    /**
     * Sets the {@link GraphStore} in the controller.
     *
     * @param graphStore the {@link GraphStore}
     */
    void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
    }

    /**
     * Sets the {@link FileChooser} used by the menu.
     *
     * @param gfaFileChooser the {@link FileChooser}
     */
    void setGfaFileChooser(final FileChooser gfaFileChooser) {
        this.gfaFileChooser = gfaFileChooser;
    }

    /**
     * Sets the {@link FileChooser} for GFF files used by the menu.
     *
     * @param gffFileChooser the {@link FileChooser}
     */
    void setGffFileChooser(final FileChooser gffFileChooser) {
        this.gffFileChooser = gffFileChooser;
    }

    /**
     * Gets settings view.
     *
     * @return the {@link SettingsView}
     */
    public SettingsView getSettingsView() {
        return settingsView;
    }

    /**
     * Sets the recentFilesMenu.
     *
     * @param recentFilesMenu the menu to be set
     */
    void setRecentFilesMenu(final Menu recentFilesMenu) {
        this.recentFilesMenu = recentFilesMenu;
    }

    /**
     * Loads the given file and updates recent files history accordingly.
     *
     * @param file the file to be opened
     * @throws IOException               if an IO error occurrs during loading or saving
     * @throws UIInitialisationException if initialisation of the UI fails
     */
    private void loadFile(final File file) throws IOException, UIInitialisationException {
        if (!file.exists()) {
            new ErrorDialogue("The file has been moved or deleted and could not be opened.").show();
            return;
        }

        final ProgressBarView progressBarView = new ProgressBarView();

        progressBarView.monitorTask(progressUpdater -> {
            if (graphStore == null) {
                LOGGER.error(String.format(FAILED_TO_LOAD_FILE, file.getName()));
                return;
            }

            try {
                graphStore.loadGfaFile(file, progressUpdater);
            } catch (final IOException e) {
                LOGGER.error(String.format(FAILED_TO_LOAD_FILE, file.getName()), e);
            }
        });

        progressBarView.show();

        Hygene.getInstance().formatTitle(file.getPath());

        RecentFiles.add(file);

        // Update menu only in initialized state (not in test-cases)
        if (recentFilesMenu != null) {
            populateRecentFilesMenu();
        }
    }
}
