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
 * Unit tests for the iterators in {@link SequenceGraph}.
 */
final class SequenceGraphIteratorTest {
    private static final Consumer<SequenceNode> DUPLICATE_MODIFIER = node -> node.addReadIdentifier("");
    private static final Function<SequenceNode, Boolean> DUPLICATE_DETECTOR =
            node -> !node.getReadIdentifiers().isEmpty();


    @Test
    void testIteratorEmptyException() {
        final SequenceGraph graph = new SequenceGraph(new ArrayList<>());
        final Iterator<SequenceNode> iterator = graph.iterator();

        // Contains two sentinel nodes
        iterator.next();
        iterator.next();

        final Throwable e = catchThrowable(iterator::next);
        assertThat(e).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testIteratorEmptySize() {
        final SequenceGraph graph = new SequenceGraph(new ArrayList<>());

        assertThat(graph.iterator()).hasSize(2);
    }

    @Test
    void testIteratorOneElement() {
        final SequenceNode node = new SequenceNode("1", "1");

        final SequenceGraph graph = new SequenceGraph(Collections.singletonList(node));
        final SequenceNode source = graph.getSourceNode();
        final SequenceNode sink = graph.getSinkNode();

        assertThat(graph.iterator()).hasSize(3);
        assertThat(graph.iterator()).containsExactly(source, node, sink);
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
        final SequenceNode source = graph.getSourceNode();
        final SequenceNode sink = graph.getSinkNode();

        assertThat(graph.iterator()).hasSize(6 + 2);
        assertThat(graph.iterator()).containsExactly(source, nodeA, nodeB, nodeC, nodeD, sink, sink, sink);
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
        final SequenceNode source = graph.getSourceNode();
        final SequenceNode sink = graph.getSinkNode();

        assertThat(graph.iterator()).hasSize(6);
        assertThat(graph.iterator()).containsExactly(source, nodeA, nodeB, nodeC, nodeD, sink);
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
        final SequenceNode source = graph.getSourceNode();
        final SequenceNode sink = graph.getSinkNode();

        assertThat(graph.iterator()).hasSize(7 + 1);
        assertThat(graph.iterator()).containsExactly(source, nodeA, nodeB, nodeC, nodeD, nodeD, sink, sink);
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
        final SequenceNode source = graph.getSourceNode();
        final SequenceNode sink = graph.getSinkNode();
        final List<SequenceNode> nodes = iterateModifyCollect(graph.iterator(DUPLICATE_DETECTOR), DUPLICATE_MODIFIER);

        assertThat(nodes).hasSize(6);
        assertThat(nodes).containsExactly(source, nodeA, nodeB, nodeC, nodeD, sink);
    }


    @Test
    void testReverseIteratorEmptySize() {
        final SequenceGraph graph = new SequenceGraph(new ArrayList<>());

        assertThat(graph.reverseIterator()).hasSize(2);
    }

    @Test
    void testReverseIteratorOneElement() {
        final SequenceNode node = new SequenceNode("1", "1");

        final SequenceGraph graph = new SequenceGraph(Collections.singletonList(node));
        final SequenceNode source = graph.getSourceNode();
        final SequenceNode sink = graph.getSinkNode();

        assertThat(graph.reverseIterator()).hasSize(3);
        assertThat(graph.reverseIterator()).containsExactly(sink, node, source);
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
        final SequenceNode source = graph.getSourceNode();
        final SequenceNode sink = graph.getSinkNode();

        assertThat(graph.reverseIterator()).hasSize(8 + 2);
        assertThat(graph.reverseIterator()).containsExactly(sink, nodeB, nodeC, nodeD, nodeA, nodeA, nodeA, source,
                source, source);
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
        final SequenceNode source = graph.getSourceNode();
        final SequenceNode sink = graph.getSinkNode();

        assertThat(graph.reverseIterator()).hasSize(6);
        assertThat(graph.reverseIterator()).containsExactly(sink, nodeD, nodeC, nodeB, nodeA, source);
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
        final SequenceNode source = graph.getSourceNode();
        final SequenceNode sink = graph.getSinkNode();

        assertThat(graph.reverseIterator()).hasSize(7 + 1);
        assertThat(graph.reverseIterator()).containsExactly(sink, nodeD, nodeB, nodeC, nodeA, nodeA, source, source);
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
        final SequenceNode source = graph.getSourceNode();
        final SequenceNode sink = graph.getSinkNode();
        final List<SequenceNode> nodes = iterateModifyCollect(graph.reverseIterator(DUPLICATE_DETECTOR),
                DUPLICATE_MODIFIER);

        assertThat(nodes).hasSize(6);
        assertThat(nodes).containsExactly(sink, nodeB, nodeC, nodeD, nodeA, source);
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
        final SequenceNode source = graph.getSourceNode();
        final SequenceNode sink = graph.getSinkNode();
        final List<SequenceNode> nodes = iterateModifyCollect(graph.reverseIterator(DUPLICATE_DETECTOR),
                DUPLICATE_MODIFIER);

        assertThat(nodes).hasSize(6);
        assertThat(nodes).containsExactly(sink, nodeD, nodeB, nodeC, nodeA, source);
    }


    /**
     * Applies the {@code modifier} to each element it iterates over during iteration, and collects the results.
     *
     * @param iterator an {@link Iterator} over {@link SequenceNode}s
     * @param modifier a {@link Consumer} of {@link SequenceNode}s
     * @return the {@link SequenceNode}s returned by the {@link Iterator}
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
