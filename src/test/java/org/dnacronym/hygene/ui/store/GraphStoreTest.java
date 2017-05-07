package org.dnacronym.hygene.ui.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests of {@link GraphStore}s.
 */
public class GraphStoreTest {
    private GraphStore graphStore;


    @BeforeEach
    final void beforeEach() {
        graphStore = new GraphStore();
    }


    @Test
    public final void testInitialGraphNull() {
        assertThat(graphStore.getSequenceGraphProperty().get()).isNull();
    }

    @Test
    public final void testOpenGFAFile() throws IOException {
        final File file = new File("src/test/resources/gfa/simple.gfa");

        graphStore.load(file);

        assertThat(graphStore.getSequenceGraphProperty().get()).isNotNull();
    }
}
