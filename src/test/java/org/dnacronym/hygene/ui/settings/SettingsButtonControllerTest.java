package org.dnacronym.hygene.ui.settings;

import com.google.inject.testing.fieldbinder.Bind;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link SettingsButtonController}s.
 */
@SuppressFBWarnings(
        value = "URF_UNREAD_FIELD",
        justification = "Graph visualizer does not need to be read, but still needs to be mocked."
)
final class SettingsButtonControllerTest extends UITestBase {
    private SettingsButtonController settingsButtonController;
    @Bind
    private Settings settings;
    @Bind
    private GraphVisualizer graphVisualizer;

    private ActionEvent actionEvent;
    private Node source;
    private Scene scene;
    private Stage stage;


    @Override
    public void beforeEach() {
        settings = mock(Settings.class);
        graphVisualizer = mock(GraphVisualizer.class);
        createContextOfTest();

        settingsButtonController = new SettingsButtonController();
        injectMembers(settingsButtonController);

        actionEvent = mock(ActionEvent.class);
        stage = mock(Stage.class);
        scene = mock(Scene.class);
        source = mock(Node.class);

        when(actionEvent.getSource()).thenReturn(source);
        when(source.getScene()).thenReturn(scene);
        when(scene.getWindow()).thenReturn(stage);
    }


    @Test
    void testOk() {
        interact(() -> settingsButtonController.okAction(actionEvent));

        verify(settings, times(1)).executeAll();
        verify(stage, times(1)).hide();
    }

    @Test
    void testApplyAll() {
        interact(() -> settingsButtonController.applyAction(actionEvent));
        verify(settings, times(1)).executeAll();
        verify(stage, never()).hide();
    }

    @Test
    void testCancel() {
        interact(() -> settingsButtonController.cancelAction(actionEvent));

        verify(stage, times(1)).hide();
        verify(settings, never()).executeAll();
        verify(settings, times(1)).clearAll();
    }
}
