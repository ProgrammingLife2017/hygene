package org.dnacronym.hygene.ui.store;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * Model of a base bookmark.
 */
public final class Bookmark {
    private final IntegerProperty nodeId;
    private final IntegerProperty baseOffset;
    private final IntegerProperty radius;
    private final StringProperty description;


    /**
     * Constructs a new {@link Bookmark} instance.
     *
     * @param nodeId      the ID of the bookmarked node
     * @param baseOffset  the offset within that node
     * @param radius      the number of hops that a center-point query should take for this bookmark
     * @param description a description of this bookmark
     */
    public Bookmark(final int nodeId, final int baseOffset, final int radius, final String description) {
        this.nodeId = new SimpleIntegerProperty(nodeId);
        this.baseOffset = new SimpleIntegerProperty(baseOffset);
        this.radius = new SimpleIntegerProperty(radius);
        this.description = new SimpleStringProperty(description);
    }


    /**
     * Returns the ID of the bookmarked node.
     *
     * @return the ID of that node
     */
    public IntegerProperty getNodeIdProperty() {
        return nodeId;
    }

    /**
     * Returns the base offset in that node.
     *
     * @return the base offset in that node
     */
    public IntegerProperty getBaseOffsetProperty() {
        return baseOffset;
    }

    /**
     * Returns the radius.
     *
     * @return the radius
     */
    public IntegerProperty getRadiusProperty() {
        return radius;
    }

    /**
     * Returns the description.
     *
     * @return the description
     */
    public StringProperty getDescriptionProperty() {
        return description;
    }
}
