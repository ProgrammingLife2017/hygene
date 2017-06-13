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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.spy;


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
        nodeMetaDatacache = new NodeMetadataCache(graph.getGfaFile());

        HygeneEventBus.getInstance().register(nodeMetaDatacache);
    }

    @AfterEach
    void cleanUp() throws IOException {
        MetadataParserFactory.setInstance(null);
        HygeneEventBus.getInstance().unregister(nodeMetaDatacache);
        Files.deleteIfExists(Paths.get(TEST_GRAPH_FILE + FileDatabaseDriver.DB_FILE_EXTENSION));
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
