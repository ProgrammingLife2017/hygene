package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class NodeTest {
    @Test
    void testGetId() {
        Node node = NodeBuilder.start().withNodeId(5).create();

        assertThat(node.getId()).isEqualTo(5);
    }

    @Test
    void testGetLineNumber() {
        Node node = NodeBuilder.start().withLineNumber(4).create();

        assertThat(node.getLineNumber()).isEqualTo(4);
    }

    @Test
    void testGetColor() {
        Node node = NodeBuilder.start().withColor(NodeColor.GREEN).create();

        assertThat(node.getColor()).isEqualTo(NodeColor.GREEN);
    }

    @Test
    void testGetUnscaledXPosition() {
        Node node = NodeBuilder.start().withUnscaledXPosition(5).create();

        assertThat(node.getUnscaledXPosition()).isEqualTo(5);
    }

    @Test
    void testGetUnscaledYPosition() {
        Node node = NodeBuilder.start().withUnscaledYPosition(6).create();

        assertThat(node.getUnscaledYPosition()).isEqualTo(6);
    }

    @Test
    void testToArray() {
        Node node = NodeBuilder.start()
                .withNodeId(42)
                .withLineNumber(1)
                .withColor(NodeColor.BLUE)
                .withUnscaledXPosition(3)
                .withUnscaledYPosition(4)
                .create();

        assertThat(node.toArray()).isEqualTo(new int[]{
                1, 2, 3, 4, 0
        });
    }

    @Test
    void testGetNumberOfOutgoingEdges() {
        Node node = NodeBuilder.start().create();

        assertThat(node.getNumberOfOutgoingEdges()).isEqualTo(0);
    }
}
