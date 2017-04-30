package org.dnacronym.insertproductname.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@code Link}s.
 */
final class LinkTest {
    private Segment from;
    private Segment to;


    @BeforeEach
    void beforeEach() {
        from = mock(Segment.class);
        to = mock(Segment.class);

        when(from.getLength()).thenReturn(1000);
        when(to.getLength()).thenReturn(1000);
    }


    @Test
    void testNullFrom() {
        final Throwable e = catchThrowable(() -> new Link(null, true, to, true, 341));
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNullTo() {
        final Throwable e = catchThrowable(() -> new Link(from, false, null, true, 884));
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNegativeOverlap() {
        final Throwable e = catchThrowable(() -> new Link(from, true, to, false, -426));
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testExcessiveOverlap() {
        final Throwable e = catchThrowable(() -> new Link(from, false, to, true, 1001));
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGetFrom() {
        final Link link = new Link(from, true, to, false, 973);
        assertThat(link.getFrom()).isEqualTo(from);
    }

    @Test
    void testGetFromOrient() {
        final Link link = new Link(from, false, to, false, 790);
        assertThat(link.getFromOrient()).isEqualTo(false);
    }

    @Test
    void testGetTo() {
        final Link link = new Link(from, true, to, true, 5);
        assertThat(link.getTo()).isEqualTo(to);
    }

    @Test
    void testGetToOrient() {
        final Link link = new Link(from, false, to, false, 707);
        assertThat(link.getToOrient()).isEqualTo(false);
    }

    @Test
    void testGetOverlap() {
        final Link link = new Link(from, true, to, true, 649);
        assertThat(link.getOverlap()).isEqualTo(649);
    }
}
