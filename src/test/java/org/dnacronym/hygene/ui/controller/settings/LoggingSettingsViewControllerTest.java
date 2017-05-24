package org.dnacronym.hygene.ui.controller.settings;

import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import org.apache.logging.log4j.LogManager;
import org.dnacronym.hygene.ui.UITest;
import org.dnacronym.hygene.ui.store.Settings;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for {@link LoggingSettingsViewController}s.
 */
final class LoggingSettingsViewControllerTest extends UITest {
    private LoggingSettingsViewController loggingSettingsViewController;
    private GraphVisualizer graphVisualizer;
    private Settings settingsMock;


    @Override
    public void beforeEach() {
        loggingSettingsViewController = new LoggingSettingsViewController();

        graphVisualizer = mock(GraphVisualizer.class);
        settingsMock = mock(Settings.class);
        loggingSettingsViewController.setGraphVisualizer(graphVisualizer);
        loggingSettingsViewController.setSettings(settingsMock);
    }

    @Test
    void testChangeLogLevelEvent() {
        ActionEvent event = new ActionEvent();
        interact(() -> loggingSettingsViewController.onLogLevelChanged(event));
        verify(settingsMock, times(1)).addRunnable(any(Runnable.class));
    }

    /**
     * This test will set the value of {@link LoggingSettingsViewController} {@link ChoiceBox} to value to different
     * from the current {@link Level}.
     * <p>
     * The {@link LoggingSettingsViewController#onLogLevelChanged(ActionEvent)} method
     * will be called which creates a new {@link Runnable} containing the command to update the {@link Level}.
     * <p>
     * The {@link Runnable} will be added to the {@link Settings} using {@link Settings#addRunnable(Runnable)}.
     * {@link Settings} has been mocked and and a {@link ArgumentCaptor} has been added to intercept the runnable. After
     * then runnable has been intercepted it can be invoked.
     * <p>
     * This test will then succeed if the log {@link Level} has been changed accordingly.
     */
    @Test
    void testChangeLogLevelRunnable() {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();

        String currentLevel = LogManager.getRootLogger().getLevel().toString();
        String newLevel;
        if (currentLevel.equals("ERROR")) {
            choiceBox.setValue("DEBUG");
            newLevel = "DEBUG";
        } else {
            choiceBox.setValue("ERROR");
            newLevel = "ERROR";
        }

        assertThat(currentLevel).isNotEqualTo(newLevel);

        loggingSettingsViewController.setChoiceBox(choiceBox);

        ActionEvent event = new ActionEvent();
        interact(() -> loggingSettingsViewController.onLogLevelChanged(event));

        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(settingsMock).addRunnable(captor.capture());
        Runnable command = captor.getValue();
        command.run();

        currentLevel = LogManager.getRootLogger().getLevel().toString();

        assertThat(currentLevel).isEqualTo(newLevel);
    }

    @Test
    void testInitializationLogLevels() {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();

        loggingSettingsViewController.setChoiceBox(choiceBox);
        loggingSettingsViewController.initialize(null, null);

        assertThat(choiceBox.getItems()).isEqualTo(LoggingSettingsViewController.getLogLevels());
    }

    @Test
    void testInitializationCurrentLogLevel() {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        String currentLevel = LogManager.getRootLogger().getLevel().toString();

        loggingSettingsViewController.setChoiceBox(choiceBox);
        loggingSettingsViewController.initialize(null, null);

        assertThat(choiceBox.getValue()).isEqualTo(currentLevel);
    }

}
