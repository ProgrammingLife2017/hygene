package org.dnacronym.hygene.ui.controller.bookmarks;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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
 * Controller for showing of {@link SimpleBookmark}s.
 */
public final class BookmarksTableController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(BookmarksTableController.class);

    private SimpleBookmarkStore simpleBookmarkStore;
    private GraphStore graphStore;

    @FXML
    private ScrollPane bookmarksPane;
    /**
     * Table which shows the bookmarks of the current graph in view. If a user double clicks on a row, the current
     * center node id in {@link org.dnacronym.hygene.ui.visualizer.GraphVisualizer} is updated to the one in the
     * bookmark.
     */
    @FXML
    private TableView<SimpleBookmark> bookmarksTable;
    @FXML
    private TableColumn<SimpleBookmark, Number> nodeId;
    @FXML
    private TableColumn<SimpleBookmark, Number> baseOffset;
    @FXML
    private TableColumn<SimpleBookmark, Number> radius;
    @FXML
    private TableColumn<SimpleBookmark, String> description;


    /**
     * Create new instance of a {@link BookmarksTableController}.
     */
    public BookmarksTableController() {
        try {
            setSimpleBookmarkStore(Hygene.getInstance().getSimpleBookmarkStore());
            setGraphStore(Hygene.getInstance().getGraphStore());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize BookmarksTableController.", e);
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        nodeId.setCellValueFactory(cell -> cell.getValue().getNodeIdProperty());
        baseOffset.setCellValueFactory(cell -> cell.getValue().getBaseOffsetProperty());
        description.setCellValueFactory(cell -> cell.getValue().getDescriptionProperty());
        radius.setCellValueFactory(cell -> cell.getValue().getRadiusProperty());

        bookmarksTable.setRowFactory(tableView -> {
            final TableRow<SimpleBookmark> simpleBookmarkTableRow = new TableRow<>();
            simpleBookmarkTableRow.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !simpleBookmarkTableRow.isEmpty()) {
                    simpleBookmarkTableRow.getItem().getOnClick().run();
                }
            });
            return simpleBookmarkTableRow;
        });

        bookmarksTable.setItems(simpleBookmarkStore.getSimpleBookmarks());

        bookmarksPane.managedProperty().bind(Bindings.isNotNull(graphStore.getGfaFileProperty()));
        bookmarksPane.visibleProperty().bind(Bindings.isNotNull(graphStore.getGfaFileProperty()));
    }

    /**
     * Sets the {@link SimpleBookmarkStore} for use by the controller.
     *
     * @param simpleBookmarkStore {@link SimpleBookmarkStore} for use by the controller
     */
    void setSimpleBookmarkStore(final SimpleBookmarkStore simpleBookmarkStore) {
        this.simpleBookmarkStore = simpleBookmarkStore;
    }

    /**
     * Sets the {@link GraphStore} for use by the controller.
     *
     * @param graphStore {@link GraphStore} for use by the controller
     */
    void setGraphStore(final GraphStore graphStore) {
        this.graphStore = graphStore;
    }
}
