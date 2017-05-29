package org.dnacronym.hygene.ui.bookmark;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for showing of {@link SimpleBookmark}s.
 */
public final class BookmarkTableController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(BookmarkTableController.class);

    private SimpleBookmarkStore simpleBookmarkStore;
    private GraphStore graphStore;

    @FXML
    private AnchorPane tableAnchor;
    @FXML
    private ScrollPane bookmarksPane;
    /**
     * Table which shows the bookmarks of the current graph in view. If a user double clicks on a row, the current
     * center node id in {@link org.dnacronym.hygene.ui.graph.GraphVisualizer} is updated to the one in the
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
    @FXML
    private Button hideButton;


    /**
     * Create new instance of a {@link BookmarkTableController}.
     */
    public BookmarkTableController() {
        try {
            setSimpleBookmarkStore(Hygene.getInstance().getSimpleBookmarkStore());
            setGraphStore(Hygene.getInstance().getGraphStore());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize BookmarkTableController.", e);
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

        tableAnchor.managedProperty().bind(simpleBookmarkStore.getTableVisibleProperty());
        tableAnchor.visibleProperty().bind(simpleBookmarkStore.getTableVisibleProperty());
        hideButton.textProperty().bind(Bindings.when(simpleBookmarkStore.getTableVisibleProperty())
                .then(">")
                .otherwise("<"));

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

    /**
     * Toggles the visibility of the the bookmarks table.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void toggleVisibilityAction(final ActionEvent actionEvent) {
        final boolean newValue = simpleBookmarkStore.getTableVisibleProperty().not().get();
        simpleBookmarkStore.getTableVisibleProperty().set(newValue);
        actionEvent.consume();
    }
}
