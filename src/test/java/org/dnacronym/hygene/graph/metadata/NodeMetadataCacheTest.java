package org.dnacronym.hygene.graph.metadata;

import org.dnacronym.hygene.core.HygeneEventBus;
import org.dnacronym.hygene.event.CenterPointQueryChangeEvent;
import org.dnacronym.hygene.event.LayoutDoneEvent;
import org.dnacronym.hygene.graph.CenterPointQuery;
import org.dnacronym.hygene.graph.Subgraph;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.GfaParseException;
import org.dnacronym.hygene.parser.MetadataParseException;
import org.dnacronym.hygene.parser.MetadataParser;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.dnacronym.hygene.parser.factories.MetadataParserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link NodeMetadataCache}.
 */
final class NodeMetadataCacheTest {
    private static final String TEST_GRAPH_FILE = "src/test/resources/gfa/simple.gfa";

    private MetadataParser parser;
    private GfaFile gfaFile;
    private NodeMetadataCache cache;
    @Captor
    private ArgumentCaptor<Map<Integer, Long>> captor;


    @BeforeEach
    void beforeEach() throws GfaParseException {
        MockitoAnnotations.initMocks(this);

        parser = spyMetadataParser();
        gfaFile = createGraph();
        cache = new NodeMetadataCache(gfaFile);

        HygeneEventBus.getInstance().register(cache);
    }

    @AfterEach
    void afterEach() {
        MetadataParserFactory.setInstance(null);
        HygeneEventBus.getInstance().unregister(cache);
    }


    @Test
    void testEmptySubgraph() throws MetadataParseException, GfaParseException {
        final Subgraph subgraph = new Subgraph();

        cache.layoutDone(new LayoutDoneEvent(subgraph));
        cache.getRetrievalExecutor().block();

        verify(parser).parseNodeMetadata(eq(gfaFile), captor.capture());
        assertThat(captor.getValue()).isEmpty();
    }

    @Test
    void testSingleSegment() throws MetadataParseException, GfaParseException {
        final Subgraph subgraph = new Subgraph();
        final Segment segment = new Segment(2, 69, 6);
        subgraph.add(segment);

        cache.layoutDone(new LayoutDoneEvent(subgraph));
        cache.getRetrievalExecutor().block();

        verify(parser).parseNodeMetadata(eq(gfaFile), captor.capture());
        assertThat(captor.getValue().keySet()).containsExactly(2);
        assertThat(captor.getValue().values()).containsExactly(69L);
    }

    @Test
    void testTwoSegments() throws MetadataParseException, GfaParseException {
        final Subgraph subgraph = new Subgraph();
        final Segment segment1 = new Segment(1, 40, 5);
        final Segment segment2 = new Segment(2, 69, 6);
        subgraph.add(segment1);
        subgraph.add(segment2);

        cache.layoutDone(new LayoutDoneEvent(subgraph));
        cache.getRetrievalExecutor().block();

        verify(parser).parseNodeMetadata(eq(gfaFile), captor.capture());
        assertThat(captor.getValue().keySet()).containsExactly(1, 2);
        assertThat(captor.getValue().values()).containsExactly(40L, 69L);
    }

    @Test
    void testLargeRadius() {
        final CenterPointQuery cpq = mock(CenterPointQuery.class);
        when(cpq.getRadius()).thenReturn(500);
        cache.centerPointQueryChanged(new CenterPointQueryChangeEvent(cpq));

        cache.layoutDone(new LayoutDoneEvent(mock(Subgraph.class)));
        cache.getRetrievalExecutor().block();

        verifyNoMoreInteractions(parser);
    }

    @Test
    void testSingleSegmentHasMetadata() {
        final Subgraph subgraph = new Subgraph();
        final Segment segment = new Segment(2, 66, 6);
        subgraph.add(segment);

        cache.layoutDone(new LayoutDoneEvent(subgraph));
        cache.getRetrievalExecutor().block();

        assertThat(segment.hasMetadata()).isTrue();
    }

    @Test
    void testInvalidByteOffset() {
        final Subgraph subgraph = new Subgraph();
        final Segment segment = new Segment(2, 504, 6);
        subgraph.add(segment);

        cache.layoutDone(new LayoutDoneEvent(subgraph));
        cache.getRetrievalExecutor().block();

        assertThat(segment.hasMetadata()).isFalse();
    }

    /**
     * Tests that metadata is not retrieved twice if its metadata is put in the cache.
     *
     * @throws GfaParseException if the metadata could not be parser
     */
    @Test
    void testRetrieveTwice() throws MetadataParseException, GfaParseException {
        final Subgraph subgraph = new Subgraph();
        final Segment segment = new Segment(1, 38, 5);
        subgraph.add(segment);

        cache.layoutDone(new LayoutDoneEvent(subgraph));
        cache.getRetrievalExecutor().block();
        reset(parser);
        cache.layoutDone(new LayoutDoneEvent(subgraph));
        cache.getRetrievalExecutor().block();

        verify(parser).parseNodeMetadata(eq(gfaFile), captor.capture());
        assertThat(captor.getValue()).isEmpty();
    }

    /**
     * Tests that the merge constructor creates an empty metadata object.
     */
    @Test
    void testMergeConstructorEmptyCollection() {
        final List<NodeMetadata> metadataList = new ArrayList<>();

        final NodeMetadata mergedMetadata = new NodeMetadata(metadataList);

        assertThat(mergedMetadata.getName()).isEqualTo("[]");
        assertThat(mergedMetadata.getSequence()).isEqualTo("[]");
        assertThat(mergedMetadata.getGenomes()).isEmpty();
    }

    @Test
    void testMergeConstructorSingleElementNoGenomes() {
        final List<NodeMetadata> metadataList = new ArrayList<>();
        final NodeMetadata metadata = new NodeMetadata("name", "sequence", new ArrayList<>());
        metadataList.add(metadata);

        final NodeMetadata mergedMetadata = new NodeMetadata(metadataList);

        assertThat(mergedMetadata.getName()).isEqualTo("[name]");
        assertThat(mergedMetadata.getSequence()).isEqualTo("[sequence]");
        assertThat(mergedMetadata.getGenomes()).isEmpty();
    }

    @Test
    void testMergeConstructorSingleElementWithGenomes() {
        final List<NodeMetadata> metadataList = new ArrayList<>();
        final NodeMetadata metadata = new NodeMetadata("name", "sequence", Arrays.asList("genA", "genB"));
        metadataList.add(metadata);

        final NodeMetadata mergedMetadata = new NodeMetadata(metadataList);

        assertThat(mergedMetadata.getName()).isEqualTo("[name]");
        assertThat(mergedMetadata.getSequence()).isEqualTo("[sequence]");
        assertThat(mergedMetadata.getGenomes()).containsExactly("genA", "genB");
    }

    @Test
    void testMergeConstructorMultipleElementsWithoutDuplicates() {
        final List<NodeMetadata> metadataList = new ArrayList<>();
        final NodeMetadata metadataA = new NodeMetadata("nameA", "sequenceA", Arrays.asList("genA", "genB"));
        final NodeMetadata metadataB = new NodeMetadata("nameB", "sequenceB", Arrays.asList("genC", "genD"));
        metadataList.add(metadataA);
        metadataList.add(metadataB);

        final NodeMetadata mergedMetadata = new NodeMetadata(metadataList);

        assertThat(mergedMetadata.getName()).isEqualTo("[nameA, nameB]");
        assertThat(mergedMetadata.getSequence()).isEqualTo("[sequenceA, sequenceB]");
        assertThat(mergedMetadata.getGenomes()).containsExactly("genA", "genB", "genC", "genD");
    }

    @Test
    void testMergeConstructorMultipleElementsWithDuplicates() {
        final List<NodeMetadata> metadataList = new ArrayList<>();
        final NodeMetadata metadataA = new NodeMetadata("nameA", "sequenceA", Arrays.asList("genA", "genB"));
        final NodeMetadata metadataB = new NodeMetadata("nameB", "sequenceB", Arrays.asList("genB", "genC"));
        metadataList.add(metadataA);
        metadataList.add(metadataB);

        final NodeMetadata mergedMetadata = new NodeMetadata(metadataList);

        assertThat(mergedMetadata.getName()).isEqualTo("[nameA, nameB]");
        assertThat(mergedMetadata.getSequence()).isEqualTo("[sequenceA, sequenceB]");
        assertThat(mergedMetadata.getGenomes()).containsExactly("genA", "genB", "genC");
    }


    /**
     * Creates a new {@link GfaFile}.
     *
     * @return
     * @throws GfaParseException
     */
    private GfaFile createGraph() throws GfaParseException {
        final GfaFile gfaFile = new GfaFile(TEST_GRAPH_FILE);
        gfaFile.parse(ProgressUpdater.DUMMY);

        return gfaFile;
    }

    /**
     * Creates a spy of a {@link MetadataParser} and sets it as the preferred instance of the
     * {@link MetadataParserFactory}.
     *
     * @return a spy of a {@link MetadataParser}
     */
    private MetadataParser spyMetadataParser() {
        final MetadataParser metadataParser = spy(MetadataParser.class);

        MetadataParserFactory.setInstance(metadataParser);

        return metadataParser;
    }
}
