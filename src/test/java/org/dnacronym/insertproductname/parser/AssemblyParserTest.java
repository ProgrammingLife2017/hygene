package org.dnacronym.insertproductname.parser;

import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.models.SequenceNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class AssemblyParserTest {
    private AssemblyParser parser;


    @BeforeEach
    void beforeEach() {
        parser = new AssemblyParser();
    }


    @Test
    void testParseEmpty() {
        final Assembly assembly = new Assembly();
        final SequenceGraph graph = parser.parse(assembly);

        assertThat(graph.getStartNode()).isNull();
        assertThat(graph.getEndNode()).isNull();
    }

    @Test
    void testParseOneLooseSegment() {
        final Assembly assembly = new Assembly();
        final Segment segment = new Segment("name", "sequence");

        assembly.addSegment(segment);
        final SequenceGraph graph = parser.parse(assembly);

        assertThat(graph.getStartNode().getId()).isEqualTo("name");
        assertThat(graph.getEndNode().getId()).isEqualTo("name");
    }

    @Test
    void testParseTwoLooseSegments() {
        final Assembly assembly = new Assembly();
        final Segment segmentA = new Segment("A", "sequenceA");
        final Segment segmentB = new Segment("B", "sequenceB");

        assembly.addSegment(segmentA);
        assembly.addSegment(segmentB);
        final SequenceGraph graph = parser.parse(assembly);

        assertThat(graph.getStartNode().getId()).isEqualTo("A");
        assertThat(graph.getEndNode().getId()).isEqualTo("A");
    }

    @Test
    void testParseSingleLink() {
        final Assembly assembly = new Assembly();
        final Segment segmentA = new Segment("A", "sequenceA");
        final Segment segmentB = new Segment("B", "sequenceB");
        final Link link = new Link("A", true, "B", false, 0);

        assembly.addSegment(segmentA);
        assembly.addSegment(segmentB);
        assembly.addLink(link);
        final SequenceGraph graph = parser.parse(assembly);

        assertThat(graph.getStartNode().getId()).isEqualTo("A");
        assertThat(graph.getEndNode().getId()).isEqualTo("B");
    }

    @Test
    void testParseSplit() {
        final Assembly assembly = new Assembly();
        final Segment segmentA = new Segment("A", "sequenceA");
        final Segment segmentB = new Segment("B", "sequenceB");
        final Segment segmentC = new Segment("C", "sequenceC");
        final Link linkAB = new Link("A", true, "B", true, 0);
        final Link linkAC = new Link("A", true, "C", true, 0);

        assembly.addSegment(segmentA);
        assembly.addSegment(segmentB);
        assembly.addSegment(segmentC);
        assembly.addLink(linkAB);
        assembly.addLink(linkAC);
        final SequenceGraph graph = parser.parse(assembly);
        final SequenceNode startNode = graph.getStartNode();
        final Object[] rightNeighbours = startNode.getRightNeighbours().stream().map(SequenceNode::getId).toArray();

        assertThat(startNode.getId()).isEqualTo("A");
        assertThat(rightNeighbours).contains("B");
        assertThat(rightNeighbours).contains("C");
    }

    @Test
    void testParseJoin() {
        final Assembly assembly = new Assembly();
        final Segment segmentA = new Segment("A", "sequenceA");
        final Segment segmentB = new Segment("B", "sequenceB");
        final Segment segmentC = new Segment("C", "sequenceC");
        final Link linkAC = new Link("A", true, "C", true, 0);
        final Link linkBC = new Link("B", true, "C", true, 0);

        assembly.addSegment(segmentA);
        assembly.addSegment(segmentB);
        assembly.addSegment(segmentC);
        assembly.addLink(linkAC);
        assembly.addLink(linkBC);
        final SequenceGraph graph = parser.parse(assembly);
        final SequenceNode endNode = graph.getEndNode();
        final Object[] leftNeighbours = endNode.getLeftNeighbours().stream().map(SequenceNode::getId).toArray();

        assertThat(endNode.getId()).isEqualTo("C");
        assertThat(leftNeighbours).contains("A");
        assertThat(leftNeighbours).contains("B");
    }
}
