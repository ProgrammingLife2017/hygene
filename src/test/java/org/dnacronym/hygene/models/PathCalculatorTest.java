package org.dnacronym.hygene.models;

import org.dnacronym.hygene.graph.Link;
import org.dnacronym.hygene.graph.Segment;
import org.dnacronym.hygene.graph.Subgraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;


/**
 * Created by Niels on 13/06/2017.
 */
class PathCalculatorTest {
    private static final Random RANDOM = new Random();

    private Subgraph subgraph;

    private PathCalculator pathCalculator;


    @BeforeEach
    void setUp() {
        subgraph = new Subgraph();

        pathCalculator = new PathCalculator(subgraph);
    }


    @Test
    void testComputePaths() {
        final Segment segment1 = new Segment(1, 63, 19);
        final Segment segment2 = new Segment(2, 90, 32);
        final Segment segment3 = new Segment(3, 98, 14);
        final Segment segment4 = new Segment(4, 55, 10);

        connectSegments(segment1, segment2);
        connectSegments(segment1, segment3);
        connectSegments(segment1, segment4);
        connectSegments(segment2, segment4);
        connectSegments(segment3, segment4);

        subgraph.addAll(Arrays.asList(segment1, segment2, segment3, segment4));

        System.out.println(subgraph.getSourceConnectedNodes());


//        pathCalculator.computePaths(subgraph);
    }

    @Test
    void testComputePathsTopo() {
        final Segment segment1 = new Segment(1, 63, 19);
        final Segment segment2 = new Segment(2, 90, 32);
        final Segment segment3 = new Segment(3, 98, 14);
        final Segment segment4 = new Segment(4, 55, 10);

        connectSegments(segment1, segment2);
        connectSegments(segment1, segment3);
        connectSegments(segment1, segment4);
        connectSegments(segment2, segment4);
        connectSegments(segment3, segment4);

        segment1.setMetadata(new NodeMetadata("-", "-", Arrays.asList("abcd")));
        segment2.setMetadata(new NodeMetadata("-", "-", Arrays.asList("b")));
        segment3.setMetadata(new NodeMetadata("-", "-", Arrays.asList("cd")));
        segment4.setMetadata(new NodeMetadata("-", "-", Arrays.asList("abcd")));

        subgraph.addAll(Arrays.asList(segment1, segment2, segment3, segment4));

        pathCalculator.computePaths(subgraph);
    }

    /**
     * Connects the two segments with a {@link Link}.
     *
     * @param leftSegment  the left segment
     * @param rightSegment the right segment
     */
    private static void connectSegments(final Segment leftSegment, final Segment rightSegment) {
        final Link link = new Link(leftSegment, rightSegment, RANDOM.nextInt());
        leftSegment.getOutgoingEdges().add(link);
        rightSegment.getIncomingEdges().add(link);
    }
}
