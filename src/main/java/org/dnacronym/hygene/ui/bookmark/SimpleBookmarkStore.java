package org.dnacronym.hygene.ui.bookmark;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.graph.layout.FafospLayerer;
import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.graph.bookmark.Bookmark;
import org.dnacronym.hygene.parser.GfaParseException;
import org.dnacronym.hygene.persistence.FileBookmarks;
import org.dnacronym.hygene.persistence.FileDatabase;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Stores all {@link SimpleBookmark}s of the current session.
 */
public final class SimpleBookmarkStore implements BookmarkStore {
    private static final Logger LOGGER = LogManager.getLogger(SimpleBookmarkStore.class);

    private final GraphVisualizer graphVisualizer;
    private final SequenceVisualizer sequenceVisualizer;
    private final GraphDimensionsCalculator graphDimensionsCalculator;

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
     * It uses the {@link GraphDimensionsCalculator} as a reference for each internal {@link SimpleBookmark}.
     *
     * @param graphStore                the {@link GraphStore} to be observed by this class
     * @param graphVisualizer           the {@link GraphVisualizer} to be used by this class
     * @param graphDimensionsCalculator the {@link GraphDimensionsCalculator} to be used by this class
     * @param sequenceVisualizer        the {@link SequenceVisualizer} to be used by this class
     * @see SimpleBookmark
     */
    @Inject
    public SimpleBookmarkStore(final GraphStore graphStore, final GraphVisualizer graphVisualizer,
                               final GraphDimensionsCalculator graphDimensionsCalculator,
                               final SequenceVisualizer sequenceVisualizer) {
        this.graphDimensionsCalculator = graphDimensionsCalculator;
        this.graphVisualizer = graphVisualizer;
        this.sequenceVisualizer = sequenceVisualizer;

        simpleBookmarks = new ArrayList<>();
        observableSimpleBookmarks = FXCollections.observableList(simpleBookmarks);
        observableSimpleBookmarks.addListener((ListChangeListener<SimpleBookmark>) listener -> graphVisualizer.draw());

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


    @Override
    public void writeBookmarksToFile() {
        if (fileBookmarks == null) {
            return;
        }

        final List<Bookmark> bookmarks = new ArrayList<>(simpleBookmarks.size());

        simpleBookmarks.forEach(simpleBookmark -> bookmarks.add(simpleBookmark.getBookmark()));

        try {
            fileBookmarks.storeAll(bookmarks);
        } catch (final SQLException e) {
            LOGGER.error("Unable to store bookmarks to file.", e);
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

    @Override
    public void addBookmark(final Bookmark bookmark) {
        try {
            observableSimpleBookmarks.add(new SimpleBookmark(bookmark, () -> {
                final long bookmarkPosition = (long) FafospLayerer.LAYER_WIDTH * graphVisualizer.getGraph()
                        .getUnscaledXPosition(bookmark.getNodeId());
                graphDimensionsCalculator.getViewPointProperty().set(bookmarkPosition);
                graphDimensionsCalculator.getRadiusProperty().set(bookmark.getRadius());

                graphVisualizer.setSelectedSegment(bookmark.getNodeId());
                sequenceVisualizer.setOffset(bookmark.getBaseOffset());
            }));
        } catch (final GfaParseException e) {
            LOGGER.error("Unable to create bookmark %s.", bookmark, e);
        }
    }

    @Override
    public boolean containsBookmark(final Node node) {
        if (!(node instanceof Segment)) {
            return false;
        }

        final int nodeId = ((Segment) node).getId();
        for (final SimpleBookmark simpleBookmark : simpleBookmarks) {
            if (simpleBookmark.getBookmark().getNodeId() == nodeId) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ObservableList<SimpleBookmark> getSimpleBookmarks() {
        return observableSimpleBookmarks;
    }
}
