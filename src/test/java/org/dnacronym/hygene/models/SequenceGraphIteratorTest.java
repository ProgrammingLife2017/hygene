package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for the iterators in {@code SequenceGraph}s.
 */
class SequenceGraphIteratorTest {
    @Test
    void testIteratorEmpty() {
        final SequenceGraph graph = new SequenceGraph(new ArrayList<>());
        assertThat(graph.iterator()).isEmpty();
    }

    @Test
    void testIteratorOneElement() {
        final SequenceNode node = new SequenceNode("1", "1");
        final SequenceGraph graph = new SequenceGraph(Collections.singletonList(node));

        assertThat(graph.iterator()).hasSize(1);
        assertThat(graph.iterator()).containsExactly(node);
    }

    @Test
    void testIteratorParallelElements() {
        final SequenceNode nodeA = new SequenceNode("1", "1");
        final SequenceNode nodeB = new SequenceNode("2", "2");
        final SequenceNode nodeC = new SequenceNode("3", "3");
        final SequenceNode nodeD = new SequenceNode("4", "4");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeA.linkToRightNeighbour(nodeD);

        final SequenceGraph graph = new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD));

        assertThat(graph.iterator()).hasSize(4);
        assertThat(graph.iterator()).containsExactly(nodeA, nodeB, nodeC, nodeD);
    }

    @Test
    void testIteratorSequentialElements() {
        final SequenceNode nodeA = new SequenceNode("1", "1");
        final SequenceNode nodeB = new SequenceNode("2", "2");
        final SequenceNode nodeC = new SequenceNode("3", "3");
        final SequenceNode nodeD = new SequenceNode("3", "3");
        nodeA.linkToRightNeighbour(nodeB);
        nodeB.linkToRightNeighbour(nodeC);
        nodeC.linkToRightNeighbour(nodeD);

        final SequenceGraph graph = new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD));

        assertThat(graph.iterator()).hasSize(4);
        assertThat(graph.iterator()).containsExactly(nodeA, nodeB, nodeC, nodeD);
    }

    @Test
    void testIteratorDiamond() {
        final SequenceNode nodeA = new SequenceNode("1", "1");
        final SequenceNode nodeB = new SequenceNode("2", "2");
        final SequenceNode nodeC = new SequenceNode("3", "3");
        final SequenceNode nodeD = new SequenceNode("3", "3");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeC.linkToRightNeighbour(nodeD);

        final SequenceGraph graph = new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD));

        assertThat(graph.iterator()).hasSize(5);
        assertThat(graph.iterator()).containsExactly(nodeA, nodeB, nodeC, nodeD, nodeD);
    }


    @Test
    void testReverseIteratorEmpty() {
        final SequenceGraph graph = new SequenceGraph(new ArrayList<>());
        assertThat(graph.reverseIterator()).hasSize(0);
    }

    @Test
    void testReverseIteratorOneElement() {
        final SequenceNode node = new SequenceNode("1", "1");
        final SequenceGraph graph = new SequenceGraph(Collections.singletonList(node));

        assertThat(graph.reverseIterator()).hasSize(1);
        assertThat(graph.reverseIterator()).containsExactly(node);
    }

    @Test
    void testReverseIteratorParallelElements() {
        final SequenceNode nodeA = new SequenceNode("1", "1");
        final SequenceNode nodeB = new SequenceNode("2", "2");
        final SequenceNode nodeC = new SequenceNode("3", "3");
        final SequenceNode nodeD = new SequenceNode("4", "4");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeA.linkToRightNeighbour(nodeD);

        final SequenceGraph graph = new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD));

        assertThat(graph.reverseIterator()).hasSize(6);
        assertThat(graph.reverseIterator()).containsExactly(nodeB, nodeC, nodeD, nodeA, nodeA, nodeA);
    }

    @Test
    void testReverseIteratorSequentialElements() {
        final SequenceNode nodeA = new SequenceNode("1", "1");
        final SequenceNode nodeB = new SequenceNode("2", "2");
        final SequenceNode nodeC = new SequenceNode("3", "3");
        final SequenceNode nodeD = new SequenceNode("3", "3");
        nodeA.linkToRightNeighbour(nodeB);
        nodeB.linkToRightNeighbour(nodeC);
        nodeC.linkToRightNeighbour(nodeD);

        final SequenceGraph graph = new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD));

        assertThat(graph.reverseIterator()).hasSize(4);
        assertThat(graph.reverseIterator()).containsExactly(nodeD, nodeC, nodeB, nodeA);
    }

    @Test
    void testReverseIteratorDiamond() {
        final SequenceNode nodeA = new SequenceNode("1", "1");
        final SequenceNode nodeB = new SequenceNode("2", "2");
        final SequenceNode nodeC = new SequenceNode("3", "3");
        final SequenceNode nodeD = new SequenceNode("3", "3");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeC.linkToRightNeighbour(nodeD);

        final SequenceGraph graph = new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD));

        assertThat(graph.reverseIterator()).hasSize(5);
        assertThat(graph.reverseIterator()).containsExactly(nodeD, nodeB, nodeC, nodeA, nodeA);
    }
}
