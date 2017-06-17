package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.graph.ArrayBasedEdge;
import org.dnacronym.hygene.graph.metadata.EdgeMetadata;
import org.dnacronym.hygene.graph.ArrayBasedNode;
import org.dnacronym.hygene.graph.metadata.NodeMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests the integration between {@link MetadataParser}, {@link GfaFile}, {@link ArrayBasedNode} and {@link ArrayBasedEdge}.
 */
final class MetadataParsingIntegrationTest {
    @Test
    void testGetNodeMetadata() throws ParseException {
        final GfaFile gfaFile = new GfaFile("src/test/resources/gfa/simple.gfa");
        gfaFile.parse(ProgressUpdater.DUMMY);

        final ArrayBasedNode node = ArrayBasedNode.fromGraph(gfaFile.getGraph(), 1);
        final NodeMetadata nodeMetadata = gfaFile.parseNodeMetadata(node.getByteOffset());

        assertThat(nodeMetadata.getSequence()).isEqualTo("ACCTT");
        assertThat(nodeMetadata.getName()).isEqualTo("11");
    }

    @Test
    void testGetEdgeMetadata() throws ParseException {
        final GfaFile gfaFile = new GfaFile("src/test/resources/gfa/simple.gfa");
        gfaFile.parse(ProgressUpdater.DUMMY);

        final ArrayBasedEdge edge = ArrayBasedNode.fromGraph(gfaFile.getGraph(), 1).getOutgoingEdges().iterator().next();
        final EdgeMetadata edgeMetadata = gfaFile.parseEdgeMetadata(edge.getByteOffset());

        assertThat(edgeMetadata.getFromOrient()).isEqualTo("+");
        assertThat(edgeMetadata.getToOrient()).isEqualTo("-");
        assertThat(edgeMetadata.getOverlap()).isEqualTo("4M");
    }
}
