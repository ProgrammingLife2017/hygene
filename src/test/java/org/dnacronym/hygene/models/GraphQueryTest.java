package org.dnacronym.hygene.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for {@link GraphQuery}s.
 */
class GraphQueryTest extends GraphBasedTest {
    @Test
    void testQueryIllegalNegativeCentre() {
        createGraph(71);
        createGraphQuery();

        final Throwable e = catchThrowable(() -> getGraphQuery().query(-14, 43));

        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testQueryIllegalExcessiveCentre() {
        createGraph(85);
        createGraphQuery();

        final Throwable e = catchThrowable(() -> getGraphQuery().query(139, 9));

        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testQueryIllegalNegativeRadius() {
        createGraph(60);
        createGraphQuery();

        final Throwable e = catchThrowable(() -> getGraphQuery().query(23, -14));

        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests that no nodes are in the GraphQuery when the radius is zero.
     */
    @Test
    void testQueryZeroRadius() {
        createGraph(62);
        createGraphQuery();

        final List<Integer> nodes = new ArrayList<>();
        getGraphQuery().query(49, 0);
        getGraphQuery().visit(nodes::add);

        assertThat(nodes).containsExactlyInAnyOrder(49);
    }

    /**
     * Tests that only the centre node is in the GraphQuery when the radius is one.
     */
    @Test
    void testQueryOneRadius() {
        createGraph(72);
        createGraphQuery();
        addEdges(new int[][] {{13, 22}, {22, 25}, {25, 55}});

        final List<Integer> nodes = new ArrayList<>();
        getGraphQuery().query(22, 1);
        getGraphQuery().visit(nodes::add);

        assertThat(nodes).containsExactlyInAnyOrder(13, 22, 25);
    }

    /**
     * Tests that a new query removes the data from a previous query.
     */
    @Test
    void testQueryNewCentre() {
        createGraph(29);
        createGraphQuery();
        addEdges(new int[][] {
                {4, 19}, {19, 23}, {23, 28},
                {13, 17}, {17, 24}, {24, 28}
        });

        final List<Integer> nodes = new ArrayList<>();
        getGraphQuery().query(19, 1);
        getGraphQuery().query(17, 2);
        getGraphQuery().visit(nodes::add);

        assertThat(nodes).containsExactlyInAnyOrder(13, 17, 24, 28);
    }
}
