package org.dnacronym.hygene.graph;

import org.dnacronym.hygene.core.UnsignedInteger;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link ArrayBasedEdge}.
 */
final class ArrayBasedEdgeTest {
    @Test
    void testGetTo() {
        final ArrayBasedEdge edge = new ArrayBasedEdge(1, 2, 42, null);

        assertThat(edge.getTo()).isEqualTo(2);
    }

    @Test
    void testGetByteOffset() {
        final ArrayBasedEdge edge = new ArrayBasedEdge(4, 3, UnsignedInteger.fromLong(12), null);

        assertThat(edge.getByteOffset()).isEqualTo(12);
    }

    @Test
    void testGetFrom() {
        final ArrayBasedEdge edge = new ArrayBasedEdge(2, 5, 56, null);

        assertThat(edge.getFrom()).isEqualTo(2);
    }

    @Test
    void testCompareToFirstIsLarger() {
        final ArrayBasedEdge edge1 = new ArrayBasedEdge(4, 5, 16, null);
        final ArrayBasedEdge edge2 = new ArrayBasedEdge(3, 5, 56, null);

        assertThat(edge1.compareTo(edge2)).isPositive();
    }

    @Test
    void testCompareToFirstIsSmaller() {
        final ArrayBasedEdge edge1 = new ArrayBasedEdge(3, 5, 16, null);
        final ArrayBasedEdge edge2 = new ArrayBasedEdge(4, 5, 56, null);

        assertThat(edge1.compareTo(edge2)).isNegative();
    }

    @Test
    void testCompareToEqual() {
        final ArrayBasedEdge edge1 = new ArrayBasedEdge(1, 10, 1, null);
        final ArrayBasedEdge edge2 = new ArrayBasedEdge(1, 10, 2, null);

        assertThat(edge1.compareTo(edge2)).isZero();
    }

    @Test
    void testEqualsSameInstance() {
        final ArrayBasedEdge edge = new ArrayBasedEdge(4, 5, 10, null);

        assertThat(edge).isEqualTo(edge);
    }

    @Test
    void testEqualsDifferentInstancesSameValues() {
        final ArrayBasedEdge edge1 = new ArrayBasedEdge(6, 12, 2, null);
        final ArrayBasedEdge edge2 = new ArrayBasedEdge(6, 12, 2, null);

        assertThat(edge1).isEqualTo(edge2);
    }

    @Test
    void testInstanceNotEqualsNull() {
        final ArrayBasedEdge edge = new ArrayBasedEdge(6, 12, 2, null);

        assertThat(edge).isNotEqualTo(null);
    }

    @Test
    void testInstanceNotEqualToInstanceOfOtherClass() {
        final ArrayBasedEdge edge = new ArrayBasedEdge(6, 12, 2, null);

        assertThat(edge).isNotEqualTo("instance-of-other-class");
    }

    @Test
    void testEqualsDifferentFromNode() {
        final ArrayBasedEdge edge1 = new ArrayBasedEdge(1, 0, 0, null);
        final ArrayBasedEdge edge2 = new ArrayBasedEdge(9, 0, 0, null);

        assertThat(edge1).isNotEqualTo(edge2);
    }

    @Test
    void testEqualsDifferentToNode() {
        final ArrayBasedEdge edge1 = new ArrayBasedEdge(0, 1, 0, null);
        final ArrayBasedEdge edge2 = new ArrayBasedEdge(0, 9, 0, null);

        assertThat(edge1).isNotEqualTo(edge2);
    }

    @Test
    void testEqualsDifferentByteOffset() {
        final ArrayBasedEdge edge1 = new ArrayBasedEdge(0, 0, 1, null);
        final ArrayBasedEdge edge2 = new ArrayBasedEdge(0, 0, 9, null);

        assertThat(edge1).isNotEqualTo(edge2);
    }

    @Test
    void testHashCode() {
        final ArrayBasedEdge edge = new ArrayBasedEdge(1, 2, 3, null);

        assertThat(edge.hashCode()).isEqualTo(31747);
    }
}
