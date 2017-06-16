package org.dnacronym.hygene.models;

import org.dnacronym.hygene.graph.DummyNode;
import org.dnacronym.hygene.graph.Link;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Segment;
import org.dnacronym.hygene.graph.Subgraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * Unit test for {@link PathCalculator}.
 */
class PathCalculatorTest {
    private static final Random RANDOM = new Random();

    private Subgraph subgraph;

    private PathCalculator pathCalculator;


    @BeforeEach
    void setUp() {
        subgraph = new Subgraph();

        pathCalculator = new PathCalculator();
    }


    @Test
    void testComputePathsNoDummyNodes() {
        final Segment segment1 = new Segment(1, 63, 19);
        final Segment segment2 = new Segment(2, 90, 32);
        final Segment segment3 = new Segment(3, 98, 14);
        final Segment segment4 = new Segment(4, 55, 10);

        final Link e12 = connectNodes(segment1, segment2);
        final Link e13 = connectNodes(segment1, segment3);
        final Link e14 = connectNodes(segment1, segment4);
        final Link e24 = connectNodes(segment2, segment4);
        final Link e34 = connectNodes(segment3, segment4);

        segment1.setMetadata(new NodeMetadata("-", "-", Arrays.asList("a", "b", "c", "d")));
        segment2.setMetadata(new NodeMetadata("-", "-", Arrays.asList("b")));
        segment3.setMetadata(new NodeMetadata("-", "-", Arrays.asList("c", "d")));
        segment4.setMetadata(new NodeMetadata("-", "-", Arrays.asList("a", "b", "c", "d")));

        subgraph.addAll(Arrays.asList(segment1, segment2, segment3, segment4));

        pathCalculator.computePaths(subgraph);

        assertThat(e12.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("b")));
        assertThat(e13.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("c", "d")));
        assertThat(e14.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("a")));
        assertThat(e24.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("b")));
        assertThat(e34.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("c", "d")));
    }

    @Test
    void testComputePathsWithDummyNodes() {
        final Segment segment1 = new Segment(1, 63, 19);
        final Segment segment2 = new Segment(2, 90, 32);
        final Segment segment3 = new Segment(3, 98, 14);
        final Segment segment4 = new Segment(4, 55, 10);
        final DummyNode dummy1 = new DummyNode(segment3, segment4);
        final DummyNode dummy2 = new DummyNode(segment3, segment4);

        final Link e12 = connectNodes(segment1, segment2);
        final Link e13 = connectNodes(segment1, segment3);
        final Link e14 = connectNodes(segment1, segment4);
        final Link e24 = connectNodes(segment2, segment4);
        final Link e3d1 = connectNodes(segment3, dummy1);
        final Link d1d2 = connectNodes(dummy1, dummy2);
        final Link d2s4 = connectNodes(dummy2, segment4);


        segment1.setMetadata(new NodeMetadata("-", "-", Arrays.asList("a", "b", "c", "d")));
        segment2.setMetadata(new NodeMetadata("-", "-", Arrays.asList("b")));
        segment3.setMetadata(new NodeMetadata("-", "-", Arrays.asList("c", "d")));
        segment4.setMetadata(new NodeMetadata("-", "-", Arrays.asList("a", "b", "c", "d")));

        subgraph.addAll(Arrays.asList(segment1, segment2, segment3, segment4, dummy1, dummy2));

        pathCalculator.computePaths(subgraph);

        assertThat(e12.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("b")));
        assertThat(e13.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("c", "d")));
        assertThat(e14.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("a")));
        assertThat(e24.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("b")));
        assertThat(e3d1.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("c", "d")));
        assertThat(d1d2.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("c", "d")));
        assertThat(d2s4.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("c", "d")));
    }


    @Test
    void testComputePathsWithDummyNodes2() {
        final Segment segment1 = new Segment(1, 63, 19);
        final Segment segment2 = new Segment(2, 90, 32);
        final Segment segment3 = new Segment(3, 98, 14);
        final DummyNode dummy1 = new DummyNode(segment1, segment2);
        final DummyNode dummy2 = new DummyNode(segment2, segment3);

        final Link e13 = connectNodes(segment1, segment3);
        final Link s1d1 = connectNodes(segment1, dummy1);
        final Link d1s2 = connectNodes(dummy1, segment2);
        final Link s2d2 = connectNodes(segment2, dummy2);
        final Link d2s3 = connectNodes(dummy2, segment3);


        segment1.setMetadata(new NodeMetadata("1", "-", Arrays.asList("a", "b", "c")));
        segment2.setMetadata(new NodeMetadata("2", "-", Arrays.asList("b", "c")));
        segment3.setMetadata(new NodeMetadata("3", "-", Arrays.asList("a", "b", "c")));

        subgraph.addAll(Arrays.asList(segment1, segment2, segment3, dummy1, dummy2));

        pathCalculator.computePaths(subgraph);

        assertThat(e13.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("a")));
        assertThat(s1d1.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("b", "c")));
        assertThat(d1s2.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("b", "c")));
        assertThat(s2d2.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("b", "c")));
        assertThat(d2s3.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("b", "c")));
    }

    /**
     * Connects the two {@link NewNode} with a {@link Link}.
     *
     * @param leftNode  the left {@link NewNode}
     * @param rightNode the right {@link NewNode}
     * @return the {@link Link}
     */
    private static Link connectNodes(final NewNode leftNode, final NewNode rightNode) {
        final Link link = new Link(leftNode, rightNode, RANDOM.nextInt());
        leftNode.getOutgoingEdges().add(link);
        rightNode.getIncomingEdges().add(link);
        return link;
    }
}
