package org.dnacronym.hygene.ui.store;

import javafx.application.Platform;
import org.dnacronym.hygene.ui.UITest;
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
final class GraphStoreTest extends UITest {
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

        CompletableFuture<Object> future = new CompletableFuture<>();

        Platform.runLater(() -> {
            try {
                graphStore.load(file, progress -> {
                });
            } catch (final IOException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                assertThat(graphStore.getGfaFileProperty().get()).isNotNull();
                future.complete(null);
            });
        });

        assertThat(future.get()).isNull();

        Files.deleteIfExists(Paths.get(file.getPath() + ".db"));
    }
}
