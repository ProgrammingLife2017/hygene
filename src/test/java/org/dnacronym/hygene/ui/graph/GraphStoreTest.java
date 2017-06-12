package org.dnacronym.hygene.ui.graph;

import javafx.application.Platform;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.dnacronym.hygene.persistence.FileDatabaseDriver;
import org.dnacronym.hygene.ui.UITestBase;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests of {@link GraphStore}s.
 */
final class GraphStoreTest extends UITestBase {
    private GraphStore graphStore;


    @Override
    public void beforeEach() {
        graphStore = new GraphStore();
    }


    @Test
    void testInitialGraphNull() {
        assertThat(graphStore.getGfaFileProperty().get()).isNull();
    }

    @Test
    void testOpenGfaFile() throws IOException, ExecutionException, InterruptedException {
        final File file = new File("src/test/resources/gfa/simple.gfa");

        final CompletableFuture<Object> future = new CompletableFuture<>();

        Platform.runLater(() -> {
            try {
                graphStore.loadGfaFile(file, ProgressUpdater.DUMMY);
            } catch (final IOException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                assertThat(graphStore.getGfaFileProperty().get()).isNotNull();
                future.complete(null);
            });
        });

        assertThat(future.get()).isNull();

        Files.deleteIfExists(Paths.get(file.getPath() + FileDatabaseDriver.DB_FILE_EXTENSION));
    }
}
