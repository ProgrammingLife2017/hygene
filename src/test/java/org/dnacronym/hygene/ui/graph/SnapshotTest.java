package org.dnacronym.hygene.ui.graph;

import javafx.scene.canvas.Canvas;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.ui.UITestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link Snapshot}.
 */
final class SnapshotTest extends UITestBase {
    static final String GFA_FILE_NAME = "src/test/resources/gfa/simple.gfa";


    @AfterEach
    void tearDown() throws IOException {
        final File parentDirectory = new File(GFA_FILE_NAME).getParentFile();
        if (parentDirectory == null) {
            return;
        }
        final File[] filesInDirectory = parentDirectory.listFiles();
        if (filesInDirectory == null) {
            return;
        }

        for (final File f : filesInDirectory) {
            if (f.getName().endsWith(".png")) {
                Files.deleteIfExists(f.toPath());
            }
        }
    }


    @Test
    void testSnapshot() {
        final String expectedDestinationDirectory = new File(GFA_FILE_NAME).getParent();

        interact(() -> {
            final Canvas canvas = spy(Canvas.class);
            when(canvas.getHeight()).thenReturn(10.0);
            when(canvas.getWidth()).thenReturn(10.0);

            final String destination = Snapshot.forGfaFile(new GfaFile(GFA_FILE_NAME)).take(canvas);

            assertThat(destination).contains(expectedDestinationDirectory);
            assertThat(new File(destination)).exists();
        });
    }
}
