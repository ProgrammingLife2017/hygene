package org.dnacronym.hygene.ui.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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
