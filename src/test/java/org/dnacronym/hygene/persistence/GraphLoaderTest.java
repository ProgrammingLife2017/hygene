package org.dnacronym.hygene.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link GraphLoader}s.
 */
final class GraphLoaderTest extends FileDatabaseBaseTest {
    private FileDatabase fileDatabase;
    private GraphLoader graphLoader;


    @BeforeEach
    void setUp() throws IOException, SQLException {
        super.setUp();

        fileDatabase = new FileDatabase(GFA_FILE_NAME);
        graphLoader = new GraphLoader(fileDatabase);
    }

    @AfterEach
    void tearDown() throws IOException, SQLException {
        fileDatabase.close();

        super.tearDown();
    }


    @Test
    void testHasGraphFalse() {
        assertThat(graphLoader.hasGraph()).isFalse();
    }

    @Test
    void testHasGraphTrue() {
        graphLoader.dumpGraph(new int[][] {});

        assertThat(graphLoader.hasGraph()).isTrue();
    }

    @Test
    void testDeleteGraph() {
        graphLoader.dumpGraph(new int[][] {});
        graphLoader.deleteGraph();

        assertThat(graphLoader.hasGraph()).isFalse();
    }

    @Test
    void testDumpGraphEquivalence() {
        final int[][] graph = new int[][] {{88, 5}, {8, 11, 41, 65, 45}, {18, 33}, {90, 61, 85, 83}, {6, 61, 4, 89, 98},
                {77, 53, 24, 91}};

        graphLoader.dumpGraph(graph);

        assertThat(graphLoader.restoreGraph()).isEqualTo(graph);
    }

    @Test
    void testDumpGraphTwice() {
        final int[][] graphA = new int[][] {{47, 68, 74}, {83, 37}};
        final int[][] graphB = new int[][] {{56}, {83, 69}, {44, 75, 91, 11}};

        graphLoader.dumpGraph(graphA);
        graphLoader.dumpGraph(graphB);

        assertThat(graphLoader.restoreGraph()).isEqualTo(graphB);
    }
}
