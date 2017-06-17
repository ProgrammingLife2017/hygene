package org.dnacronym.hygene.graph.node;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.graph.metadata.NodeMetadata;


/**
 * Class representing a single, non-dummy segment node.
 */
@SuppressWarnings("squid:S2160") // Superclass equals/hashCode use UUID, which is unique enough
public final class Segment extends Node {
    /**
     * The minimal length of a segment.
     */
    public static final int MIN_SEGMENT_LENGTH = 500;

    private final int id;
    private final long byteOffset;
    private final int sequenceLength;

    private @MonotonicNonNull NodeMetadata metadata;


    /**
     * Constructs a new {@link Segment} instance.
     *
     * @param id             the internal ID of the node
     * @param byteOffset     the byte offset of the line of the segment in the GFA file this node was defined in
     * @param sequenceLength the length of the node
     */
    public Segment(final int id, final long byteOffset, final int sequenceLength) {
        this.id = id;
        this.byteOffset = byteOffset;
        this.sequenceLength = sequenceLength;
    }


    /**
     * Returns the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the byte offset.
     *
     * @return the byte offset
     */
    public long getByteOffset() {
        return byteOffset;
    }

    /**
     * Returns the sequence length.
     *
     * @return the sequence length
     */
    public int getSequenceLength() {
        return sequenceLength;
    }

    @Override
    public int getLength() {
        return Math.max(MIN_SEGMENT_LENGTH, sequenceLength);
    }

    /**
     * Returns this {@link Node}'s metadata.
     *
     * @return this {@link Node}'s metadata
     */
    public NodeMetadata getMetadata() {
        if (metadata == null) {
            throw new IllegalStateException("Cannot access metadata before it is parsed.");
        }

        return metadata;
    }

    /**
     * Sets the metadata for this node.
     *
     * @param metadata the metadata for this node
     */
    public void setMetadata(final NodeMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Returns {@code true} iff. this {@link Node} has metadata set.
     *
     * @return {@code true} iff. this {@link Node} has metadata set
     */
    public boolean hasMetadata() {
        return metadata != null;
    }

    @Override
    public String toString() {
        return "Segment{id=" + id + "}";
    }
}
