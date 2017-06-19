package org.dnacronym.hygene.graph;

import org.dnacronym.hygene.graph.edge.DummyEdge;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.edge.Link;
import org.dnacronym.hygene.graph.metadata.NodeMetadata;
import org.dnacronym.hygene.graph.node.DummyNode;
import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.node.Segment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


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

    /**
     * Test path calculator on the following type of graph.
     * <p>
     * s2 -----------
     * /             \
     * s1-- s3--d1-d2-s4
     * \             /
     * s4------------
     */
    @Test
    void testComputePathsWithDummyNodes1() {
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

        final Link originals3s4 = connectNodes(segment3, segment4);
        final Edge e3d1 = connectNodes(segment3, dummy1, originals3s4);
        final Edge d1d2 = connectNodes(dummy1, dummy2, originals3s4);
        final Edge d2s4 = connectNodes(dummy2, segment4, originals3s4);


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

    /**
     * Test path calculator on the following type of graph.
     * <p>
     * d1--s2--d2-----
     * /              \
     * s1--------------s3
     */
    @Test
    void testComputePathsWithDummyNodes2() {
        final Segment segment1 = new Segment(1, 63, 19);
        final Segment segment2 = new Segment(2, 90, 32);
        final Segment segment3 = new Segment(3, 98, 14);
        final DummyNode dummy1 = new DummyNode(segment1, segment2);
        final DummyNode dummy2 = new DummyNode(segment2, segment3);

        final Link e13 = connectNodes(segment1, segment3);

        final Link originals1s2 = connectNodes(segment1, segment2);
        final DummyEdge s1d1 = connectNodes(segment1, dummy1, originals1s2);
        final DummyEdge d1s2 = connectNodes(dummy1, segment2, originals1s2);

        final Link originals2s3 = connectNodes(segment2, segment3);
        final DummyEdge s2d2 = connectNodes(segment2, dummy2, originals2s3);
        final DummyEdge d2s3 = connectNodes(dummy2, segment3, originals2s3);


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
     * Test path calculator on the following type of graph.
     * <p>
     * d1---d2-----
     * /           \
     * s1----s2----s3
     */
    @Test
    void testComputePathsWithDummyNodes3() {
        final Segment segment1 = new Segment(1, 63, 19);
        final Segment segment2 = new Segment(2, 90, 32);
        final Segment segment3 = new Segment(3, 90, 32);
        final DummyNode dummy1 = new DummyNode(segment1, segment3);
        final DummyNode dummy2 = new DummyNode(segment1, segment3);

        final Link originals1s3 = connectNodes(segment1, segment3);
        final DummyEdge s1d1 = connectNodes(segment1, dummy1, originals1s3);
        final DummyEdge d1d2 = connectNodes(dummy1, dummy2, originals1s3);
        final DummyEdge d2s3 = connectNodes(dummy2, segment3, originals1s3);

        final Link s1s2 = connectNodes(segment1, segment2);
        final Link s2s3 = connectNodes(segment2, segment3);

        segment1.setMetadata(new NodeMetadata("1", "-", Arrays.asList("a", "b", "c")));
        segment2.setMetadata(new NodeMetadata("2", "-", Arrays.asList("b", "c")));
        segment3.setMetadata(new NodeMetadata("2", "-", Arrays.asList("a", "b", "c")));

        subgraph.addAll(Arrays.asList(segment1, segment2, segment3, dummy1, dummy2));

        pathCalculator.computePaths(subgraph);

        assertThat(s1d1.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("a")));
        assertThat(d1d2.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("a")));
        assertThat(d2s3.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("a")));
        assertThat(s1s2.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("b", "c")));
        assertThat(s2s3.getGenomes()).isEqualTo(new HashSet<>(Arrays.asList("b", "c")));
    }

    /**
     * Connects the two {@link Segment}s with a {@link Link}.
     *
     * @param leftNode  the left {@link Node}
     * @param rightNode the right {@link Node}
     * @return the {@link Link}
     */
    private static Link connectNodes(final Segment leftNode, final Segment rightNode) {
        final Link link = new Link(leftNode, rightNode, RANDOM.nextInt());
        leftNode.getOutgoingEdges().add(link);
        rightNode.getIncomingEdges().add(link);
        return link;
    }

    /**
     * Connect the two {@link Node}s with a {@link DummyEdge}.
     *
     * @param leftNode  the left {@link Node}
     * @param rightNode the left {@link Node}
     * @param original  the original {@link Link}
     * @return the {@link DummyEdge}
     */
    private static DummyEdge connectNodes(final Node leftNode, final Node rightNode, final Link original) {
        final DummyEdge dummyEdge = new DummyEdge(leftNode, rightNode, original);
        leftNode.getOutgoingEdges().add(dummyEdge);
        rightNode.getIncomingEdges().add(dummyEdge);
        return dummyEdge;
    }
}
