package org.dnacronym.hygene.ui.menu;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.bookmark.SimpleBookmarkStore;
import org.dnacronym.hygene.ui.console.ConsoleView;
import org.dnacronym.hygene.ui.help.HelpMenuView;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the tools menu.
 */
public final class ToolsMenuController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ToolsMenuController.class);

    private SimpleBookmarkStore simpleBookmarkStore;
    private SequenceVisualizer sequenceVisualizer;

    private ConsoleView consoleView;
    private HelpMenuView helpMenuView;

    @FXML
    private MenuItem toggleBookmarkTable;


    /**
     * Creates an instance of {@link ToolsMenuController}.
     */
    public ToolsMenuController() {
        try {
            setSimpleBookmarkStore(Hygene.getInstance().getSimpleBookmarkStore());
            setSequenceVisualizer(Hygene.getInstance().getSequenceVisualizer());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize " + getClass().getSimpleName() + ".", e);
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        toggleBookmarkTable.textProperty().bind(Bindings.when(simpleBookmarkStore.getTableVisibleProperty())
                .then("Hide bookmarks")
                .otherwise("Show bookmarks"));
    }

    /**
     * Sets the {@link SimpleBookmarkStore} for use by the controller.
     *
     * @param simpleBookmarkStore the {@link SimpleBookmarkStore} for use by the controller
     */
    void setSimpleBookmarkStore(final SimpleBookmarkStore simpleBookmarkStore) {
        this.simpleBookmarkStore = simpleBookmarkStore;
    }

    /**
     * Sets the {@link SequenceVisualizer} for use by the controller.
     *
     * @param sequenceVisualizer the {@link SequenceVisualizer} for use by the controller
     */
    void setSequenceVisualizer(final SequenceVisualizer sequenceVisualizer) {
        this.sequenceVisualizer = sequenceVisualizer;
    }

    /**
     * Opens an independent stage showing the console window.
     *
     * @param actionEvent {@link ActionEvent} associated with the event
     * @throws IOException if unable to located the FXML resource
     */
    @FXML
    void openConsoleAction(final ActionEvent actionEvent) throws IOException {
        try {
            if (consoleView == null) {
                consoleView = new ConsoleView();
                LOGGER.info("Launched GUI console window");
            }

            consoleView.bringToFront();
        } catch (final UIInitialisationException e) {
            LOGGER.error(e);
        }

        actionEvent.consume();
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
     * When the user wants to toggle the visibility of the BookmarksTable.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void toggleBookmarksTableAction(final ActionEvent actionEvent) {
        simpleBookmarkStore.getTableVisibleProperty().set(!simpleBookmarkStore.getTableVisibleProperty().get());
        actionEvent.consume();
    }

    /**
     * When the user wants to toggle the visibility of the node properties pane.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void toggleNodePropertiesAction(final ActionEvent actionEvent) {
        actionEvent.consume();
    }

    /**
     * When the user wants to toggle the visibility of the bookmark create pane.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void toggleBookmarkCreateAction(final ActionEvent actionEvent) {
        actionEvent.consume();
    }

    /**
     * When the user wants to toggle the visibility of the sequence visualizer pane.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void toggleSequenceVisualizerAction(final ActionEvent actionEvent) {
        sequenceVisualizer.getVisibleProperty().set(!sequenceVisualizer.getVisibleProperty().get());
        actionEvent.consume();
    }

    /**
     * Returns the {@link ConsoleView} attached to this menu.
     *
     * @return the {@link ConsoleView}
     */
    public ConsoleView getConsoleView() {
        return consoleView;
    }

    /**
     * Gets the {@link HelpMenuView}.
     *
     * @return the {@link HelpMenuView}
     */
    public HelpMenuView getHelpMenuView() {
        return helpMenuView;
    }
}
