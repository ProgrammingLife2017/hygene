package org.dnacronym.hygene.ui.bookmark;

import javafx.collections.ObservableList;
import org.dnacronym.hygene.graph.bookmark.Bookmark;
import org.dnacronym.hygene.graph.node.Node;


/**
 * Stores all {@link SimpleBookmark}s of the current session.
 */
public interface BookmarkStore {
    /**
     * Writes all {@link Bookmark}s inside all the {@link SimpleBookmark}s in memory to the database.
     */
    void writeBookmarksToFile();

    /**
     * Adds a single {@link SimpleBookmark}.
     *
     * @param bookmark {@link Bookmark} to add
     */
    void addBookmark(Bookmark bookmark);

    /**
     * Checks whether this {@link Node} is bookmarked.
     *
     * @param node the {@link Node} which may or may not be bookmarked
     * @return true if {@link Node} has been bookmarked
     */
    boolean containsBookmark(Node node);

    /**
     * Gets an {@link ObservableList} of {@link SimpleBookmark}s.
     *
     * @return {@link ObservableList} of {@link SimpleBookmark}s
     */
    ObservableList<SimpleBookmark> getSimpleBookmarks();
}
