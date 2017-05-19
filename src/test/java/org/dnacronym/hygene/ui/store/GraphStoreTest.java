package org.dnacronym.hygene.ui.store;

import org.dnacronym.hygene.ui.UITest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    void testOpenGfaFile() throws IOException {
        final File file = new File("src/test/resources/gfa/simple.gfa");

        interact(() -> {
            try {
                graphStore.load(file, progress -> {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        assertThat(graphStore.getGfaFileProperty().get()).isNotNull();
        Files.deleteIfExists(Paths.get(file.getPath() + ".db"));
    }
}
