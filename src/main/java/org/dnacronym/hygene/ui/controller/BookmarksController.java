package org.dnacronym.hygene.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.Bookmark;
import org.dnacronym.hygene.ui.store.BookmarkStore;
import org.dnacronym.hygene.ui.store.GraphStore;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for keeping track of bookmarks.
 */
public final class BookmarksController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ConfigController.class);

    private @MonotonicNonNull BookmarkStore bookmarkStore;
    private @MonotonicNonNull GraphStore graphStore;

    @FXML
    private @MonotonicNonNull TableView<Bookmark> bookmarksTable;
    @FXML
    private @MonotonicNonNull TableColumn<Bookmark, Number> nodeIdColumn;
    @FXML
    private @MonotonicNonNull TableColumn<Bookmark, String> baseColumn;
    @FXML
    private @MonotonicNonNull TableColumn<Bookmark, String> descriptionColumn;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            bookmarkStore = Hygene.getInstance().getBookmarkStore();
            graphStore = Hygene.getInstance().getGraphStore();
        } catch (UIInitialisationException e) {
            LOGGER.error("Unable to initialize BookmarksController.", e);
            return;
        }

        if (bookmarksTable != null && nodeIdColumn != null && baseColumn != null && descriptionColumn != null) {
            nodeIdColumn.setCellValueFactory(cell -> cell.getValue().getNodeIdProperty());

            baseColumn.setCellValueFactory(cell -> {
                final Bookmark bookmark = cell.getValue();
                final int nodeId = bookmark.getNodeIdProperty().get();

                if (graphStore != null) {
                    try {
                        final Graph graph = graphStore.getGfaFileProperty().get().getGraph();
                        final String sequence = graph.getNode(nodeId).retrieveMetadata().getSequence();
                        final String base = String.valueOf(sequence.charAt(bookmark.getBaseOffsetProperty().get()));

                        return new SimpleStringProperty(base);
                    } catch (ParseException e) {
                        LOGGER.error("Unable to parse graph to get base.", e);
                    }
                }

                return new SimpleStringProperty("-");
            });

            descriptionColumn.setCellValueFactory(cell -> cell.getValue().getDescriptionProperty());

            bookmarksTable.setItems(bookmarkStore.getBookmarks());
        }
    }
}
