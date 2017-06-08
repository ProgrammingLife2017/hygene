package org.dnacronym.hygene.ui.menu;

import javafx.event.ActionEvent;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.console.ConsoleView;
import org.dnacronym.hygene.ui.help.HelpMenuView;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * Unit tests for {@link ToolsMenuController}.
 */
final class ToolsMenuControllerTest extends UITestBase {
    private ToolsMenuController toolsMenuController;


    @Override
    public void beforeEach() {
        toolsMenuController = new ToolsMenuController();
    }


    @Test
    void testOpenConsoleActionInit() throws Exception {
        final ActionEvent action = mock(ActionEvent.class);

        final CompletableFuture<Object> future = new CompletableFuture<>();

        interact(() -> {
            try {
                toolsMenuController.openConsoleAction(action);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            future.complete(toolsMenuController.getConsoleView());
        });

        assertThat(future.get()).isNotNull();
    }

    @Test
    void testOpenHelpMenuViewWindowInitialization() throws ExecutionException, InterruptedException {
        final CompletableFuture<HelpMenuView> future = new CompletableFuture<>();

        interact(() -> {
            try {
                toolsMenuController.openHelpAction(new ActionEvent());
            } catch (final IOException e) {
                e.printStackTrace();
            }
            future.complete(toolsMenuController.getHelpMenuView());
        });

        assertThat(future.get()).isNotNull();
        assertThat(future.get().getStage().isShowing()).isTrue();
    }

    @Test
    void testOpenConsoleActionWindowState() throws Exception {
        final ActionEvent action = mock(ActionEvent.class);

        final CompletableFuture<ConsoleView> future = new CompletableFuture<>();

        interact(() -> {
            try {
                toolsMenuController.openConsoleAction(action);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            future.complete(toolsMenuController.getConsoleView());
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
                toolsMenuController.openConsoleAction(action);
                future1.complete(toolsMenuController.getConsoleView());
                toolsMenuController.openConsoleAction(action);
                future2.complete(toolsMenuController.getConsoleView());
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });

        // We want the actions object reference to be the same.
        assertThat(future1.get()).isEqualTo(future2.get());
    }
}
