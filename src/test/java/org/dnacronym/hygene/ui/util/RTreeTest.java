package org.dnacronym.hygene.ui.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


/**
 * Tests the RTree integration.
 */
final class RTreeTest {
    @Test
    void testFindNode() {
        RTree tree = new RTree();
        tree.addNode(45, 10, 10, 20, 20);

        tree.find(15, 15,
                nodeId -> assertThat(nodeId).isEqualTo(45),
                (fromNodeId, toNodeId) -> fail("Found an edge, while expecting a node")
        );
    }

    @Test
    void testFindNearNode() {
        RTree tree = new RTree();
        tree.addNode(45, 10, 10, 20, 20);

        tree.find(9, 9,
                nodeId -> assertThat(nodeId).isEqualTo(45),
                (fromNodeId, toNodeId) -> fail("Found an edge, while expecting a node")
        );
    }

    @Test
    void testFindEdge() {
        RTree tree = new RTree();
        tree.addEdge(30, 31, 25, 25, 50, 50);

        tree.find(40, 40,
                nodeId -> fail("Found a node, while expecting an edge"),
                (fromNodeId, toNodeId) -> {
                    assertThat(fromNodeId).isEqualTo(30);
                    assertThat(toNodeId).isEqualTo(31);
                }
        );
    }

    @Test
    void testFindNearEdge() {
        RTree tree = new RTree();
        tree.addEdge(30, 31, 25, 25, 50, 50);

        tree.find(41, 40,
                nodeId -> fail("Found a node, while expecting an edge"),
                (fromNodeId, toNodeId) -> {
                    assertThat(fromNodeId).isEqualTo(30);
                    assertThat(toNodeId).isEqualTo(31);
                }
        );
    }

    @Test
    void testOnlyFindWithinMaxRadius() {
        RTree tree = new RTree();
        tree.addNode(0, 0, 0, 5, 5);

        tree.find(1000, 1000,
                nodeId -> fail("Found a node, while expecting no results"),
                (fromNodeId, toNodeId) -> fail("Found an edge, while expecting no results")
        );
    }
}
