package org.dnacronym.hygene.models;

import org.checkerframework.checker.nullness.qual.NonNull;


/**
 * Model of a base bookmark.
 */
public final class Bookmark {
    private final int nodeId;
    private final int baseOffset;
    private final int radius;
    private final @NonNull String description;


    /**
     * Constructs a new {@link Bookmark} instance.
     *
     * @param nodeId      the ID of the bookmarked node
     * @param baseOffset  the offset within that node
     * @param radius      the number of hops that a center-point query should take for this bookmark
     * @param description a description of this bookmark
     */
    public Bookmark(final int nodeId, final int baseOffset, final int radius, final @NonNull String description) {
        this.nodeId = nodeId;
        this.baseOffset = baseOffset;
        this.radius = radius;
        this.description = description;
    }


    /**
     * Returns the ID of the bookmarked node.
     *
     * @return the ID of that node
     */
    public int getNodeId() {
        return nodeId;
    }

    /**
     * Returns the base offset in that node.
     *
     * @return the base offset in that node
     */
    public int getBaseOffset() {
        return baseOffset;
    }

    /**
     * Returns the radius.
     *
     * @return the radius
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Returns the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
