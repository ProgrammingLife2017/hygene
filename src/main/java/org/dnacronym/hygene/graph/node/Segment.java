package org.dnacronym.hygene.graph.node;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.graph.metadata.NodeMetadata;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Class representing a single, non-dummy segment node.
 */
@SuppressWarnings("squid:S2160") // Superclass equals/hashCode use UUID, which is unique enough
public final class Segment extends GfaNode {
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
     * Returns this segment if it has the given id.
     *
     * @param segmentId the id of a segment
     * @return this segment if it has the given id
     */
    @Override
    public Optional<Segment> getSegment(final int segmentId) {
        return id == segmentId ? Optional.of(this) : Optional.empty();
    }

    /**
     * Returns this {@link Segment} as a singleton list.
     *
     * @return this {@link Segment} as a singleton list
     */
    @Override
    public List<Segment> getSegments() {
        return Collections.singletonList(this);
    }

    /**
     * Returns {@code true} iff. this segment has the given id.
     *
     * @param segmentId the id of a segment
     * @return {@code true} iff. this segment has the given id
     */
    @Override
    public boolean containsSegment(final int segmentId) {
        return id == segmentId;
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
     * Returns {@code true} iff. the given id equals this segment's id.
     *
     * @return {@code true} iff. the given id equals this segment's id
     */
    @Override
    public NodeMetadata getMetadata() {
        if (metadata == null) {
            throw new IllegalStateException("Cannot access metadata before it is parsed.");
        }

        return metadata;
    }

    @Override
    public void setMetadata(final NodeMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean hasMetadata() {
        return metadata != null;
    }

    @Override
    public String toString() {
        return "Segment{id=" + id + "}";
    }
}
