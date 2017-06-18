package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.core.UnsignedInteger;
import org.dnacronym.hygene.graph.ArrayBasedNode;
import org.dnacronym.hygene.graph.ArrayBasedEdge;
import org.dnacronym.hygene.graph.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link GfaParser}.
 */
final class GfaParserTest {
    private GfaParser parser;


    @BeforeEach
    void beforeEach() {
        parser = new GfaParser();
    }


    @Test
    void testParseEmpty() throws ParseException {
        final Throwable e = catchThrowable(() -> parse(""));
        assertThat(e).isInstanceOf(ParseException.class);
        assertThat(e).hasMessageContaining("The GFA file should contain at least one segment.");
    }

    @Test
    void testUnknownRecordType() {
        final String gfa = "ç 4 + 2 + 1M";

        final Throwable e = catchThrowable(() -> parse(gfa));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testIgnoredRecordTypes() throws ParseException {
        final String gfa = "H header\nC containment\nP path\nS name content";
        assertThat(parse(gfa)).isNotNull();
    }

    @Test
    void testLinkWithMissingNode() {
        final String gfa = "L 1 +";

        final Throwable e = catchThrowable(() -> parse(gfa));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testSegmentProperties() throws ParseException {
        final String gfa = "S name1 contents\nS name2 contents\nS name3 contents";
        final Graph graph = parse(gfa);

        assertThat(graph.getByteOffset(3)).isEqualTo(34);
        assertThat(graph.getLength(3)).isEqualTo(500);
    }

    @Test
    void testSizes() throws ParseException {
        final String gfa = "S 1 A\nS 2 B\nL 1 + 2 + 0M";
        final Graph graph = parse(gfa);

        assertThat(graph.getNodeArrays()).hasSize(4);
        assertThat(ArrayBasedNode.fromGraph(graph, 1).getOutgoingEdges()).hasSize(1);
        assertThat(ArrayBasedNode.fromGraph(graph, 2).getIncomingEdges()).hasSize(1);
    }

    @Test
    void testEdges() throws ParseException {
        final String gfa = "S 1 A\nS 2 B\nL 1 + 2 + 0M\nL 2 + 1 + 0M";
        final Graph graph = parse(gfa);

        final ArrayBasedNode firstNode = ArrayBasedNode.fromGraph(graph, 1);

        assertThat(firstNode.getNumberOfOutgoingEdges()).isEqualTo(1);
        assertThat(firstNode.getOutgoingEdges()).contains(new ArrayBasedEdge(1, 2, UnsignedInteger.fromLong(12), null));

        assertThat(firstNode.getNumberOfIncomingEdges()).isEqualTo(1);
        assertThat(firstNode.getIncomingEdges()).contains(new ArrayBasedEdge(2, 1, UnsignedInteger.fromLong(25), null));
    }

    @Test
    void testEdgeSpecifiedBeforeNode() throws ParseException {
        final String gfa = "S 1 A\nL 1 + 2 + 0M\nS 2 B";
        final Graph graph = parse(gfa);

        final ArrayBasedNode firstNode = ArrayBasedNode.fromGraph(graph, 1);
        final ArrayBasedNode secondNode = ArrayBasedNode.fromGraph(graph, 2);

        assertThat(firstNode.getNumberOfOutgoingEdges()).isEqualTo(1);
        assertThat(firstNode.getOutgoingEdges()).contains(new ArrayBasedEdge(1, 2, UnsignedInteger.fromLong(6), null));

        assertThat(secondNode.getNumberOfIncomingEdges()).isEqualTo(1);
        assertThat(secondNode.getIncomingEdges()).contains(new ArrayBasedEdge(1, 2, UnsignedInteger.fromLong(6), null));
    }

    @Test
    void testEdgeSpecifiedBeforeAllNodes() throws ParseException {
        final String gfa = "L 100 + 200 + 0M\nS 100 A\nS 200 B";
        final Graph graph = parse(gfa);

        final ArrayBasedNode firstNode = ArrayBasedNode.fromGraph(graph, 1);
        final ArrayBasedNode secondNode = ArrayBasedNode.fromGraph(graph, 2);

        assertThat(firstNode.getNumberOfOutgoingEdges()).isEqualTo(1);
        assertThat(firstNode.getOutgoingEdges()).contains(new ArrayBasedEdge(1, 2, UnsignedInteger.fromLong(0), null));

        assertThat(secondNode.getNumberOfIncomingEdges()).isEqualTo(1);
        assertThat(secondNode.getIncomingEdges()).contains(new ArrayBasedEdge(1, 2, UnsignedInteger.fromLong(0), null));
    }

    @Test
    void testSourceNodesAreAdded() throws ParseException {
        final String gfa = "S 1 A\nS 2 B\nL 1 + 2 + 0M";
        final Graph graph = parse(gfa);

        assertThat(graph.getNodeArrays()).hasSize(4);
        assertThat(ArrayBasedNode.fromGraph(graph, 0).toArray()).containsSequence(0, 0, -1);
        assertThat(ArrayBasedNode.fromGraph(graph, 0).getNumberOfOutgoingEdges()).isEqualTo(1);
        assertThat(ArrayBasedNode.fromGraph(graph, 0).getNumberOfIncomingEdges()).isEqualTo(0);
    }

    @Test
    void testSinkNodesAreAdded() throws ParseException {
        final String gfa = "S 1 A\nS 2 B\nL 1 + 2 + 0M";
        final Graph graph = parse(gfa);

        assertThat(graph.getNodeArrays()).hasSize(4);
        assertThat(ArrayBasedNode.fromGraph(graph, 3).toArray()).containsSequence(0, 0, -1);
        assertThat(ArrayBasedNode.fromGraph(graph, 3).getNumberOfOutgoingEdges()).isEqualTo(0);
        assertThat(ArrayBasedNode.fromGraph(graph, 3).getNumberOfIncomingEdges()).isEqualTo(1);
    }


    private String replaceSpacesWithTabs(final String string) {
        return string.replaceAll(" ", "\t");
    }

    private Graph parse(final String gfa) throws ParseException {
        final byte[] gfaBytes = replaceSpacesWithTabs(gfa).getBytes(StandardCharsets.UTF_8);
        final GfaFile gfaFile = mock(GfaFile.class);
        when(gfaFile.readFile()).thenAnswer(invocationOnMock ->
                new BufferedReader(new InputStreamReader(new ByteArrayInputStream(gfaBytes)))
        );
        when(gfaFile.getInputStream()).thenAnswer(invocationOnMock ->
                new ByteArrayInputStream(gfaBytes)
        );
        return parser.parse(gfaFile, ProgressUpdater.DUMMY);
    }
}
