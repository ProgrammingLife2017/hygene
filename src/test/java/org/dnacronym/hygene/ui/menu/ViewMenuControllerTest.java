package org.dnacronym.hygene.ui.menu;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.console.ConsoleView;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Unit tests of {@link ViewMenuController}.
 */
final class ViewMenuControllerTest extends UITestBase {
    private ViewMenuController viewMenuController;
    private SequenceVisualizer sequenceVisualizer;


    @Override
    public void beforeEach() {
        final BooleanProperty sequenceVisible = new SimpleBooleanProperty();
        sequenceVisualizer = mock(SequenceVisualizer.class);
        when(sequenceVisualizer.getVisibleProperty()).thenReturn(sequenceVisible);

        viewMenuController = new ViewMenuController();
        viewMenuController.setSequenceVisualizer(sequenceVisualizer);
    }


    @Test
    void testToggleSequence() {
        final boolean original = sequenceVisualizer.getVisibleProperty().get();

        viewMenuController.toggleSequenceVisualizerAction(mock(ActionEvent.class));

        assertThat(sequenceVisualizer.getVisibleProperty().get()).isNotEqualTo(original);
    }

    @Test
    void testOpenConsoleActionInit() throws Exception {
        final ActionEvent action = mock(ActionEvent.class);

        final CompletableFuture<Object> future = new CompletableFuture<>();

        interact(() -> {
            try {
                viewMenuController.openConsoleAction(action);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            future.complete(viewMenuController.getConsoleView());
        });

        assertThat(future.get()).isNotNull();
    }

    @Test
    void testOpenConsoleActionWindowState() throws Exception {
        final ActionEvent action = mock(ActionEvent.class);

        final CompletableFuture<ConsoleView> future = new CompletableFuture<>();

        interact(() -> {
            try {
                viewMenuController.openConsoleAction(action);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            future.complete(viewMenuController.getConsoleView());
        });

        assertThat(future.get().getStage().isShowing()).isTrue();
    }

    @Test
    void testConsoleWindowPersistence() throws Exception {
        final ActionEvent action = mock(ActionEvent.class);

        final CompletableFuture<ConsoleView> future1 = new CompletableFuture<>();
        final CompletableFuture<ConsoleView> future2 = new CompletableFuture<>();

        interact(() -> {
            try {
                viewMenuController.openConsoleAction(action);
                future1.complete(viewMenuController.getConsoleView());
                viewMenuController.openConsoleAction(action);
                future2.complete(viewMenuController.getConsoleView());
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });

        // We want the actions object reference to be the same.
        assertThat(future1.get()).isEqualTo(future2.get());
    }
}
