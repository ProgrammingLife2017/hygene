package org.dnacronym.hygene.graph.node;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.graph.metadata.NodeMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Aggregates multiple segments into a single node.
 */
public final class AggregateSegment extends GfaNode {
    private final List<Segment> segments;
    private final int length;

    private @MonotonicNonNull NodeMetadata metadata;


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
     * Returns the segment with the given id, if present.
     *
     * @param segmentId the id of a segment
     * @return the segment with the given id, if present
     */
    @Override
    public Optional<Segment> getSegment(final int segmentId) {
        return segments.stream()
                .filter(segment -> segment.getId() == segmentId)
                .findFirst();
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
     * Returns the ids of the aggregated segments.
     *
     * @return the ids of the aggregated segments
     */
    @Override
    public List<Integer> getSegmentIds() {
        return segments.stream()
                .map(Segment::getId)
                .collect(Collectors.toList());
    }

    /**
     * Returns {@code true} iff. a segment with the given id is aggregated in this node.
     *
     * @param segmentId the id of a segment
     * @return {@code true} iff. a segment with the given id is aggregated in this node
     */
    @Override
    public boolean containsSegment(final int segmentId) {
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
}
