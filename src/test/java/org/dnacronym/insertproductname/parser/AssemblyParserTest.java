package org.dnacronym.insertproductname.parser;

import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.models.SequenceNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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
        final Segment segment = mock(Segment.class);
        when(segment.getName()).thenReturn("random string here"); // TODO place random string here

        final Assembly assembly = new Assembly();
        assembly.addSegment(segment);
        final SequenceGraph graph = parser.parse(assembly);

        assertThat(graph.getStartNode().getId()).isEqualTo(segment.getName());
        assertThat(graph.getEndNode().getId()).isEqualTo(segment.getName());
    }

    @Test
    void testParseTwoLooseSegments() {
        final Segment segmentA = mock(Segment.class);
        final Segment segmentB = mock(Segment.class);
        when(segmentA.getName()).thenReturn("random string1 here"); // TODO place random string here
        when(segmentB.getName()).thenReturn("random string2 here"); // TODO place random string here

        final Assembly assembly = new Assembly();
        assembly.addSegment(segmentA);
        assembly.addSegment(segmentB);
        final SequenceGraph graph = parser.parse(assembly);

        assertThat(graph.getStartNode().getId()).isEqualTo(segmentB.getName());
        assertThat(graph.getEndNode().getId()).isEqualTo(segmentB.getName());
    }

    @Test
    void testParseSingleLink() {
        final Segment segmentA = mock(Segment.class);
        final Segment segmentB = mock(Segment.class);
        when(segmentA.getName()).thenReturn("segmentA");
        when(segmentB.getName()).thenReturn("segmentB");
        final Link link = new Link(segmentA.getName(), true, segmentB.getName(), false, 0);

        final Assembly assembly = new Assembly();
        assembly.addSegment(segmentA);
        assembly.addSegment(segmentB);
        assembly.addLink(link);
        final SequenceGraph graph = parser.parse(assembly);

        assertThat(graph.getStartNode().getId()).isEqualTo(segmentA.getName());
        assertThat(graph.getEndNode().getId()).isEqualTo(segmentB.getName());
    }

    @Test
    void testParseSplit() {
        final Segment segmentA = mock(Segment.class);
        final Segment segmentB = mock(Segment.class);
        final Segment segmentC = mock(Segment.class);
        when(segmentA.getName()).thenReturn("random string1 here"); // TODO place random string here
        when(segmentB.getName()).thenReturn("random string2 here"); // TODO place random string here
        when(segmentC.getName()).thenReturn("random string3 here"); // TODO place random string here
        final Link linkAB = new Link(segmentA.getName(), true, segmentB.getName(), false, 0);
        final Link linkAC = new Link(segmentA.getName(), true, segmentC.getName(), false, 0);


        final Assembly assembly = new Assembly();
        assembly.addSegment(segmentA);
        assembly.addSegment(segmentB);
        assembly.addSegment(segmentC);
        assembly.addLink(linkAB);
        assembly.addLink(linkAC);
        final SequenceGraph graph = parser.parse(assembly);
        final SequenceNode startNode = graph.getStartNode();

        assertThat(startNode.getId()).isEqualTo(segmentA.getName());
        // TODO make this dynamic so that the order of right neighbours doesn't matter
        assertThat(startNode.getRightNeighbours().get(0).getId()).isEqualTo(segmentB.getName());
        assertThat(startNode.getRightNeighbours().get(1).getId()).isEqualTo(segmentC.getName());
    }

    @Test
    void testParseJoin() {
        final Segment segmentA = mock(Segment.class);
        final Segment segmentB = mock(Segment.class);
        final Segment segmentC = mock(Segment.class);
        when(segmentA.getName()).thenReturn("random string1 here"); // TODO place random string here
        when(segmentB.getName()).thenReturn("random string2 here"); // TODO place random string here
        when(segmentC.getName()).thenReturn("random string3 here"); // TODO place random string here
        final Link linkAC = new Link(segmentA.getName(), true, segmentC.getName(), false, 0);
        final Link linkBC = new Link(segmentB.getName(), true, segmentC.getName(), false, 0);


        final Assembly assembly = new Assembly();
        assembly.addSegment(segmentA);
        assembly.addSegment(segmentB);
        assembly.addSegment(segmentC);
        assembly.addLink(linkAC);
        assembly.addLink(linkBC);
        final SequenceGraph graph = parser.parse(assembly);
        final SequenceNode endNode = graph.getEndNode();

        assertThat(endNode.getId()).isEqualTo(segmentC.getName());
        // TODO make this dynamic so that the order of right neighbours doesn't matter
        assertThat(endNode.getLeftNeighbours().get(0).getId()).isEqualTo(segmentA.getName());
        assertThat(endNode.getLeftNeighbours().get(1).getId()).isEqualTo(segmentB.getName());
    }
}
