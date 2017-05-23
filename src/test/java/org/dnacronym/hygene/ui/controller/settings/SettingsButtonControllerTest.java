package org.dnacronym.hygene.ui.controller.settings;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.dnacronym.hygene.ui.UITest;
import org.dnacronym.hygene.ui.store.Settings;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link SettingsButtonController}s.
 */
final class SettingsButtonControllerTest extends UITest {
    private SettingsButtonController settingsButtonController;
    private Settings settings;

    private ActionEvent actionEvent;
    private Node source;
    private Scene scene;
    private Stage stage;


    @Override
    public void beforeEach() {
        this.settingsButtonController = new SettingsButtonController();

        settings = mock(Settings.class);
        actionEvent = mock(ActionEvent.class);
        stage = mock(Stage.class);
        scene = mock(Scene.class);
        source = mock(Node.class);

        when(actionEvent.getSource()).thenReturn(source);
        when(source.getScene()).thenReturn(scene);
        when(scene.getWindow()).thenReturn(stage);
        settingsButtonController.setSettings(settings);
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
