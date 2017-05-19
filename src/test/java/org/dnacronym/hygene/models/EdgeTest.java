package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link Edge}s.
 */
class EdgeTest {

    @Test
    void testGetTo() {
        final Edge edge = new Edge(1, 2, 42, null);

        assertThat(edge.getTo()).isEqualTo(2);
    }

    @Test
    void testGetLineNumber() {
        final Edge edge = new Edge(4, 3, 12, null);

        assertThat(edge.getLineNumber()).isEqualTo(12);
    }

    @Test
    void testGetFrom() {
        final Edge edge = new Edge(2, 5, 56, null);

        assertThat(edge.getFrom()).isEqualTo(2);
    }

    @Test
    void testCompareToFirstIsLarger() {
        final Edge edge1 = new Edge(4, 5, 16, null);
        final Edge edge2 = new Edge(3, 5, 56, null);

        assertThat(edge1.compareTo(edge2)).isPositive();
    }

    @Test
    void testCompareToFirstIsSmaller() {
        final Edge edge1 = new Edge(3, 5, 16, null);
        final Edge edge2 = new Edge(4, 5, 56, null);

        assertThat(edge1.compareTo(edge2)).isNegative();
    }

    @Test
    void testCompareToEqual() {
        final Edge edge1 = new Edge(1, 10, 1, null);
        final Edge edge2 = new Edge(1, 10, 2, null);

        assertThat(edge1.compareTo(edge2)).isZero();
    }

    @Test
    void testEqualsSameInstance() {
        final Edge edge = new Edge(4, 5, 10, null);

        assertThat(edge).isEqualTo(edge);
    }

    @Test
    void testEqualsDifferentInstancesSameValues() {
        final Edge edge1 = new Edge(6, 12, 2, null);
        final Edge edge2 = new Edge(6, 12, 2, null);

        assertThat(edge1).isEqualTo(edge2);
    }

    @Test
    void testInstanceNotEqualsNull() {
        final Edge edge = new Edge(6, 12, 2, null);

        assertThat(edge).isNotEqualTo(null);
    }

    @Test
    void testInstanceNotEqualToInstanceOfOtherClass() {
        final Edge edge = new Edge(6, 12, 2, null);

        assertThat(edge).isNotEqualTo("instance-of-other-class");
    }

    @Test
    void testEqualsDifferentFromNode() {
        final Edge edge1 = new Edge(1, 0, 0, null);
        final Edge edge2 = new Edge(9, 0, 0, null);

        assertThat(edge1).isNotEqualTo(edge2);
    }

    @Test
    void testEqualsDifferentToNode() {
        final Edge edge1 = new Edge(0, 1, 0, null);
        final Edge edge2 = new Edge(0, 9, 0, null);

        assertThat(edge1).isNotEqualTo(edge2);
    }

    @Test
    void testEqualsDifferentLineNumber() {
        final Edge edge1 = new Edge(0, 0, 1, null);
        final Edge edge2 = new Edge(0, 0, 9, null);

        assertThat(edge1).isNotEqualTo(edge2);
    }

    @Test
    void testHashCode() {
        final Edge edge = new Edge(1, 2, 3, null);

        assertThat(edge.hashCode()).isEqualTo(31747);
    }
}
