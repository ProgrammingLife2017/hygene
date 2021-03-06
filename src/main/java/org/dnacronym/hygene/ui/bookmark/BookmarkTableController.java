package org.dnacronym.hygene.ui.bookmark;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.graph.node.GfaNode;
import org.dnacronym.hygene.ui.genomeindex.GenomeMappingController;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for showing of {@link SimpleBookmark}s.
 */
public final class BookmarkTableController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(GenomeMappingController.class);
    private static final int DESCRIPTION_TEXT_PADDING = 10;

    @Inject
    private BookmarkStore bookmarkStore;
    @Inject
    private GraphVisualizer graphVisualizer;
    @Inject
    private BookmarkCreateView bookmarkCreateView;

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
    private Button createBookmarkButton;


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

        bookmarksTable.setItems(bookmarkStore.getSimpleBookmarks());

        final ObjectProperty<GfaNode> selectedNodeProperty = graphVisualizer.getSelectedSegmentProperty();
        createBookmarkButton.disableProperty().bind(selectedNodeProperty.isNull());
        selectedNodeProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                createBookmarkButton.setText("Create Bookmark (Select node first)");
            } else {
                createBookmarkButton.setText("Create Bookmark for Node " + newValue.getSegmentIds());
            }
        });
    }

    @FXML
    void createBookmarkAction(final ActionEvent actionEvent) {
        try {
            bookmarkCreateView.showAndWait();
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to show the bookmark create view.", e);
        }

        actionEvent.consume();
    }
}
