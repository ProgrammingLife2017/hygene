package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for the iterators in {@code SequenceGraph}s.
 */
class SequenceGraphIteratorTest {
    private static final Consumer<SequenceNode> DUPLICATE_MODIFIER = node -> node.addReadIdentifier("");
    private static final Function<SequenceNode, Boolean> DUPLICATE_DETECTOR =
            node -> !node.getReadIdentifiers().isEmpty();


    @Test
    void testIteratorEmptyException() {
        final SequenceGraph graph = new SequenceGraph(new ArrayList<>());

        final Throwable e = catchThrowable(() -> graph.iterator().next());
        assertThat(e).isInstanceOf(NoSuchElementException.class);
    }

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
    void testIteratorDuplicateDiamond() {
        final SequenceNode nodeA = new SequenceNode("1", "1");
        final SequenceNode nodeB = new SequenceNode("2", "2");
        final SequenceNode nodeC = new SequenceNode("3", "3");
        final SequenceNode nodeD = new SequenceNode("3", "3");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeC.linkToRightNeighbour(nodeD);

        final SequenceGraph graph = new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD));
        final List<SequenceNode> nodes = iterateModifyCollect(graph.iterator(DUPLICATE_DETECTOR), DUPLICATE_MODIFIER);

        assertThat(nodes).hasSize(4);
        assertThat(nodes).containsExactly(nodeA, nodeB, nodeC, nodeD);
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


    @Test
    void testReverseIteratorDuplicateParallelElements() {
        final SequenceNode nodeA = new SequenceNode("1", "1");
        final SequenceNode nodeB = new SequenceNode("2", "2");
        final SequenceNode nodeC = new SequenceNode("3", "3");
        final SequenceNode nodeD = new SequenceNode("4", "4");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeA.linkToRightNeighbour(nodeD);

        final SequenceGraph graph = new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD));
        final List<SequenceNode> nodes = iterateModifyCollect(graph.reverseIterator(DUPLICATE_DETECTOR),
                DUPLICATE_MODIFIER);

        assertThat(nodes).hasSize(4);
        assertThat(nodes).containsExactly(nodeB, nodeC, nodeD, nodeA);
    }

    @Test
    void testReverseIteratorDuplicateDiamond() {
        final SequenceNode nodeA = new SequenceNode("1", "1");
        final SequenceNode nodeB = new SequenceNode("2", "2");
        final SequenceNode nodeC = new SequenceNode("3", "3");
        final SequenceNode nodeD = new SequenceNode("3", "3");
        nodeA.linkToRightNeighbour(nodeB);
        nodeA.linkToRightNeighbour(nodeC);
        nodeB.linkToRightNeighbour(nodeD);
        nodeC.linkToRightNeighbour(nodeD);

        final SequenceGraph graph = new SequenceGraph(Arrays.asList(nodeA, nodeB, nodeC, nodeD));
        final List<SequenceNode> nodes = iterateModifyCollect(graph.reverseIterator(DUPLICATE_DETECTOR),
                DUPLICATE_MODIFIER);

        assertThat(nodes).hasSize(4);
        assertThat(nodes).containsExactly(nodeD, nodeB, nodeC, nodeA);
    }


    /**
     * Applies the {@code modifier} to each element it iterates over during iteration, and collects the results.
     *
     * @param iterator an {@code Iterator} over {@code SequenceNode}s
     * @param modifier a {@code Consumer} of {@code SequenceNode}s
     * @return the {@code SequenceNode}s returned by the {@code Iterator}
     */
    private List<SequenceNode> iterateModifyCollect(final Iterator<SequenceNode> iterator,
                                                    final Consumer<SequenceNode> modifier) {
        final List<SequenceNode> nodes = new ArrayList<>();
        iterator.forEachRemaining(node -> {
            nodes.add(node);
            modifier.accept(node);
        });
        return nodes;
    }
}
