package org.dnacronym.hygene.ui.menu;

import javafx.event.ActionEvent;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.help.HelpMenuView;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link HelpMenuController}.
 */
final class HelpMenuControllerTest extends UITestBase {
    private HelpMenuController helpMenuController;


    @Override
    public void beforeEach() {
        helpMenuController = new HelpMenuController();
        injectMembers(helpMenuController);
    }


    @Test
    void testOpenHelpMenuViewWindowInitialization() throws ExecutionException, InterruptedException {
        final CompletableFuture<HelpMenuView> future = new CompletableFuture<>();

        interact(() -> {
            try {
                helpMenuController.openHelpAction(new ActionEvent());
            } catch (final IOException e) {
                e.printStackTrace();
            }
            future.complete(helpMenuController.getHelpMenuView());
        });

        assertThat(future.get()).isNotNull();
        assertThat(future.get().getStage().isShowing()).isTrue();
    }
}
