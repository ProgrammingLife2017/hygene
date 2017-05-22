package org.dnacronym.hygene.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link NodeDistanceMap}s.
 */
class NodeDistanceMapTest {
    private NodeDistanceMap map;


    @BeforeEach
    void beforeEach() {
        map = new NodeDistanceMap();
    }


    /*
     * size
     */

    @Test
    void testSizeEmpty() {
        assertThat(map.size()).isEqualTo(0);
    }

    @Test
    void testSizeOne() {
        map.setDistance(903, 185);

        assertThat(map.size()).isEqualTo(1);
    }

    @Test
    void testSizeTwo() {
        map.setDistance(949, 184);
        map.setDistance(627, 932);

        assertThat(map.size()).isEqualTo(2);
    }


    /*
     * isEmpty
     */

    @Test
    void testIsEmptyTrue() {
        assertThat(map.isEmpty()).isTrue();
    }

    @Test
    void testIsEmptyFalse() {
        map.setDistance(57, 756);

        assertThat(map.isEmpty()).isFalse();
    }


    /*
     * containsNode
     */

    @Test
    void testContainsNodeFalse() {
        assertThat(map.containsNode(203)).isFalse();
    }

    @Test
    void testContainsNodeTrue() {
        map.setDistance(511, 347);

        assertThat(map.containsNode(511)).isTrue();
    }

    @Test
    void testContainsNodeRemoved() {
        map.setDistance(540, 105);
        map.removeNode(540);

        assertThat(map.containsNode(540)).isFalse();
    }


    /*
     * getDistance
     */

    @Test
    void testGetSetDistanceInvalidNode() {
        assertThat(map.getDistance(895)).isNull();
    }

    @Test
    void testGetSetDistanceNormal() {
        map.setDistance(681, 731);

        assertThat(map.getDistance(681)).isEqualTo(731);
    }

    @Test
    void testGetSetDistanceUpdate() {
        map.setDistance(111, 471);
        map.setDistance(111, 368);

        assertThat(map.getDistance(111)).isEqualTo(368);
    }

    @Test
    void testGetSetDistanceOverwrite() {
        map.setDistance(923, 316);
        map.setDistance(923, 115);

        assertThat(map.getNodes(316)).doesNotContain(923);
    }


    /*
     * getNodes
     */

    @Test
    void testGetNodesEmpty() {
        assertThat(map.getNodes(317)).isEmpty();
    }

    @Test
    void testGetNodesContents() {
        map.setDistance(439, 822);
        map.setDistance(785, 822);

        assertThat(map.getNodes(822)).containsExactlyInAnyOrder(439, 785);
    }


    /*
     * removeNode
     */

    @Test
    void testRemoveNodeInvalidNode() {
        assertThat(map.removeNode(530)).isNull();
    }

    @Test
    void testRemoveNodeReturn() {
        map.setDistance(346, 295);

        assertThat(map.removeNode(346)).isEqualTo(295);
    }

    @Test
    void testRemoveNodeRemoved() {
        map.setDistance(191, 765);

        map.removeNode(191);

        assertThat(map.getDistance(191)).isNull();
        assertThat(map.getNodes(765)).doesNotContain(191);
    }


    /*
     * clear
     */

    @Test
    void testClearEmpty() {
        map.clear();

        assertThat(map.size()).isEqualTo(0);
    }

    @Test
    void testClearOneNode() {
        map.setDistance(366, 205);

        map.clear();

        assertThat(map.size()).isEqualTo(0);
        assertThat(map.getDistance(366)).isNull();
        assertThat(map.getNodes(205)).isEmpty();
    }


    /*
     * keySet
     */

    @Test
    void testKeySetEmpty() {
        assertThat(map.keySet()).isEmpty();
    }

    @Test
    void testKeySetMultipleNodes() {
        map.setDistance(425, 881);
        map.setDistance(68, 855);
        map.setDistance(983, 935);
        map.setDistance(63, 736);

        assertThat(map.keySet()).containsExactlyInAnyOrder(63, 68, 425, 983);
    }


    /*
     * values
     */

    @Test
    void testValuesEmpty() {
        assertThat(map.values()).isEmpty();
    }

    @Test
    void testValuesMultipleNodes() {
        map.setDistance(156, 656);
        map.setDistance(644, 656);
        map.setDistance(950, 735);
        map.setDistance(984, 639);

        assertThat(map.values()).containsExactlyInAnyOrder(639, 656, 656, 735);
    }


    /*
     * entrySet
     */

    @Test
    void testEntrySetEmpty() {
        assertThat(map.entrySet()).isEmpty();
    }

    @Test
    void testEntrySetMultipleNodes() {
        map.setDistance(402, 509);
        map.setDistance(294, 239);
        map.setDistance(944, 817);

        assertThat(map.entrySet()).containsExactlyInAnyOrder(
                entry(402, 509), entry(294, 239), entry(944, 817));
    }


    private Map.Entry<Integer, Integer> entry(final int key, final int value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }
}
