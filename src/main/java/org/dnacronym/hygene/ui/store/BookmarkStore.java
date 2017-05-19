package org.dnacronym.hygene.ui.store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Stores all bookmarks associated with the current graph.
 */
public final class BookmarkStore {
    private final ObservableList<Bookmark> bookmarks;


    /**
     * Create an instance of a {@link BookmarkStore}.
     */
    public BookmarkStore() {
        bookmarks = FXCollections.observableArrayList();
    }


    /**
     * Add an {@link java.util.Collection} of {@link Bookmark}s.
     *
     * @param bookmarks bookmarks to add
     */
    public void addBookmarks(final Bookmark... bookmarks) {
        this.bookmarks.addAll(bookmarks);
    }

    /**
     * Add a single {@link Bookmark}.
     *
     * @param bookmark bookmark to add
     */
    public void addBookmark(final Bookmark bookmark) {
        bookmarks.add(bookmark);
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
