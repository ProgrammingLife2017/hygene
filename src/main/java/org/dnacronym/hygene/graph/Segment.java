package org.dnacronym.hygene.graph;

import java.util.HashSet;
import java.util.Set;


/**
 * Class representing a single, non-dummy segment node.
 */
@SuppressWarnings("squid:S2160") // Superclass equals/hashCode use UUID, which is unique enough
public final class Segment extends Node {
    /**
     * The minimal length of a segment.
     */
    private static final int MIN_SEGMENT_LENGTH = 100;

    private final int id;
    private final int lineNumber;
    private final int sequenceLength;


    /**
     * Constructs a new {@link Segment} instance.
     *
     * @param id             the internal ID of the node
     * @param lineNumber     the number of the corresponding segment in the GFA file this node was defined in
     * @param sequenceLength the length of the node
     */
    public Segment(final int id, final int lineNumber, final int sequenceLength) {
        this(id, lineNumber, sequenceLength, new HashSet<>(), new HashSet<>());
    }

    /**
     * Constructs a new {@link Segment} instance.
     *
     * @param id             the internal ID of the node
     * @param lineNumber     the number of the corresponding segment in the GFA file this node was defined in
     * @param sequenceLength the length of the node
     * @param incomingEdges  the incoming edges
     * @param outgoingEdges  the outgoing edges
     */
    public Segment(final int id, final int lineNumber, final int sequenceLength, final Set<Edge> incomingEdges,
                   final Set<Edge> outgoingEdges) {
        super(incomingEdges, outgoingEdges);

        this.id = id;
        this.lineNumber = lineNumber;
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
     * Returns the line number.
     *
     * @return the line number
     */
    public int getLineNumber() {
        return lineNumber;
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
}
