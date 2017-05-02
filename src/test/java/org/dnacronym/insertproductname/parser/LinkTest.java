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
        final Throwable e = catchThrowable(() -> new Link(null, "to", 341));
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNullTo() {
        final Throwable e = catchThrowable(() -> new Link("from", null, 884));
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNegativeOverlap() {
        final Throwable e = catchThrowable(() -> new Link("from", "to", -426));
        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGetFrom() {
        final Link link = new Link("from", "to", 973);
        assertThat(link.getFrom()).isEqualTo("from");
    }

    @Test
    void testGetTo() {
        final Link link = new Link("from", "to", 5);
        assertThat(link.getTo()).isEqualTo("to");
    }

    @Test
    void testGetOverlap() {
        final Link link = new Link("from", "to", 649);
        assertThat(link.getOverlap()).isEqualTo(649);
    }
}
