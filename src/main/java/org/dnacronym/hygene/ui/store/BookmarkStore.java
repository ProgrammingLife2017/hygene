package org.dnacronym.hygene.ui.store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dnacronym.hygene.models.Bookmark;


/**
 * Stores all bookmarks associated with the current graph.
 */
public class BookmarkStore {
    private final ObservableList<Bookmark> bookmarks;


    /**
     * Create an instance of a {@link BookmarkStore}.
     */
    public BookmarkStore() {
        bookmarks = FXCollections.observableArrayList();
    }


    /**
     * Get an {@link ObservableList} of {@link Bookmark}s.
     *
     * @return {@link ObservableList} of {@link Bookmark}s
     */
    public ObservableList<Bookmark> getBookmarks() {
        return bookmarks;
    }
}
