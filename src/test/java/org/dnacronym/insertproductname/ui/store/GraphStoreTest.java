package org.dnacronym.insertproductname.ui.store;

import org.dnacronym.insertproductname.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.catchThrowable;
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
        assertThat(graphStore.sequenceGraphProperty().get()).isNull();
    }

    @Test
    public final void testNonExistentFile() {
        final File file = mock(File.class);
        when(file.exists()).thenReturn(false);

        Throwable e = catchThrowable(() -> graphStore.load(file));

        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public final void testWrongFileExtension() {
        final File file = mock(File.class);
        when(file.getName()).thenReturn("file.wrong");

        Throwable e = catchThrowable(() -> graphStore.load(file));

        assertThat(e).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public final void testOpenGFAFile() throws IOException, ParseException {
        final File file = new File("src/test/resources/gfa/simple.gfa");

        graphStore.load(file);

        assertThat(graphStore.sequenceGraphProperty().get()).isNotNull();
    }
}
