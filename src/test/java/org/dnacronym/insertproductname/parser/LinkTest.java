package org.dnacronym.insertproductname.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for {@code Link}s.
 */
final class LinkTest {
    @Test
    void testNullFrom() {
        final Throwable e = catchThrowable(() -> new Link(null, true, "to", true, 341));
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNullTo() {
        final Throwable e = catchThrowable(() -> new Link("from", false, null, true, 884));
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNegativeOverlap() {
        final Throwable e = catchThrowable(() -> new Link("from", true, "to", false, -426));
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGetFrom() {
        final Link link = new Link("from", true, "to", false, 973);
        assertThat(link.getFrom()).isEqualTo("from");
    }

    @Test
    void testGetFromOrient() {
        final Link link = new Link("from", false, "to", false, 790);
        assertThat(link.getFromOrient()).isEqualTo(false);
    }

    @Test
    void testGetTo() {
        final Link link = new Link("from", true, "to", true, 5);
        assertThat(link.getTo()).isEqualTo("to");
    }

    @Test
    void testGetToOrient() {
        final Link link = new Link("from", false, "to", false, 707);
        assertThat(link.getToOrient()).isEqualTo(false);
    }

    @Test
    void testGetOverlap() {
        final Link link = new Link("from", true, "to", true, 649);
        assertThat(link.getOverlap()).isEqualTo(649);
    }
}
