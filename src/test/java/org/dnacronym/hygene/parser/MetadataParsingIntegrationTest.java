package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.models.Edge;
import org.dnacronym.hygene.models.EdgeMetadata;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.models.NodeMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests the integration between {@link MetadataParser}, {@link GfaFile}, {@link Node} and {@link Edge}.
 */
final class MetadataParsingIntegrationTest {
    @Test
    void testGetNodeMetadata() throws ParseException {
        final GfaFile gfaFile = new GfaFile("src/test/resources/gfa/simple.gfa");
        gfaFile.parse(ProgressUpdater.DUMMY);

        final Node node = gfaFile.getGraph().getNode(1);
        final NodeMetadata nodeMetadata = gfaFile.parseNodeMetadata(node.getByteOffset());

        assertThat(nodeMetadata.getSequence()).isEqualTo("ACCTT");
        assertThat(nodeMetadata.getName()).isEqualTo("11");
    }

    @Test
    void testGetEdgeMetadata() throws ParseException {
        final GfaFile gfaFile = new GfaFile("src/test/resources/gfa/simple.gfa");
        gfaFile.parse(ProgressUpdater.DUMMY);

        final Edge edge = gfaFile.getGraph().getNode(1).getOutgoingEdges().iterator().next();
        final EdgeMetadata edgeMetadata = edge.retrieveMetadata();

        assertThat(edgeMetadata.getFromOrient()).isEqualTo("+");
        assertThat(edgeMetadata.getToOrient()).isEqualTo("-");
        assertThat(edgeMetadata.getOverlap()).isEqualTo("4M");
    }
}
