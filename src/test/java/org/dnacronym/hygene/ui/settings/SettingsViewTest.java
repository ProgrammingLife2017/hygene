package org.dnacronym.hygene.ui.settings;


import javafx.stage.Stage;
import org.dnacronym.hygene.ui.UITest;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


final class SettingsViewTest extends UITest {
    private SettingsView settingsView;
    private Settings settings;
    private Stage stage;


    @Override
    public void beforeEach() {
        settings = mock(Settings.class);
        settingsView = new SettingsView(settings);

        stage = mock(Stage.class);
        settingsView.setStage(stage);
    }


    @Test
    void testShow() {
        settingsView.show();
        verify(stage, times(1)).show();
    }
}
