package org.dnacronym.hygene.graph;

import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.GfaParseException;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for {@link CenterPointQuery}.
 */
final class CenterPointQueryTest {
    private static final String GFA_TEST_FILE = "src/test/resources/gfa/simple.gfa";

    private Graph graph;
    private CenterPointQuery centerPointQuery;


    @BeforeEach
    void setUp() throws GfaParseException {
        graph = new GfaFile(GFA_TEST_FILE).parse(ProgressUpdater.DUMMY);
        centerPointQuery = new CenterPointQuery(graph);
    }
    

    @Test
    void testGetRadius() {
        centerPointQuery.query(0, 5);

        assertThat(centerPointQuery.getRadius()).isEqualTo(5);
    }

    @Test
    void testGetCentre() {
        centerPointQuery.query(2, 0);

        assertThat(centerPointQuery.getCentre()).isEqualTo(2);
    }

    @Test
    void testQueryNegativeCentre() {
        final Throwable exception = catchThrowable(() -> centerPointQuery.query(-1, 0));

        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        assertThat(exception).hasMessageContaining("Centre point node id cannot be negative.");
    }

    @Test
    void testQueryCentreExceedGraphSize() {
        final Throwable exception = catchThrowable(() -> centerPointQuery.query(999, 0));

        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        assertThat(exception).hasMessageContaining("Centre point node id cannot exceed graph size.");
    }

    @Test
    void testQueryNegativeRadius() {
        final Throwable exception = catchThrowable(() -> centerPointQuery.query(0, -1));

        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        assertThat(exception).hasMessageContaining("Radius cannot be negative.");
    }

    @Test
    void testQuery() {
        centerPointQuery.query(1, 1);

        final Subgraph subgraph = centerPointQuery.getCache();

        assertThat(((Segment) subgraph.getNodes().iterator().next()).getId()).isEqualTo(1);
    }

    @Test
    void testSetCenter() {
        centerPointQuery.setCenter(2);

        assertThat(centerPointQuery.getCentre()).isEqualTo(2);
    }

    @Test
    void testSetCenterWithinMaxRadius() {
        centerPointQuery.setRadius(200);
        centerPointQuery.setCenter(2);

        assertThat(centerPointQuery.getCentre()).isEqualTo(2);
    }

    @Test
    void testSetCenterDoesNotAcceptNegativeValues() {
        final Throwable exception = catchThrowable(() -> centerPointQuery.setCenter(-1));

        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        assertThat(exception).hasMessageContaining("Centre point node id cannot be negative.");
    }

    @Test
    void testSetCenterDoesNotAcceptGraphSizeExceedingValues() {
        final Throwable exception = catchThrowable(() -> centerPointQuery.setCenter(999));

        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        assertThat(exception).hasMessageContaining("Centre point node id cannot exceed graph size.");
    }

    @Test
    void testSetRadiusToAValueLowerThanBefore() {
        centerPointQuery.query(1, 3);
        centerPointQuery.setRadius(2);

        assertThat(centerPointQuery.getRadius()).isEqualTo(2);
    }

    @Test
    void testSetRadiusToAValueHigherThanBefore() {
        centerPointQuery.query(1, 2);
        centerPointQuery.setRadius(3);

        assertThat(centerPointQuery.getRadius()).isEqualTo(3);
    }
}
