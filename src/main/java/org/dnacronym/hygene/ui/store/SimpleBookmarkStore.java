package org.dnacronym.hygene.ui.store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;


/**
 * Stores all {@link SimpleBookmark}s of the current session.
 */
public final class SimpleBookmarkStore {
    private static final Logger LOGGER = LogManager.getLogger(SimpleBookmarkStore.class);

    private final GraphVisualizer graphVisualizer;

    private final ObservableList<SimpleBookmark> bookmarks;


    /**
     * Create an instance of a {@link SimpleBookmarkStore}.
     * <p>
     * If it observed that the {@link org.dnacronym.hygene.parser.GfaFile} in {@link GraphStore} has changed, it will
     * clear all current {@link SimpleBookmark}s and load the {@link Bookmark}s associated with the new
     * {@link org.dnacronym.hygene.parser.GfaFile}.
     * <p>
     * It uses the {@link GraphVisualizer} as a reference for each internal {@link SimpleBookmark}. The
     * {@link GraphVisualizer} should also observe the {@link GraphStore} directly to ensure this class and the
     * {@link GraphVisualizer} refer to the same {@link org.dnacronym.hygene.models.Graph} at all times.
     *
     * @param graphStore      {@link GraphStore} to be observed by this class
     * @param graphVisualizer {@link GraphVisualizer} to be used by this class
     * @see SimpleBookmark
     */
    public SimpleBookmarkStore(final GraphStore graphStore, final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
        bookmarks = FXCollections.observableArrayList();

        graphStore.getGfaFileProperty().addListener((observable, oldValue, newValue) -> {
            // TODO observe when the graph changes and load the new bookmarks accordingly.
        });
    }

    /**
     * Add a single {@link SimpleBookmark}.
     *
     * @param bookmark        {@link Bookmark} to add
     */
    void addBookmark(final Bookmark bookmark) {
        try {
            bookmarks.add(new SimpleBookmark(bookmark, graphVisualizer));
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
