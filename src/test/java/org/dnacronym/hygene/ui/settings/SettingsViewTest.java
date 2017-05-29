package org.dnacronym.hygene.ui.settings;


import javafx.stage.Stage;
import org.dnacronym.hygene.ui.UITestBase;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for {@link SettingsView}.
 */
final class SettingsViewTest extends UITestBase {
    private SettingsView settingsView;
    private Settings settings;
    private Stage stage;


    @Override
    public void beforeEach() {
        settings = mock(Settings.class);
        stage = mock(Stage.class);

        interact(() -> {
            settingsView = new SettingsView(settings);
            settingsView.setStage(stage);
        });
    }


    @Test
    void testShow() {
        settingsView.show();
        verify(stage, times(1)).show();
    }
}
