package org.dnacronym.hygene.ui.progressbar;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.dnacronym.hygene.ui.UITest;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for {@link ProgressBarView}.
 */
final class ProgressBarViewTest extends UITest {
    private ProgressBarView progressBarView;
    private Stage stage;


    @Override
    public void beforeEach() {
        stage = mock(Stage.class);
        interact(() -> {
            progressBarView = new ProgressBarView();
            progressBarView.setStage(stage);
        });
    }


    @Test
    void testShow() {
        interact(() -> progressBarView.show());
        verify(stage).show();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testPerformTaskSetScene() {
        final Consumer<ProgressUpdater> consumer = mock(Consumer.class);
        interact(() -> progressBarView.performTask(consumer));

        verify(stage).setScene(any(Scene.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testConsumerCall() {
        final Consumer<ProgressUpdater> consumer  = mock(Consumer.class);

        interact(() -> progressBarView.performTask(consumer));

        verify(consumer).accept(any());
    }
}
