package org.dnacronym.hygene.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.console.ConsoleWrapper;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.help.HelpMenuView;
import org.dnacronym.hygene.ui.progressbar.ProgressBarView;
import org.dnacronym.hygene.ui.recent.RecentDirectory;
import org.dnacronym.hygene.ui.recent.RecentFiles;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.settings.Settings;
import org.dnacronym.hygene.ui.settings.SettingsView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Controller for the menu bar of the application. Handles user interaction with the menu.
 */
public final class MenuController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(MenuController.class);

    private FileChooser fileChooser;
    private GraphStore graphStore;
    private Settings settings;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu recentFilesMenu;

    private ConsoleWrapper consoleWrapper;
    private HelpMenuView helpMenuView;

    private SettingsView settingsView;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        if (SystemUtils.IS_OS_MAC) {
            menuBar.useSystemMenuBarProperty().set(true);
        }

        try {
            setGraphStore(Hygene.getInstance().getGraphStore());
            setSettings(Hygene.getInstance().getSettings());

            populateRecentFilesMenu();
            initFileChooser();
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialize MenuController.", e);
        }
    }

    /**
     * Set the {@link Settings} for use by the controller.
     *
     * @param settings {@link Settings} for use by the controller
     */
    void setSettings(final Settings settings) {
        this.settings = settings;
    }

    /**
     * Opens a {@link FileChooser} and sets the parent {@link javafx.stage.Window} as
     * {@link Hygene#getPrimaryStage()#getOwner()}.
     *
     * @param event {@link ActionEvent} associated with the event
     * @throws IOException               if unable to open or parse the file
     * @throws UIInitialisationException if this method was called before {@link Hygene} was instantiated
     */
    @FXML
    void openFileAction(final ActionEvent event) throws IOException, UIInitialisationException {
        final Stage primaryStage = Hygene.getInstance().getPrimaryStage();

        fileChooser.setInitialDirectory(RecentDirectory.get());

        final File gfaFile = fileChooser.showOpenDialog(primaryStage.getScene().getWindow());
        if (gfaFile == null) {
            return;
        }

        if (gfaFile.getParentFile() != null) {
            RecentDirectory.store(gfaFile.getParentFile());
        }

        loadFile(gfaFile);
    }

    /**
     * Opens a blocking settings window.
     */
    @FXML
    void settingsAction() {
        settingsView = new SettingsView(settings);
        settingsView.show();
    }

    /**
     * Opens an independent stage showing the console window.
     *
     * @param event {@link ActionEvent} associated with the event
     * @throws IOException if unable to located the FXML resource
     */
    @FXML
    void openConsoleAction(final ActionEvent event) throws IOException {
        try {
            if (consoleWrapper == null) {
                consoleWrapper = new ConsoleWrapper();
                LOGGER.info("Launched GUI console window");
            }

            consoleWrapper.bringToFront();
        } catch (final UIInitialisationException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Opens an independent stage showing the help menu.
     *
     * @param actionEvent {@link ActionEvent} associated with the event
     * @throws IOException if unable to locate FXML resource
     */
    public void openHelpAction(final ActionEvent actionEvent) throws IOException {
        try {
            if (helpMenuView == null) {
                helpMenuView = new HelpMenuView();
                LOGGER.info("Launched GUI help menu");
            }

            helpMenuView.bringToFront();
        } catch (final UIInitialisationException e) {
            LOGGER.error(e);
        }
        actionEvent.consume();
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
     */
    void initFileChooser() {
        final String chooserTitle = "Open GFA File";
        final FileChooser.ExtensionFilter gfaFilter =
                new FileChooser.ExtensionFilter(
                        "GFA (*." + GraphStore.GFA_EXTENSION + ")",
                        "*." + GraphStore.GFA_EXTENSION);
        final FileChooser.ExtensionFilter noFilter = new FileChooser.ExtensionFilter("All files", "*.*");

        final FileChooser chooser = new FileChooser();
        chooser.setTitle(chooserTitle);
        chooser.getExtensionFilters().add(gfaFilter);
        chooser.getExtensionFilters().add(noFilter);

        setFileChooser(chooser);
    }

    /**
     * Populates the sub-menu of recent files with items representing those files.
     *
     * @throws UIInitialisationException if initialisation of the UI fails
     */
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
     * Returns the {@link FileChooser} used by the menu.
     *
     * @return the {@link FileChooser}
     */
    FileChooser getFileChooser() {
        return fileChooser;
    }

    /**
     * Sets the {@link FileChooser} used by the menu.
     *
     * @param fileChooser the {@link FileChooser}
     */
    void setFileChooser(final FileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }

    /**
     * Returns the {@link ConsoleWrapper} attached to this menu.
     *
     * @return the {@link ConsoleWrapper}
     */
    public ConsoleWrapper getConsoleWrapper() {
        return consoleWrapper;
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
     * Gets the {@link HelpMenuView}.
     *
     * @return the {@link HelpMenuView}
     */
    public HelpMenuView getHelpMenuView() {
        return helpMenuView;
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
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // Unique instance per iteration
    private void loadFile(final File file) throws IOException, UIInitialisationException {
        final ProgressBarView progressBarView = new ProgressBarView();
        progressBarView.show();

        progressBarView.monitorTask(progressUpdater -> {
            if (graphStore == null) {
                LOGGER.error("Failed to load: " + file.getName() + ".");
                return;
            }

            try {
                graphStore.load(file, progressUpdater);
            } catch (final IOException e) {
                LOGGER.error("Failed to load: " + file.getName() + ".", e);
            }
        });

        Hygene.getInstance().formatTitle(file.getPath());

        RecentFiles.add(file);

        // Update menu only in initialized state (not in test-cases)
        if (recentFilesMenu != null) {
            populateRecentFilesMenu();
        }
    }
}
