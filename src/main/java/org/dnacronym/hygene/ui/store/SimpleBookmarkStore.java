package org.dnacronym.hygene.ui.store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.parser.ParseException;

import java.io.File;


/**
 * Stores all {@link SimpleBookmark}s of the current session.
 */
public final class SimpleBookmarkStore {
    private static final Logger LOGGER = LogManager.getLogger(SimpleBookmarkStore.class);

    private final ObservableList<SimpleBookmark> bookmarks;


    /**
     * Create an instance of a {@link SimpleBookmarkStore}.
     */
    public SimpleBookmarkStore() {
        // TODO observe when the graph changes and load the new bookmarks accordingly.
        bookmarks = FXCollections.observableArrayList();
    }


    /**
     * Load al the {@link Bookmark}s of a given {@link Graph} into memory.
     * <p>
     * Each {@link Bookmark} is wrapped in a {@link SimpleBookmark} to allow it to be easily displayed in the UI.
     *
     * @param file  database file where all the {@link Bookmark}s are stored
     * @param graph {@link Graph} associated with this database
     */
    public void load(final File file, final Graph graph) {
        // TODO implement this method. Currently it's signature is roughly equal to that of the GraphStore
    }

    /**
     * Add a single {@link SimpleBookmark}.
     *
     * @param bookmark bookmark to add
     * @param graph    associated with the given bookmark
     */
    void addBookmark(final Bookmark bookmark, final Graph graph) {
        try {
            final SimpleBookmark simpleBookmark = new SimpleBookmark(bookmark, graph);
            bookmarks.add(simpleBookmark);
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
