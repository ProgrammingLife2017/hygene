package org.dnacronym.hygene.graph.node;

import java.util.List;


/**
 * An abstract kind of segment that may contain GFA segments.
 */
public abstract class Segway extends Node {
    /**
     * Returns all inner segments.
     *
     * @return all inner segments
     */
    public abstract List<Segment> getSegments();

    /**
     * Returns {@code true} iff. this segment contains a segment with the given id.
     *
     * @param segmentId the id of a segment
     * @return {@code true} iff. this segment contains a segment with the given id
     */
    public abstract boolean containsSegment(int segmentId);
}
