package org.dnacronym.hygene.graph;

import org.dnacronym.hygene.models.SequenceDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Test suite for the {@link Subgraph} class.
 */
final class SubgraphTest {
    private static final Random RANDOM = new Random();

    private Subgraph subgraph;
    private Set<NewNode> nodes;


    @BeforeEach
    void setUp() {
        nodes = new HashSet<>();
        subgraph = new Subgraph(nodes);
    }


    @Test
    void testGetNodes() {
        assertThat(subgraph.getNodes()).isEqualTo(nodes);
    }

    @Test
    void testAddNode() {
        final NewNode node = mock(NewNode.class);
        subgraph.addNode(node);
        assertThat(subgraph.getNodes()).containsExactly(node);
    }

    @Test
    void testRemoveNode() {
        final NewNode node = mock(NewNode.class);
        subgraph.addNode(node);
        assertThat(subgraph.getNodes()).containsExactly(node);

        subgraph.removeNode(node);
        assertThat(subgraph.getNodes()).isEmpty();
    }

    @Test
    void testGetNeighbours() {
        final Segment segment1 = new Segment(100, 83, 88);
        final Segment segment2 = new Segment(42, 67, 22);
        final Segment segment3 = new Segment(87, 72, 47);

        connectSegments(segment1, segment2);
        connectSegments(segment1, segment3);

        subgraph.addNodes(Arrays.asList(segment1, segment2, segment3));

        assertThat(subgraph.getNeighbours(segment1, SequenceDirection.RIGHT))
                .containsExactlyInAnyOrder(segment2, segment3);
    }

    @Test
    void testGetNodesBFS() {
        final Segment segment1 = new Segment(1, 63, 19);
        final Segment segment2 = new Segment(2, 90, 32);
        final Segment segment3 = new Segment(3, 98, 14);
        final Segment segment4 = new Segment(4, 55, 10);

        connectSegments(segment1, segment2);
        connectSegments(segment1, segment3);
        connectSegments(segment2, segment4);
        connectSegments(segment3, segment4);

        subgraph.addNodes(Arrays.asList(segment1, segment2, segment3, segment4));

        final List<NewNode> nodes = new ArrayList<>(subgraph.getNodesBFS(SequenceDirection.RIGHT));

        assertThat(nodes).doesNotHaveDuplicates();
        assertThat(nodes.indexOf(segment1)).isLessThan(nodes.indexOf(segment2));
        assertThat(nodes.indexOf(segment1)).isLessThan(nodes.indexOf(segment3));
        assertThat(nodes.indexOf(segment2)).isLessThan(nodes.indexOf(segment4));
        assertThat(nodes.indexOf(segment3)).isLessThan(nodes.indexOf(segment4));
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
