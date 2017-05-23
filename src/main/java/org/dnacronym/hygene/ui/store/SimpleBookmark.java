package org.dnacronym.hygene.ui.store;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.parser.ParseException;


/**
 * A simple bookmark.
 * <p>
 * This is used to create a representation of a {@link Bookmark} for the user and leaves out all
 * non-UI related information.
 */
public final class SimpleBookmark {
    private final Bookmark bookmark;
    private final Graph graph;

    private final StringProperty baseProperty;
    private final StringProperty descriptionProperty;


    /**
     * Constructs a new {@link SimpleBookmark} instance.
     *
     * @param bookmark bookmark associated with this {@link SimpleBookmark}
     * @param graph    graph used to retreive information about the node
     * @throws ParseException if unable to get the sequence of the node in the bookmark
     */
    public SimpleBookmark(final Bookmark bookmark, final Graph graph) throws ParseException {
        this.bookmark = bookmark;
        this.graph = graph;

        final String sequence = graph.getNode(bookmark.getNodeId()).retrieveMetadata().getSequence();
        baseProperty = new SimpleStringProperty(String.valueOf(sequence.charAt(bookmark.getBaseOffset())));

        descriptionProperty = new SimpleStringProperty(bookmark.getDescription());
    }


    /**
     * Get the {@link Bookmark} associated with this {@link SimpleBookmark}.
     *
     * @return {@link Bookmark} associated with this {@link SimpleBookmark}
     */
    public Bookmark getBookmark() {
        return bookmark;
    }

    /**
     * Get the {@link Graph} associated with this {@link SimpleBookmark}.
     *
     * @return {@link Graph} associated with this {@link SimpleBookmark}
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Returns the base property.
     *
     * @return the base property
     */
    public StringProperty getBaseProperty() {
        return baseProperty;
    }

    /**
     * Returns the description property.
     *
     * @return the description property
     */
    public StringProperty getDescriptionProperty() {
        return descriptionProperty;
    }
}
