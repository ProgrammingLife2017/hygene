package org.dnacronym.hygene.ui.bookmark;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.dialogue.ErrorDialogue;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for showing of {@link SimpleBookmark}s.
 */
public final class BookmarkTableController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(BookmarkTableController.class);
    private static final int DESCRIPTION_TEXT_PADDING = 10;

    private SimpleBookmarkStore simpleBookmarkStore;

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


    /**
     * Create new instance of a {@link BookmarkTableController}.
     */
    public BookmarkTableController() {
        try {
            setSimpleBookmarkStore(Hygene.getInstance().getSimpleBookmarkStore());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to initialize BookmarkTableController.", e);
            new ErrorDialogue(e).show();
        }
    }


    @Override
    @SuppressWarnings("squid:MaximumInheritanceDepth") // We need to overwrite the behaviour of table cells
    public void initialize(final URL location, final ResourceBundle resources) {
        nodeId.setCellValueFactory(cell -> cell.getValue().getNodeIdProperty());
        baseOffset.setCellValueFactory(cell -> cell.getValue().getBaseOffsetProperty());
        description.setCellValueFactory(cell -> cell.getValue().getDescriptionProperty());
        radius.setCellValueFactory(cell -> cell.getValue().getRadiusProperty());

        description.setCellFactory(param -> new TableCell<SimpleBookmark, String>() {
            @Override
            protected void updateItem(final String item, final boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    return;
                }

                final Text text = new Text(item);
                text.setWrappingWidth(param.getWidth() - DESCRIPTION_TEXT_PADDING);
                setPrefHeight(text.getLayoutBounds().getHeight());

                setGraphic(text);
            }
        });

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
    }

    /**
     * Sets the {@link SimpleBookmarkStore} for use by the controller.
     *
     * @param simpleBookmarkStore {@link SimpleBookmarkStore} for use by the controller
     */
    void setSimpleBookmarkStore(final SimpleBookmarkStore simpleBookmarkStore) {
        this.simpleBookmarkStore = simpleBookmarkStore;
    }
}
