package org.dnacronym.hygene.ui.store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.parser.ParseException;


/**
 * Stores all {@link SimpleBookmark}s of the current session.
 */
public final class SimpleBookmarkStore {
    private static final Logger LOGGER = LogManager.getLogger(SimpleBookmarkStore.class);

    private final ObservableList<SimpleBookmark> bookmarks;


    /**
     * Create an instance of a {@link SimpleBookmarkStore}.
     * <p>
     * If it observed that the {@link org.dnacronym.hygene.parser.GfaFile} in {@link GraphStore} has changed, it will
     * clear all current {@link SimpleBookmark}s and load the {@link Bookmark}s associated with the new
     * {@link org.dnacronym.hygene.parser.GfaFile}.
     *
     * @param graphStore {@link GraphStore} to be observed by this class
     */
    public SimpleBookmarkStore(final GraphStore graphStore) {
        bookmarks = FXCollections.observableArrayList();

        graphStore.getGfaFileProperty().addListener((observable, oldValue, newValue) -> {
            // TODO observe when the graph changes and load the new bookmarks accordingly.
        });
    }

    /**
     * Add a single {@link SimpleBookmark}.
     *
     * @param bookmark bookmark to add
     * @param graph    associated with the given bookmark
     */
    void addBookmark(final Bookmark bookmark, final Graph graph) {
        try {
            bookmarks.add(new SimpleBookmark(bookmark, graph));
        } catch (final ParseException e) {
            LOGGER.error("Unable to create bookmark %s.", bookmark, e);
        }
    }

    /**
     * Get an {@link ObservableList} of {@link SimpleBookmark}s.
     *
     * @return {@link ObservableList} of {@link SimpleBookmark}s
     */
    public ObservableList<SimpleBookmark> getBookmarks() {
        return bookmarks;
    }
}
