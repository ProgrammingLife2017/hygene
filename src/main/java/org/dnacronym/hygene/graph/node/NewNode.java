package org.dnacronym.hygene.graph.node;

import javafx.scene.paint.Color;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.graph.colorscheme.ColorScheme;
import org.dnacronym.hygene.graph.colorscheme.fixed.FixedColorScheme;
import org.dnacronym.hygene.graph.metadata.NodeMetadata;
import org.dnacronym.hygene.graph.edge.Edge;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


/**
 * Class representing a generic node.
 */
public abstract class NewNode {
    private static ColorScheme colorScheme = new FixedColorScheme(Color.BLACK);

    private final UUID uuid;
    private final Set<Edge> incomingEdges;
    private final Set<Edge> outgoingEdges;

    private long xPosition;
    private int yPosition;


    /**
     * Constructs a new {@link NewNode} instance without any edges.
     * <p>
     * This class should not be instantiated for regular use, please use {@link Segment} instead.
     */
    protected NewNode() {
        this.uuid = UUID.randomUUID();
        this.incomingEdges = Collections.synchronizedSet(new LinkedHashSet<>());
        this.outgoingEdges = Collections.synchronizedSet(new LinkedHashSet<>());
    }


    /**
     * Returns this {@link NewNode}'s {@link UUID}.
     *
     * @return this {@link NewNode}'s {@link UUID}
     */
    public final UUID getUuid() {
        return uuid;
    }

    /**
     * Returns the X position.
     *
     * @return the X position
     */
    public final long getXPosition() {
        return xPosition;
    }

    /**
     * Sets the X position.
     *
     * @param xPosition the X position
     */
    public final void setXPosition(final long xPosition) {
        this.xPosition = xPosition;
    }

    /**
     * Returns the Y position.
     *
     * @return the Y position
     */
    public final int getYPosition() {
        return yPosition;
    }

    /**
     * Sets the Y position.
     *
     * @param yPosition the Y position
     */
    public final void setYPosition(final int yPosition) {
        this.yPosition = yPosition;
    }

    /**
     * Returns the incoming edges.
     *
     * @return the incoming edges
     */
    public final Set<Edge> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * Returns the outgoing edges.
     *
     * @return the outgoing edges
     */
    public final Set<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    /**
     * Returns the length of the node when visualized.
     *
     * @return the length of the node when visualized
     */
    public abstract int getLength();

    /**
     * Returns this {@link NewNode}'s metadata.
     *
     * @return this {@link NewNode}'s metadata
     */
    public abstract NodeMetadata getMetadata();

    /**
     * Sets the metadata for this node.
     *
     * @param metadata the metadata for this node
     */
    public abstract void setMetadata(NodeMetadata metadata);

    /**
     * Returns {@code true} iff. this {@link NewNode} has metadata set.
     *
     * @return {@code true} iff. this {@link NewNode} has metadata set
     */
    public abstract boolean hasMetadata();


    /**
     * Returns the color scheme.
     *
     * @return the color scheme
     */
    public static ColorScheme getColorScheme() {
        return colorScheme;
    }

    /**
     * Sets the color scheme.
     *
     * @param colorScheme the color scheme
     */
    public static void setColorScheme(final ColorScheme colorScheme) {
        NewNode.colorScheme = colorScheme;
    }

    /**
     * Returns the color of the node.
     *
     * @return the color of the node
     */
    public final Color getColor() {
        return colorScheme.calculateColor(this);
    }


    @Override
    public final boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NewNode node = (NewNode) o;
        return Objects.equals(uuid, node.uuid);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(uuid);
    }
}
