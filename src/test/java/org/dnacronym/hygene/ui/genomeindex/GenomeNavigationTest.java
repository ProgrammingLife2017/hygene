package org.dnacronym.hygene.ui.genomeindex;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.progressbar.StatusBar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link GenomeNavigation}.
 */
final class GenomeNavigationTest {
    private GraphStore graphStore;
    private StatusBar statusBar;
    private GenomeNavigation genomeNavigation;
    private ObjectProperty<GfaFile> gfaFileObjectProperty;


    @BeforeEach
    void beforeEach() {
        graphStore = mock(GraphStore.class);
        gfaFileObjectProperty = new SimpleObjectProperty<>();
        when(graphStore.getGfaFileProperty()).thenReturn(gfaFileObjectProperty);
        statusBar = mock(StatusBar.class);

        genomeNavigation = new GenomeNavigation(graphStore, statusBar);
    }


    @Test
    @SuppressWarnings("unchecked")
    void testTriggerGenomeIndex() {
        final GfaFile gfaFile = new GfaFile("hey");
        gfaFileObjectProperty.set(gfaFile);

        verify(statusBar).monitorTask(any(Consumer.class));
    }

    @Test
    void testDefaultIndexing() {
        assertThat(genomeNavigation.getIndexedFinishedProperty().get()).isEqualTo(false);
    }
}
