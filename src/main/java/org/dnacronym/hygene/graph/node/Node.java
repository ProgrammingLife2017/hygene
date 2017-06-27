package org.dnacronym.hygene.graph.node;

import javafx.scene.paint.Color;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.graph.colorscheme.ColorScheme;
import org.dnacronym.hygene.graph.colorscheme.fixed.FixedColorScheme;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.metadata.NodeMetadata;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


/**
 * Class representing a generic node.
 */
public abstract class Node implements LayoutableNode {
    private static ColorScheme colorScheme = new FixedColorScheme(Color.BLACK);

    private final UUID uuid;
    private final Set<Edge> incomingEdges;
    private final Set<Edge> outgoingEdges;

    private long xPosition;
    private int yPosition;


    /**
     * Constructs a new {@link Node} instance without any edges.
     * <p>
     * This class should not be instantiated for regular use, please use {@link Segment} instead.
     */
    protected Node() {
        this.uuid = UUID.randomUUID();
        this.incomingEdges = Collections.synchronizedSet(new LinkedHashSet<>());
        this.outgoingEdges = Collections.synchronizedSet(new LinkedHashSet<>());
    }


    /**
     * Returns this {@link Node}'s {@link UUID}.
     *
     * @return this {@link Node}'s {@link UUID}
     */
    public final UUID getUuid() {
        return uuid;
    }

    @Override
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

    @Override
    public final void setYPosition(final int yPosition) {
        this.yPosition = yPosition;
    }

    @Override
    public final Set<Edge> getIncomingEdges() {
        return incomingEdges;
    }

    @Override
    public final Set<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    /**
     * Returns this {@link Node}'s metadata.
     *
     * @return this {@link Node}'s metadata
     */
    public abstract NodeMetadata getMetadata();

    /**
     * Sets the metadata for this node.
     *
     * @param metadata the metadata for this node
     */
    public abstract void setMetadata(NodeMetadata metadata);

    /**
     * Returns {@code true} iff. this {@link Node} has metadata set.
     *
     * @return {@code true} iff. this {@link Node} has metadata set
     */
    public abstract boolean hasMetadata();


    /**
     * Sets the color scheme.
     *
     * @param colorScheme the color scheme
     */
    public static void setColorScheme(final ColorScheme colorScheme) {
        Node.colorScheme = colorScheme;
    }

    /**
     * Returns the color of the node.
     *
     * @return the color of the node
     */
    public final Color getColor() {
        return colorScheme.calculateColor(this);
    }

    /**
     * Returns the color of a given base.
     * <p>
     * Must be either `A`, `T`, `C` or `G`. If not recognized, simply {@link Color#GRAY}.
     *
     * @param base the base
     * @return the {@link Color}
     */
    public static final Color baseToColor(final char base) {
        switch (base) {
            case 'A':
                return Color.rgb(230, 245, 201);
            case 'T':
                return Color.rgb(244, 202, 228);
            case 'C':
                return Color.rgb(203, 213, 232);
            case 'G':
                return Color.rgb(253, 205, 172);
            default:
                return Color.GRAY;
        }
    }


    @Override
    public final boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Node node = (Node) o;
        return Objects.equals(uuid, node.uuid);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(uuid);
    }
}
