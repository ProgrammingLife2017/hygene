package org.dnacronym.hygene.graph.node;

import org.dnacronym.hygene.graph.metadata.NodeMetadata;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;


/**
 * Unit tests for {@link AggregateSegment}.
 */
class AggregateSegmentTest {
    @Test
    void testEmptyCollection() {
        assertThrows(IllegalArgumentException.class, () -> new AggregateSegment(new ArrayList<>()));
    }

    @Test
    void testSingleSegmentLength() {
        final Collection<Segment> segments = new ArrayList<>();
        final Segment segment = new Segment(2, 75, 49);
        segments.add(segment);

        final AggregateSegment aggregateSegment = new AggregateSegment(segments);

        assertThat(aggregateSegment.getLength()).isEqualTo(segment.getLength());
    }

    @Test
    void testMultipleNodeLength() {
        final Collection<Segment> segments = new ArrayList<>();
        final Segment segmentA = new Segment(96, 2, 64);
        final Segment segmentB = new Segment(100, 63, 25);
        segments.add(segmentA);
        segments.add(segmentB);

        final AggregateSegment aggregateSegment = new AggregateSegment(segments);

        assertThat(aggregateSegment.getLength()).isEqualTo(segmentB.getLength());
    }

    @Test
    void testGetNodes() {
        final Collection<Segment> segments = new ArrayList<>();
        final Segment segmentA = new Segment(52, 9, 27);
        final Segment segmentB = new Segment(98, 75, 88);
        final Segment segmentC = new Segment(59, 73, 61);
        segments.add(segmentA);
        segments.add(segmentB);
        segments.add(segmentC);

        final AggregateSegment aggregateSegment = new AggregateSegment(segments);

        assertThat(aggregateSegment.getSegments()).isNotSameAs(segments);
        assertThat(aggregateSegment.getSegments()).containsExactlyElementsOf(segments);
    }

    /**
     * Tests that {@link AggregateSegment#getSegments()} returns the nodes in the collection during construction, even
     * when nodes are later added to that collection.
     */
    @Test
    void testGetNodesAddLater() {
        final Collection<Segment> segments = new ArrayList<>();
        final Segment segmentA = new Segment(85, 81, 66);
        segments.add(segmentA);

        final AggregateSegment aggregateSegment = new AggregateSegment(segments);
        final Segment segmentB = new Segment(29, 73, 64);
        segments.add(segmentB);

        assertThat(aggregateSegment.getSegments()).isNotSameAs(segments);
        assertThat(aggregateSegment.getSegments()).containsExactly(segmentA);
    }

    @Test
    void testContainsSegmentTrue() {
        final List<Segment> segments = new ArrayList<>();
        final Segment segmentA = new Segment(42, 86, 72);
        final Segment segmentB = new Segment(27, 55, 5);
        segments.add(segmentA);
        segments.add(segmentB);
        final AggregateSegment aggregateSegment = new AggregateSegment(segments);

        assertThat(aggregateSegment.containsSegment(42)).isTrue();
    }

    @Test
    void testContainsSegmentFalse() {
        final List<Segment> segments = new ArrayList<>();
        final Segment segmentA = new Segment(9, 99, 70);
        final Segment segmentB = new Segment(35, 64, 88);
        segments.add(segmentA);
        segments.add(segmentB);
        final AggregateSegment aggregateSegment = new AggregateSegment(segments);

        assertThat(aggregateSegment.containsSegment(30)).isFalse();
    }

    @Test
    void testThatMetadataIsNotSupported() {
        final AggregateSegment node = new AggregateSegment(Arrays.asList(new Segment(0, 0, 0)));

        final Throwable getMetadataException = catchThrowable(() -> node.getMetadata());
        assertThat(getMetadataException).isInstanceOf(UnsupportedOperationException.class);

        final Throwable setMetadataException = catchThrowable(() -> node.setMetadata(mock(NodeMetadata.class)));
        assertThat(setMetadataException).isInstanceOf(UnsupportedOperationException.class);

        assertThat(node.hasMetadata()).isFalse();
    }
}
