package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@code Edge}s.
 */
class EdgeTest {

    @Test
    void testGetTo() {
        Edge edge = new Edge(1, 2, 42);

        assertThat(edge.getTo()).isEqualTo(2);
    }

    @Test
    void testGetLineNumber() {
        Edge edge = new Edge(4, 3, 12);

        assertThat(edge.getLineNumber()).isEqualTo(12);
    }

    @Test
    void testGetFrom() {
        Edge edge = new Edge(2, 5, 56);

        assertThat(edge.getFrom()).isEqualTo(2);
    }

    @Test
    void testCompareToFirstIsLarger() {
        Edge edge1 = new Edge(4, 5, 16);
        Edge edge2 = new Edge(3, 5, 56);

        assertThat(edge1.compareTo(edge2)).isPositive();
    }

    @Test
    void testCompareToFirstIsSmaller() {
        Edge edge1 = new Edge(3, 5, 16);
        Edge edge2 = new Edge(4, 5, 56);

        assertThat(edge1.compareTo(edge2)).isNegative();
    }

    @Test
    void testCompareToEqual() {
        Edge edge1 = new Edge(1, 10, 1);
        Edge edge2 = new Edge(1, 10, 2);

        assertThat(edge1.compareTo(edge2)).isZero();
    }

    @Test
    void testEqualsSameInstance() {
        Edge edge = new Edge(4, 5, 10);

        assertThat(edge).isEqualTo(edge);
    }

    @Test
    void testEqualsDifferentInstancesSameValues() {
        Edge edge1 = new Edge(6, 12, 2);
        Edge edge2 = new Edge(6, 12, 2);

        assertThat(edge1).isEqualTo(edge2);
    }

    @Test
    void testInstanceNotEqualsNull() {
        Edge edge = new Edge(6, 12, 2);

        assertThat(edge).isNotEqualTo(null);
    }

    @Test
    void testInstanceNotEqualToInstanceOfOtherClass() {
        Edge edge = new Edge(6, 12, 2);

        assertThat(edge).isNotEqualTo("instance-of-other-class");
    }

    @Test
    void testEqualsDifferentFromNode() {
        Edge edge1 = new Edge(1, 0, 0);
        Edge edge2 = new Edge(9, 0, 0);

        assertThat(edge1).isNotEqualTo(edge2);
    }

    @Test
    void testEqualsDifferentToNode() {
        Edge edge1 = new Edge(0, 1, 0);
        Edge edge2 = new Edge(0, 9, 0);

        assertThat(edge1).isNotEqualTo(edge2);
    }

    @Test
    void testEqualsDifferentLineNumber() {
        Edge edge1 = new Edge(0, 0, 1);
        Edge edge2 = new Edge(0, 0, 9);

        assertThat(edge1).isNotEqualTo(edge2);
    }

    @Test
    void testHashCode() {
        Edge edge = new Edge(1, 2, 3);

        assertThat(edge.hashCode()).isEqualTo(31747);
    }
}
