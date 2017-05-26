package org.dnacronym.hygene.ui.bookmark;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.parser.ParseException;


/**
 * A simple bookmark for display in the UI.
 * <p>
 * This is used to create a representation of a {@link Bookmark} for the user and leaves out all
 * non-UI related information.
 */
public final class SimpleBookmark {
    private final Bookmark bookmark;

    private final IntegerProperty nodeIdProperty;
    private final IntegerProperty baseOffsetProperty;
    private final IntegerProperty radiusProperty;
    private final StringProperty descriptionProperty;

    private final Runnable onClick;


    /**
     * Constructs a new {@link SimpleBookmark} instance.
     *
     * @param bookmark bookmark associated with this {@link SimpleBookmark}
     * @param onClick  {@link Runnable} that can be retrieved by calling {@link #getOnClick()}
     * @throws ParseException if unable to get the sequence of the node in the bookmark
     */
    public SimpleBookmark(final Bookmark bookmark, final Runnable onClick) throws ParseException {
        this.bookmark = bookmark;
        this.onClick = onClick;

        nodeIdProperty = new SimpleIntegerProperty(bookmark.getNodeId());
        baseOffsetProperty = new SimpleIntegerProperty(bookmark.getBaseOffset());
        descriptionProperty = new SimpleStringProperty(bookmark.getDescription());
        radiusProperty = new SimpleIntegerProperty(bookmark.getRadius());
    }


    /**
     * Returns the {@link Bookmark} associated with this {@link SimpleBookmark}.
     *
     * @return {@link Bookmark} associated with this {@link SimpleBookmark}
     */
    public Bookmark getBookmark() {
        return bookmark;
    }

    /**
     * Returns the node id {@link IntegerProperty}.
     *
     * @return the node id {@link IntegerProperty}
     */
    public IntegerProperty getNodeIdProperty() {
        return nodeIdProperty;
    }

    /**
     * Returns the base offset {@link IntegerProperty}.
     *
     * @return the base offset {@link IntegerProperty}
     */
    public IntegerProperty getBaseOffsetProperty() {
        return baseOffsetProperty;
    }

    /**
     * Returns the radius {@link IntegerProperty}.
     *
     * @return the radius {@link IntegerProperty}
     */
    public IntegerProperty getRadiusProperty() {
        return radiusProperty;
    }

    /**
     * Returns the description {@link StringProperty}.
     *
     * @return the description {@link StringProperty}
     */
    public StringProperty getDescriptionProperty() {
        return descriptionProperty;
    }

    /**
     * Return the {@link Runnable} which should be fired when the user double clicks on a bookmark
     * <p>
     * This {@link Runnable} updates the center node id and the current node in the given
     * {@link org.dnacronym.hygene.ui.graph.GraphVisualizer} to the one stored internally.
     *
     * @return {@link Runnable} to be fired when user clicks on bookmark
     * @see org.dnacronym.hygene.ui.graph.GraphVisualizer#getCenterNodeIdProperty()
     */
    public Runnable getOnClick() {
        return onClick;
    }
}
