package org.dnacronym.hygene.graph;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.Set;


/**
 * Class representing a single, non-dummy segment node.
 */
public final class Segment extends Node {
    private final int id;
    private final int lineNumber;
    private final int sequenceLength;


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
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final Segment segment = (Segment) o;
        return id == segment.id
                && lineNumber == segment.lineNumber
                && sequenceLength == segment.sequenceLength;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, lineNumber, sequenceLength);
    }
}
