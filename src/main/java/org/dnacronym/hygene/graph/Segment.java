package org.dnacronym.hygene.graph;


/**
 * Class representing a single, non-dummy segment node.
 */
@SuppressWarnings("squid:S2160") // Superclass equals/hashCode use UUID, which is unique enough
public final class Segment extends NewNode {
    /**
     * The minimal length of a segment.
     */
    public static final int MIN_SEGMENT_LENGTH = 500;

    private final int id;
    private final long byteOffset;
    private final int sequenceLength;


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

    @Override
    public String toString() {
        return "Segment{id=" + id + "}";
    }
}
