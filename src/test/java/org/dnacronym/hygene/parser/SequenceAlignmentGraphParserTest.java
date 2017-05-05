package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.SequenceGraph;
import org.dnacronym.hygene.models.SequenceNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


class SequenceAlignmentGraphParserTest {
    private SequenceAlignmentGraphParser parser;


    @BeforeEach
    void beforeEach() {
        parser = new SequenceAlignmentGraphParser();
    }


    @Test
    void testParseEmpty() {
        final SequenceAlignmentGraph graph = new SequenceAlignmentGraph();
        final Throwable e = catchThrowable(() -> parser.parse(graph));

        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testParseOneLooseSegment() throws ParseException {
        final SequenceAlignmentGraph alignmentGraph = new SequenceAlignmentGraph();
        final Segment segment = new Segment("name", "sequence");

        alignmentGraph.addSegment(segment);
        final SequenceGraph graph = parser.parse(alignmentGraph);

        assertThat(graph.getSourceNode().getRightNeighbours().get(0).getId()).isEqualTo("name");
        assertThat(graph.getSinkNode().getLeftNeighbours().get(0).getId()).isEqualTo("name");
    }

    @Test
    void testParseTwoLooseSegments() throws ParseException {
        final SequenceAlignmentGraph alignmentGraph = new SequenceAlignmentGraph();
        final Segment segmentA = new Segment("A", "sequenceA");
        final Segment segmentB = new Segment("B", "sequenceB");

        alignmentGraph.addSegment(segmentA);
        alignmentGraph.addSegment(segmentB);
        final SequenceGraph graph = parser.parse(alignmentGraph);

        assertThat(graph.getSourceNode().getRightNeighbours().get(0).getId()).isEqualTo("A");
        assertThat(graph.getSinkNode().getLeftNeighbours().get(0).getId()).isEqualTo("A");
    }

    @Test
    void testParseSingleLink() throws ParseException {
        final SequenceAlignmentGraph alignmentGraph = new SequenceAlignmentGraph();
        final Segment segmentA = new Segment("A", "sequenceA");
        final Segment segmentB = new Segment("B", "sequenceB");
        final Link link = new Link("A", "B", 0);

        alignmentGraph.addSegment(segmentA);
        alignmentGraph.addSegment(segmentB);
        alignmentGraph.addLink(link);
        final SequenceGraph graph = parser.parse(alignmentGraph);

        assertThat(graph.getSourceNode().getRightNeighbours().get(0).getId()).isEqualTo("A");
        assertThat(graph.getSinkNode().getLeftNeighbours().get(0).getId()).isEqualTo("B");
    }

    @Test
    void testParseSplit() throws ParseException {
        final SequenceAlignmentGraph alignmentGraph = new SequenceAlignmentGraph();
        final Segment segmentA = new Segment("A", "sequenceA");
        final Segment segmentB = new Segment("B", "sequenceB");
        final Segment segmentC = new Segment("C", "sequenceC");
        final Link linkAB = new Link("A", "B", 0);
        final Link linkAC = new Link("A", "C", 0);

        alignmentGraph.addSegment(segmentA);
        alignmentGraph.addSegment(segmentB);
        alignmentGraph.addSegment(segmentC);
        alignmentGraph.addLink(linkAB);
        alignmentGraph.addLink(linkAC);
        final SequenceGraph graph = parser.parse(alignmentGraph);
        final SequenceNode startNode = graph.getSourceNode().getRightNeighbours().get(0);
        final Object[] rightNeighbours = startNode.getRightNeighbours().stream().map(SequenceNode::getId).toArray();

        assertThat(startNode.getId()).isEqualTo("A");
        assertThat(rightNeighbours).contains("B");
        assertThat(rightNeighbours).contains("C");
    }

    @Test
    void testParseJoin() throws ParseException {
        final SequenceAlignmentGraph alignmentGraph = new SequenceAlignmentGraph();
        final Segment segmentA = new Segment("A", "sequenceA");
        final Segment segmentB = new Segment("B", "sequenceB");
        final Segment segmentC = new Segment("C", "sequenceC");
        final Link linkAC = new Link("A", "C", 0);
        final Link linkBC = new Link("B", "C", 0);

        alignmentGraph.addSegment(segmentA);
        alignmentGraph.addSegment(segmentB);
        alignmentGraph.addSegment(segmentC);
        alignmentGraph.addLink(linkAC);
        alignmentGraph.addLink(linkBC);
        final SequenceGraph graph = parser.parse(alignmentGraph);
        final SequenceNode endNode = graph.getSinkNode().getLeftNeighbours().get(0);
        final Object[] leftNeighbours = endNode.getLeftNeighbours().stream().map(SequenceNode::getId).toArray();

        assertThat(endNode.getId()).isEqualTo("C");
        assertThat(leftNeighbours).contains("A");
        assertThat(leftNeighbours).contains("B");
    }
}
