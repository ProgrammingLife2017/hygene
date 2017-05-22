package org.dnacronym.hygene.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link SequenceAlignmentGraph}s.
 */
final class SequenceAlignmentGraphTest {
    private SequenceAlignmentGraph graph;


    @BeforeEach
    void beforeEach() {
        graph = new SequenceAlignmentGraph();
    }


    @Test
    void testGetSegmentNull() {
        final Throwable e = catchThrowable(() -> graph.getSegment("yBzxeLyGuiioMD3oGNDD"));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testAddAndGetSegment() throws ParseException {
        final Segment segment = mock(Segment.class);
        when(segment.getName()).thenReturn("wMGSp96y6eFewU4XK8fu");

        graph.addSegment(segment);

        assertThat(graph.getSegment("wMGSp96y6eFewU4XK8fu")).isEqualTo(segment);
    }

    @Test
    void testAddSegmentDuplicate() throws ParseException {
        final String sequence = "i9lmsQajqPINZ66wnZCB";
        final Segment segmentA = mock(Segment.class);
        final Segment segmentB = mock(Segment.class);
        when(segmentA.getName()).thenReturn(sequence);
        when(segmentB.getName()).thenReturn(sequence);

        graph.addSegment(segmentA);
        graph.addSegment(segmentB);

        assertThat(graph.getSegment("i9lmsQajqPINZ66wnZCB")).isEqualTo(segmentB);
    }

    @Test
    void testGetLinksEmpty() {
        assertThat(graph.getLinks()).isEmpty();
    }

    @Test
    void testAddAndGetLink() {
        final Link link = mock(Link.class);

        graph.addLink(link);

        assertThat(graph.getLinks().size()).isEqualTo(1);
        assertThat(graph.getLinks().get(0)).isEqualTo(link);
    }

    @Test
    void testAddLinkDouble() {
        final Link link = mock(Link.class);

        graph.addLink(link);
        graph.addLink(link);

        assertThat(graph.getLinks().size()).isEqualTo(2);
    }
}
