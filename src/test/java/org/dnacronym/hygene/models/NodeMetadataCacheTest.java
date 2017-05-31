package org.dnacronym.hygene.models;

import org.dnacronym.hygene.core.HygeneEventBus;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.MetadataParser;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.parser.factories.MetadataParserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for {@link NodeMetadataCache}.
 */
final class NodeMetadataCacheTest {
    private MetadataParser metadataParser;
    private Graph graph;
    private GraphQuery graphQuery;
    private NodeMetadataCache nodeMetaDatacache;


    @BeforeEach
    void setUp() throws ParseException {
        metadataParser = spyMetadataParser();
        graph = createGraph();
        graphQuery = new GraphQuery(graph);
        nodeMetaDatacache = new NodeMetadataCache(graph);

        HygeneEventBus.getInstance().register(nodeMetaDatacache);
    }

    @AfterEach
    void cleanUp() {
        MetadataParserFactory.setInstance(null);
        HygeneEventBus.getInstance().unregister(nodeMetaDatacache);
    }


    @Test
    void testThatNodeMetadataIsCached() throws ParseException, InterruptedException {
        graphQuery.query(2, 0);

        nodeMetaDatacache.getThread().join();

        assertThat(nodeMetaDatacache.has(1)).isFalse();
        assertThat(nodeMetaDatacache.has(2)).isTrue();
        assertThat(nodeMetaDatacache.getOrRetrieve(2).retrieveMetadata().getSequence()).isEqualTo("TCAAGG");

        // Verify that we are not getting metadata via traditional metadata retriever
        verify(metadataParser, never()).parseNodeMetadata(eq(graph.getGfaFile()), anyInt());
    }

    @Test
    void testThatOldNodeMetadataCacheIsRemoved() throws ParseException, InterruptedException {
        graphQuery.query(2, 0);
        graphQuery.query(1, 0);

        nodeMetaDatacache.getThread().join();

        assertThat(nodeMetaDatacache.has(1)).isTrue();
        assertThat(nodeMetaDatacache.has(2)).isFalse();
    }

    @Test
    void testThatNewQueriesDoNotRemoveOldCache() throws ParseException, InterruptedException {
        graphQuery.query(1, 0);

        nodeMetaDatacache.getThread().join();

        final Node node = nodeMetaDatacache.getOrRetrieve(1);

        graphQuery.query(1, 0);

        nodeMetaDatacache.getThread().join();

        assertThat(node == nodeMetaDatacache.getOrRetrieve(1)).isTrue();
    }

    @Test
    void testThatNodesOfLargeRadiusCenterPointQueriesAreNotCached() throws ParseException, InterruptedException {
        graphQuery.query(1, 200);

        assertThat(nodeMetaDatacache.getThread()).isNull();
        assertThat(nodeMetaDatacache.has(1)).isFalse();
    }

    @Test
    void testThatUncachedNodeCanBeRetrieved() throws ParseException, InterruptedException {
        graphQuery.query(1, 0);

        assertThat(nodeMetaDatacache.has(2)).isFalse();
        assertThat(nodeMetaDatacache.getOrRetrieve(2).retrieveMetadata().getSequence()).isEqualTo("TCAAGG");
    }

    private Graph createGraph() throws ParseException {
        GfaFile gfaFile = new GfaFile("src/test/resources/gfa/simple.gfa");
        gfaFile.parse(progress -> {
        });

        return gfaFile.getGraph();
    }

    private MetadataParser spyMetadataParser() {
        final MetadataParser metadataParser = spy(MetadataParser.class);

        MetadataParserFactory.setInstance(metadataParser);

        return metadataParser;
    }
}
