package org.dnacronym.hygene.ui.controller.settings;

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


    @Override
    public void beforeEach() {
        this.settingsButtonController = new SettingsButtonController();

        settings = mock(Settings.class);
        settingsButtonController.setSettings(settings);
    }


    @Test
    void testOk() {
        interact(() -> settingsButtonController.okAction());
        verify(settings, times(1)).executeAll();
    }

    @Test
    void testApplyAll() {
        interact(() -> settingsButtonController.applyAction());
        verify(settings, times(1)).executeAll();
    }

    @Test
    void testCancel() {
        interact(() -> settingsButtonController.cancelAction());

        verify(settings, never()).executeAll();
        verify(settings, times(1)).clearAll();
    }
}
