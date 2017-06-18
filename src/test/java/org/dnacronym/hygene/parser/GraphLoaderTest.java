package org.dnacronym.hygene.parser;

import org.dnacronym.hygene.core.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link GraphLoader}.
 */
final class GraphLoaderTest {
    private String temporaryPath;
    private GraphLoader graphLoader;
    private ProgressUpdater progressUpdater;


    @BeforeEach
    void setUp() throws IOException, SQLException {
        temporaryPath = Files.getInstance().getTemporaryFile("test").getAbsolutePath();

        graphLoader = new GraphLoader(temporaryPath);

        progressUpdater = ProgressUpdater.DUMMY;
    }


    @Test
    void testHasGraphFalse() {
        assertThat(graphLoader.hasGraph()).isFalse();
    }

    @Test
    void testHasGraphTrue() throws IOException {
        graphLoader.dumpGraph(new int[][] {});

        assertThat(graphLoader.hasGraph()).isTrue();
    }

    @Test
    void testDumpGraphEquivalence() throws IOException {
        final int[][] graph = new int[][] {{88, 5}, {8, 11, 41, 65, 45}, {18, 33}, {90, 61, 85, 83}, {6, 61, 4, 89, 98},
                {77, 53, 24, 91}};

        graphLoader.dumpGraph(graph);

        assertThat(graphLoader.restoreGraph(progressUpdater)).isEqualTo(graph);
    }

    @Test
    void testDumpGraphTwice() throws IOException {
        final int[][] graphA = new int[][] {{47, 68, 74}, {83, 37}};
        final int[][] graphB = new int[][] {{56}, {83, 69}, {44, 75, 91, 11}};

        graphLoader.dumpGraph(graphA);
        graphLoader.dumpGraph(graphB);

        assertThat(graphLoader.restoreGraph(progressUpdater)).isEqualTo(graphB);
    }
}
