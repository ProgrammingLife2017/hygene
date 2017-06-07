package org.dnacronym.hygene.models;

import org.dnacronym.hygene.core.HygeneEventBus;
import org.dnacronym.hygene.graph.CenterPointQuery;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.MetadataParser;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.dnacronym.hygene.parser.factories.MetadataParserFactory;
import org.dnacronym.hygene.persistence.FileDatabaseDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private static final String TEST_GRAPH_FILE = "src/test/resources/gfa/simple.gfa";

    private MetadataParser metadataParser;
    private Graph graph;
    private CenterPointQuery query;
    private NodeMetadataCache nodeMetaDatacache;


    @BeforeEach
    void setUp() throws ParseException {
        metadataParser = spyMetadataParser();
        graph = createGraph();
        query = new CenterPointQuery(graph);
        nodeMetaDatacache = new NodeMetadataCache(graph);

        HygeneEventBus.getInstance().register(nodeMetaDatacache);
    }

    @AfterEach
    void cleanUp() throws IOException {
        MetadataParserFactory.setInstance(null);
        HygeneEventBus.getInstance().unregister(nodeMetaDatacache);
        Files.deleteIfExists(Paths.get(TEST_GRAPH_FILE + FileDatabaseDriver.DB_FILE_EXTENSION));
    }


    @Test
    void testThatNodeMetadataIsCached() throws ParseException, InterruptedException {
        query.query(2, 0);

        nodeMetaDatacache.getThread().join();

        assertThat(nodeMetaDatacache.has(1)).isFalse();
        assertThat(nodeMetaDatacache.has(2)).isTrue();
        assertThat(nodeMetaDatacache.getOrRetrieve(2).retrieveMetadata().getSequence()).isEqualTo("TCAAGG");

        // Verify that we are not getting metadata via traditional metadata retriever
        verify(metadataParser, never()).parseNodeMetadata(eq(graph.getGfaFile()), anyInt());
    }

    @Test
    void testThatNodeMetadataIsCachedOnlyOnce() throws ParseException, InterruptedException {
        query.query(2, 0);
        nodeMetaDatacache.getThread().join();

        query.query(2, 0);
        nodeMetaDatacache.getThread().join();

        assertThat(nodeMetaDatacache.has(2)).isTrue();

        verify(metadataParser, times(1)).parseNodeMetadata(
                eq(graph.getGfaFile()), any(Map.class));
    }

    @Test
    void testThatOldNodeMetadataCacheIsRemoved() throws ParseException, InterruptedException {
        query.query(2, 0);
        query.query(1, 0);

        nodeMetaDatacache.getThread().join();

        assertThat(nodeMetaDatacache.has(1)).isTrue();
        assertThat(nodeMetaDatacache.has(2)).isFalse();
    }

    @Test
    void testThatNewQueriesDoNotRemoveOldCache() throws ParseException, InterruptedException {
        query.query(1, 0);

        nodeMetaDatacache.getThread().join();

        final Node node = nodeMetaDatacache.getOrRetrieve(1);

        query.query(1, 0);

        nodeMetaDatacache.getThread().join();

        assertThat(node == nodeMetaDatacache.getOrRetrieve(1)).isTrue();
    }

    @Test
    void testThatNodesOfLargeRadiusCenterPointQueriesAreNotCached() throws ParseException, InterruptedException {
        query.query(1, 200);

        assertThat(nodeMetaDatacache.getThread()).isNull();
        assertThat(nodeMetaDatacache.has(1)).isFalse();
    }

    @Test
    void testThatUncachedNodeCanBeRetrieved() throws ParseException, InterruptedException {
        query.query(1, 0);

        assertThat(nodeMetaDatacache.has(2)).isFalse();
        assertThat(nodeMetaDatacache.getOrRetrieve(2).retrieveMetadata().getSequence()).isEqualTo("TCAAGG");
    }

    private Graph createGraph() throws ParseException {
        GfaFile gfaFile = new GfaFile(TEST_GRAPH_FILE);
        gfaFile.parse(ProgressUpdater.DUMMY);

        return gfaFile.getGraph();
    }

    private MetadataParser spyMetadataParser() {
        final MetadataParser metadataParser = spy(MetadataParser.class);

        MetadataParserFactory.setInstance(metadataParser);

        return metadataParser;
    }
}
