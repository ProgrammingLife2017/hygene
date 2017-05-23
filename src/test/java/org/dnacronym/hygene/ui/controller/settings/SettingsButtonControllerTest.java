package org.dnacronym.hygene.ui.controller.settings;

import javafx.stage.Stage;
import org.dnacronym.hygene.ui.UITest;
import org.dnacronym.hygene.ui.store.Settings;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for {@link SettingsButtonController}s.
 */
final class SettingsButtonControllerTest extends UITest {
    private SettingsButtonController settingsButtonController;
    private Settings settings;
    private Stage stage;


    @Override
    public void beforeEach() {
        this.settingsButtonController = new SettingsButtonController();

        settings = mock(Settings.class);
        stage = mock(Stage.class);
        settingsButtonController.setSettings(settings);
        settingsButtonController.setStage(stage);
    }


    @Test
    void testOk() {
        interact(() -> settingsButtonController.okAction());

        verify(settings, times(1)).executeAll();
        verify(stage, times(1)).hide();
    }

    @Test
    void testApplyAll() {
        interact(() -> settingsButtonController.applyAction());
        verify(settings, times(1)).executeAll();
        verify(stage, never()).hide();
    }

    @Test
    void testCancel() {
        interact(() -> settingsButtonController.cancelAction());

        verify(stage, times(1)).hide();
        verify(settings, never()).executeAll();
        verify(settings, times(1)).clearAll();
    }
}
