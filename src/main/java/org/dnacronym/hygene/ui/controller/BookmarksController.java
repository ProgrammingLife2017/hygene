package org.dnacronym.hygene.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.Bookmark;
import org.dnacronym.hygene.ui.store.BookmarkStore;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for keeping track of bookmarks.
 */
public class BookmarksController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ConfigController.class);

    private @MonotonicNonNull BookmarkStore bookmarkStore;

    @FXML
    private TableColumn<Bookmark, Integer> nodeIdColumn;
    @FXML
    private TableColumn<Bookmark, String> baseColumn;
    @FXML
    private TableColumn<Bookmark, String> descriptionColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            bookmarkStore = Hygene.getInstance().getBookmarkStore();
        } catch (UIInitialisationException e) {
            LOGGER.error("Unable to initialize BookmarksController.", e);
        }
    }
}
