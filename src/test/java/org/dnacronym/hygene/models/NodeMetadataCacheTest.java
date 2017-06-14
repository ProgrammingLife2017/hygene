package org.dnacronym.hygene.models;

import org.dnacronym.hygene.core.HygeneEventBus;
import org.dnacronym.hygene.events.CenterPointQueryChangeEvent;
import org.dnacronym.hygene.events.LayoutDoneEvent;
import org.dnacronym.hygene.graph.CenterPointQuery;
import org.dnacronym.hygene.graph.Segment;
import org.dnacronym.hygene.graph.Subgraph;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.MetadataParser;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.dnacronym.hygene.parser.factories.MetadataParserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
    private ArgumentCaptor<Map<Integer, Long>> captor;


    @BeforeEach
    void beforeEach() throws ParseException {
        parser = spyMetadataParser();
        gfaFile = createGraph();
        cache = new NodeMetadataCache(gfaFile);
        captor = ArgumentCaptor.forClass(Map.class);

        HygeneEventBus.getInstance().register(cache);
    }

    @AfterEach
    void afterEach() {
        MetadataParserFactory.setInstance(null);
        HygeneEventBus.getInstance().unregister(cache);
    }


    @Test
    void testEmptySubgraph() throws ParseException {
        final Subgraph subgraph = new Subgraph();

        cache.layoutDone(new LayoutDoneEvent(subgraph));
        cache.getRetrievalExecutor().block();

        verify(parser).parseNodeMetadata(eq(gfaFile), captor.capture());
        assertThat(captor.getValue()).isEmpty();
    }

    @Test
    void testSingleSegment() throws ParseException {
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
    void testTwoSegments() throws ParseException {
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
     * @throws ParseException if the metadata could not be parser
     */
    @Test
    void testRetrieveTwice() throws ParseException {
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
     * Creates a new {@link GfaFile}
     *
     * @return
     * @throws ParseException
     */
    private GfaFile createGraph() throws ParseException {
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
