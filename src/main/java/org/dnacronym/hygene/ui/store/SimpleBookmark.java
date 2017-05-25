package org.dnacronym.hygene.ui.store;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.dnacronym.hygene.models.Bookmark;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;


/**
 * A simple bookmark for display in the UI.
 * <p>
 * This is used to create a representation of a {@link Bookmark} for the user and leaves out all
 * non-UI related information.
 */
public final class SimpleBookmark {
    private final IntegerProperty nodeIdProperty;
    private final IntegerProperty baseOffsetProperty;
    private final StringProperty descriptionProperty;

    private final Runnable onClick;

    /**
     * Constructs a new {@link SimpleBookmark} instance.
     *
     * @param bookmark        bookmark associated with this {@link SimpleBookmark}
     * @param graphVisualizer {@link GraphVisualizer} that bookmark is associated with
     * @throws ParseException if unable to get the sequence of the node in the bookmark
     */
    public SimpleBookmark(final Bookmark bookmark, final GraphVisualizer graphVisualizer) throws ParseException {
        nodeIdProperty = new SimpleIntegerProperty(bookmark.getNodeId());
        baseOffsetProperty = new SimpleIntegerProperty(bookmark.getBaseOffset());
        descriptionProperty = new SimpleStringProperty(bookmark.getDescription());

        onClick = () -> graphVisualizer.getCenterNodeIdProperty().set(nodeIdProperty.get());
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
     * This {@link Runnable} updates the center node id in the given {@link GraphVisualizer} to the one stored
     * internally.
     *
     * @return {@link Runnable} to be fired when user clicks on bookmark
     * @see GraphVisualizer#getCenterNodeIdProperty()
     */
    public Runnable getOnClick() {
        return onClick;
    }
}
