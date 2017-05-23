package org.dnacronym.hygene.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.GraphStore;
import org.dnacronym.hygene.ui.store.SimpleBookmark;
import org.dnacronym.hygene.ui.store.SimpleBookmarkStore;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for keeping track of bookmarks.
 */
public final class BookmarksController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ConfigController.class);

    private SimpleBookmarkStore simpleBookmarkStore;
    private GraphStore graphStore;

    @FXML
    private ScrollPane bookmarksPane;
    @FXML
    private TableView<SimpleBookmark> bookmarksTable;
    @FXML
    private TableColumn<SimpleBookmark, String> baseColumn;
    @FXML
    private TableColumn<SimpleBookmark, String> descriptionColumn;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setSimpleBookmarkStore(Hygene.getInstance().getSimpleBookmarkStore());
            setGraphStore(Hygene.getInstance().getGraphStore());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize BookmarksController.", e);
            return;
        }

        baseColumn.setCellValueFactory(cell -> cell.getValue().getBaseProperty());
        descriptionColumn.setCellValueFactory(cell -> cell.getValue().getDescriptionProperty());

        bookmarksTable.setItems(simpleBookmarkStore.getBookmarks());

        bookmarksPane.managedProperty().bind(Bindings.isNotNull(graphStore.getGfaFileProperty()));
        bookmarksPane.visibleProperty().bind(Bindings.isNotNull(graphStore.getGfaFileProperty()));
    }

    /**
     * Set the {@link SimpleBookmarkStore} for use by the controller.
     *
     * @param simpleBookmarkStore {@link SimpleBookmarkStore} for use by the controller
     */
    void setSimpleBookmarkStore(final SimpleBookmarkStore simpleBookmarkStore) {
        this.simpleBookmarkStore = simpleBookmarkStore;
    }

    /**
     * Set the {@link GraphStore} for use by the controller.
     *
     * @param graphStore {@link GraphStore} for use by the controller
     */
    void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
    }
}
