package org.dnacronym.hygene.graph.node;

import org.dnacronym.hygene.graph.metadata.NodeMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;


/**
 * Aggregates multiple segments into a single node.
 */
public final class AggregateSegment extends Segway {
    private final List<Segment> segments;
    private final int length;


    /**
     * Constructs a new {@link AggregateSegment}.
     *
     * @param segments the segments to aggregate
     */
    public AggregateSegment(final Collection<Segment> segments) {
        if (segments.isEmpty()) {
            throw new IllegalArgumentException("AggregateSegment cannot aggregate an empty collection.");
        }

        this.segments = new ArrayList<>(segments);
        this.length = segments.stream()
                .map(Segment::getLength)
                .max(Integer::compare)
                .orElseThrow(() -> new IllegalStateException("Collection is non-empty and has no maximum."));
    }


    /**
     * Returns the aggregated segments.
     *
     * @return the aggregated segments
     */
    @Override
    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    /**
     * Returns {@code true} iff. a segment with the given id is aggregated in this node.
     *
     * @param segmentId the id of a segment
     * @return {@code true} iff. a segment with the given id is aggregated in this node
     */
    @Override
    public boolean containsSegment(int segmentId) {
        return segments.stream().anyMatch(segment -> segment.getId() == segmentId);
    }

    /**
     * Returns the largest length of the segments in this aggregate.
     *
     * @return the largest length of the segment in this aggregate
     */
    @Override
    public int getLength() {
        return length;
    }

    @Override
    public NodeMetadata getMetadata() {
        throw new UnsupportedOperationException("AggregateSegments cannot have metadata.");
    }

    @Override
    public void setMetadata(final NodeMetadata metadata) {
        throw new UnsupportedOperationException("AggregateSegments cannot have metadata.");
    }

    @Override
    public boolean hasMetadata() {
        return false;
    }
}
