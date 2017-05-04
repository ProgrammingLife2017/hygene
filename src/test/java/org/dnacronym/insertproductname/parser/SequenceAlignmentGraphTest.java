package org.dnacronym.insertproductname.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@code SequenceAlignmentGraph}s.
 */
class SequenceAlignmentGraphTest {
    private SequenceAlignmentGraph sag;


    @BeforeEach
    void beforeEach() {
        sag = new SequenceAlignmentGraph();
    }


    @Test
    void testGetSegmentNull() {
        final Throwable e = catchThrowable(() -> sag.getSegment("yBzxeLyGuiioMD3oGNDD"));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testAddAndGetSegment() throws ParseException {
        final Segment segment = mock(Segment.class);
        when(segment.getName()).thenReturn("wMGSp96y6eFewU4XK8fu");

        sag.addSegment(segment);

        assertThat(sag.getSegment("wMGSp96y6eFewU4XK8fu")).isEqualTo(segment);
    }

    @Test
    void testAddSegmentDuplicate() throws ParseException {
        final String sequence = "i9lmsQajqPINZ66wnZCB";
        final Segment segmentA = mock(Segment.class);
        final Segment segmentB = mock(Segment.class);
        when(segmentA.getName()).thenReturn(sequence);
        when(segmentB.getName()).thenReturn(sequence);

        sag.addSegment(segmentA);
        sag.addSegment(segmentB);

        assertThat(sag.getSegment("i9lmsQajqPINZ66wnZCB")).isEqualTo(segmentB);
    }

    @Test
    void testGetLinksEmpty() {
        assertThat(sag.getLinks()).isEmpty();
    }

    @Test
    void testAddAndGetLink() {
        final Link link = mock(Link.class);

        sag.addLink(link);

        assertThat(sag.getLinks().size()).isEqualTo(1);
        assertThat(sag.getLinks().get(0)).isEqualTo(link);
    }

    @Test
    void testAddLinkDouble() {
        final Link link = mock(Link.class);

        sag.addLink(link);
        sag.addLink(link);

        assertThat(sag.getLinks().size()).isEqualTo(2);
    }
}
