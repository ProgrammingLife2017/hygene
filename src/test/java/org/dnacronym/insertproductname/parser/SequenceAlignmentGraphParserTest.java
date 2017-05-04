package org.dnacronym.insertproductname.parser;

import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.models.SequenceNode;
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
        final SequenceAlignmentGraph sag = new SequenceAlignmentGraph();
        final Throwable e = catchThrowable(() -> parser.parse(sag));

        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testParseOneLooseSegment() throws ParseException {
        final SequenceAlignmentGraph sag = new SequenceAlignmentGraph();
        final Segment segment = new Segment("name", "sequence");

        sag.addSegment(segment);
        final SequenceGraph graph = parser.parse(sag);

        assertThat(graph.getStartNode().getId()).isEqualTo("name");
        assertThat(graph.getEndNode().getId()).isEqualTo("name");
    }

    @Test
    void testParseTwoLooseSegments() throws ParseException {
        final SequenceAlignmentGraph sag = new SequenceAlignmentGraph();
        final Segment segmentA = new Segment("A", "sequenceA");
        final Segment segmentB = new Segment("B", "sequenceB");

        sag.addSegment(segmentA);
        sag.addSegment(segmentB);
        final SequenceGraph graph = parser.parse(sag);

        assertThat(graph.getStartNode().getId()).isEqualTo("A");
        assertThat(graph.getEndNode().getId()).isEqualTo("A");
    }

    @Test
    void testParseSingleLink() throws ParseException {
        final SequenceAlignmentGraph sag = new SequenceAlignmentGraph();
        final Segment segmentA = new Segment("A", "sequenceA");
        final Segment segmentB = new Segment("B", "sequenceB");
        final Link link = new Link("A", "B", 0);

        sag.addSegment(segmentA);
        sag.addSegment(segmentB);
        sag.addLink(link);
        final SequenceGraph graph = parser.parse(sag);

        assertThat(graph.getStartNode().getId()).isEqualTo("A");
        assertThat(graph.getEndNode().getId()).isEqualTo("B");
    }

    @Test
    void testParseSplit() throws ParseException {
        final SequenceAlignmentGraph sag = new SequenceAlignmentGraph();
        final Segment segmentA = new Segment("A", "sequenceA");
        final Segment segmentB = new Segment("B", "sequenceB");
        final Segment segmentC = new Segment("C", "sequenceC");
        final Link linkAB = new Link("A", "B", 0);
        final Link linkAC = new Link("A", "C", 0);

        sag.addSegment(segmentA);
        sag.addSegment(segmentB);
        sag.addSegment(segmentC);
        sag.addLink(linkAB);
        sag.addLink(linkAC);
        final SequenceGraph graph = parser.parse(sag);
        final SequenceNode startNode = graph.getStartNode();
        final Object[] rightNeighbours = startNode.getRightNeighbours().stream().map(SequenceNode::getId).toArray();

        assertThat(startNode.getId()).isEqualTo("A");
        assertThat(rightNeighbours).contains("B");
        assertThat(rightNeighbours).contains("C");
    }

    @Test
    void testParseJoin() throws ParseException {
        final SequenceAlignmentGraph sag = new SequenceAlignmentGraph();
        final Segment segmentA = new Segment("A", "sequenceA");
        final Segment segmentB = new Segment("B", "sequenceB");
        final Segment segmentC = new Segment("C", "sequenceC");
        final Link linkAC = new Link("A", "C", 0);
        final Link linkBC = new Link("B", "C", 0);

        sag.addSegment(segmentA);
        sag.addSegment(segmentB);
        sag.addSegment(segmentC);
        sag.addLink(linkAC);
        sag.addLink(linkBC);
        final SequenceGraph graph = parser.parse(sag);
        final SequenceNode endNode = graph.getEndNode();
        final Object[] leftNeighbours = endNode.getLeftNeighbours().stream().map(SequenceNode::getId).toArray();

        assertThat(endNode.getId()).isEqualTo("C");
        assertThat(leftNeighbours).contains("A");
        assertThat(leftNeighbours).contains("B");
    }
}
