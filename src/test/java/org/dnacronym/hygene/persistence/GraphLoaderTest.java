package org.dnacronym.hygene.persistence;

import org.dnacronym.hygene.parser.ProgressUpdater;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link GraphLoader}.
 */
final class GraphLoaderTest extends FileDatabaseTestBase {
    private FileDatabase fileDatabase;
    private GraphLoader graphLoader;
    private ProgressUpdater progressUpdater;
    private String temporaryPath;


    @BeforeEach
    void setUp() throws IOException, SQLException {
        super.setUp();

        fileDatabase = new FileDatabase(GFA_FILE_NAME);
        graphLoader = new GraphLoader(fileDatabase);

        temporaryPath = File.createTempFile("test", ".tmp").getAbsolutePath();

        progressUpdater = ProgressUpdater.DUMMY;
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
    void testHasGraphTrue() throws IOException {
        graphLoader.dumpGraph(new int[][] {}, temporaryPath);

        assertThat(graphLoader.hasGraph()).isTrue();
    }

    @Test
    void testDeleteGraph() throws IOException {
        graphLoader.dumpGraph(new int[][] {}, temporaryPath);
        graphLoader.deleteGraph();

        assertThat(graphLoader.hasGraph()).isFalse();
    }

    @Test
    void testDumpGraphEquivalence() throws IOException {
        final int[][] graph = new int[][] {{88, 5}, {8, 11, 41, 65, 45}, {18, 33}, {90, 61, 85, 83}, {6, 61, 4, 89, 98},
                {77, 53, 24, 91}};

        graphLoader.dumpGraph(graph, temporaryPath);

        assertThat(graphLoader.restoreGraph(progressUpdater, temporaryPath)).isEqualTo(graph);
    }

    @Test
    void testDumpGraphTwice() throws IOException {
        final int[][] graphA = new int[][] {{47, 68, 74}, {83, 37}};
        final int[][] graphB = new int[][] {{56}, {83, 69}, {44, 75, 91, 11}};

        graphLoader.dumpGraph(graphA, temporaryPath);
        graphLoader.dumpGraph(graphB, temporaryPath);

        assertThat(graphLoader.restoreGraph(progressUpdater, temporaryPath)).isEqualTo(graphB);
    }
}
