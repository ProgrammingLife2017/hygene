package org.dnacronym.hygene.ui.bookmark;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.persistence.FileBookmarks;
import org.dnacronym.hygene.persistence.FileDatabase;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Stores all {@link SimpleBookmark}s of the current session.
 */
public final class SimpleBookmarkStore {
    private static final Logger LOGGER = LogManager.getLogger(SimpleBookmarkStore.class);

    private final GraphVisualizer graphVisualizer;

    private final List<SimpleBookmark> simpleBookmarks;
    private final ObservableList<SimpleBookmark> observableSimpleBookmarks;

    private FileBookmarks fileBookmarks;


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
        simpleBookmarks = new ArrayList<>();
        observableSimpleBookmarks = FXCollections.observableList(simpleBookmarks);

        graphStore.getGfaFileProperty().addListener((observable, oldValue, newValue) -> {
            try {
                fileBookmarks = new FileBookmarks(new FileDatabase(newValue.getFileName()));

                simpleBookmarks.clear();
                addBookmarks(fileBookmarks.getAll());
            } catch (final SQLException | IOException e) {
                LOGGER.error("Unable to load bookmarks from file.", e);
            }
        });
    }


    /**
     * Write all {@link Bookmark}s inside all the {@link SimpleBookmark}s in memory to the database.
     */
    public void writeBookmarksToFile() {
        if (fileBookmarks != null) {
            List<Bookmark> bookmarks = new ArrayList<>(simpleBookmarks.size());

            simpleBookmarks.forEach(simpleBookmark -> bookmarks.add(simpleBookmark.getBookmark()));

            try {
                fileBookmarks.storeAll(bookmarks);
            } catch (final SQLException e) {
                LOGGER.error("Unable to recent bookmarks to file.", e);
            }
        }
    }

    /**
     * Add all {@link Bookmark}s in a given collection.
     *
     * @param bookmarks {@link java.util.Collection} of {@link Bookmark}s
     */
    void addBookmarks(final List<Bookmark> bookmarks) {
        for (final Bookmark bookmark : bookmarks) {
            addBookmark(bookmark);
        }
    }

    /**
     * Adds a single {@link SimpleBookmark}.
     *
     * @param bookmark {@link Bookmark} to add
     */
    public void addBookmark(final Bookmark bookmark) {
        try {
            observableSimpleBookmarks.add(new SimpleBookmark(bookmark, () -> {
                graphVisualizer.getCenterNodeIdProperty().set(bookmark.getNodeId());
                graphVisualizer.getHopsProperty().set(bookmark.getRadius());

                graphVisualizer.setSelectedNode(bookmark.getNodeId());
            }));
        } catch (final ParseException e) {
            LOGGER.error("Unable to create bookmark %s.", bookmark, e);
        }
    }

    /**
     * Gets an {@link ObservableList} of {@link SimpleBookmark}s.
     *
     * @return {@link ObservableList} of {@link SimpleBookmark}s
     */
    public ObservableList<SimpleBookmark> getSimpleBookmarks() {
        return observableSimpleBookmarks;
    }
}
