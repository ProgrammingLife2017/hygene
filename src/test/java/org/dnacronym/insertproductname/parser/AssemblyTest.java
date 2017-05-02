package org.dnacronym.insertproductname.parser;

import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@code Assembly}s.
 */
class AssemblyTest {
    private Assembly assembly;


    @BeforeEach
    void beforeEach() {
        assembly = new Assembly();
    }


    @Test
    void testGetSegmentNull() {
        final Throwable e = catchThrowable(() -> assembly.getSegment("yBzxeLyGuiioMD3oGNDD"));
        assertThat(e).isInstanceOf(ParseException.class);
    }

    @Test
    void testAddAndGetSegment() throws ParseException {
        final Segment segment = mock(Segment.class);
        when(segment.getName()).thenReturn("wMGSp96y6eFewU4XK8fu");

        assembly.addSegment(segment);

        assertThat(assembly.getSegment("wMGSp96y6eFewU4XK8fu")).isEqualTo(segment);
    }

    @Test
    void testAddSegmentDuplicate() throws ParseException {
        final String sequence = "i9lmsQajqPINZ66wnZCB";
        final Segment segmentA = mock(Segment.class);
        final Segment segmentB = mock(Segment.class);
        when(segmentA.getName()).thenReturn(sequence);
        when(segmentB.getName()).thenReturn(sequence);

        assembly.addSegment(segmentA);
        assembly.addSegment(segmentB);

        assertThat(assembly.getSegment("i9lmsQajqPINZ66wnZCB")).isEqualTo(segmentB);
    }

    @Test
    void testGetLinksEmpty() {
        assertThat(assembly.getLinks()).isEmpty();
    }

    @Test
    void testAddAndGetLink() {
        final Link link = mock(Link.class);

        assembly.addLink(link);

        assertThat(assembly.getLinks().size()).isEqualTo(1);
        assertThat(assembly.getLinks().get(0)).isEqualTo(link);
    }

    @Test
    void testAddLinkDouble() {
        final Link link = mock(Link.class);

        assembly.addLink(link);
        assembly.addLink(link);

        assertThat(assembly.getLinks().size()).isEqualTo(2);
    }
}
