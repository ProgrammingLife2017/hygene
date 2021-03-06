package org.dnacronym.hygene.graph.node;

import java.util.List;
import java.util.Optional;


/**
 * An abstract kind of segment that may contain any number of GFA segments.
 */
public abstract class GfaNode extends Node {
    /**
     * Returns the segment with the given id, if present.
     *
     * @param segmentId the id of a segment
     * @return the segment with the given id, if present
     */
    public abstract Optional<Segment> getSegment(int segmentId);

    /**
     * Returns all inner segments.
     *
     * @return all inner segments
     */
    public abstract List<Segment> getSegments();

    /**
     * Returns the ids of all inner segments.
     *
     * @return the ids of all inner segments
     */
    public abstract List<Integer> getSegmentIds();

    /**
     * Returns {@code true} iff. this segment contains a segment with the given id.
     *
     * @param segmentId the id of a segment
     * @return {@code true} iff. this segment contains a segment with the given id
     */
    public abstract boolean containsSegment(int segmentId);
}
