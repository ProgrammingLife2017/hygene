package org.dnacronym.hygene.ui.settings;

import com.google.inject.testing.fieldbinder.Bind;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import org.apache.logging.log4j.LogManager;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
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
@SuppressFBWarnings(
        value = "URF_UNREAD_FIELD",
        justification = "Graph visualizer does not need to be read, but still needs to be mocked."
)
final class LoggingSettingsViewControllerTest extends UITestBase {
    private LoggingSettingsViewController loggingSettingsViewController;
    @Bind
    private GraphVisualizer graphVisualizer;
    @Bind
    private Settings settingsMock;


    @Override
    public void beforeEach() {
        graphVisualizer = mock(GraphVisualizer.class);
        settingsMock = mock(Settings.class);
        createContextOfTest();

        loggingSettingsViewController = new LoggingSettingsViewController();
        injectMembers(loggingSettingsViewController);
    }

    @Test
    void testChangeLogLevelEvent() {
        final ActionEvent event = new ActionEvent();
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
        final ChoiceBox<String> choiceBox = new ChoiceBox<>();

        String currentLevel = LogManager.getRootLogger().getLevel().toString();
        final String newLevel;
        if (currentLevel.equals("ERROR")) {
            choiceBox.setValue("DEBUG");
            newLevel = "DEBUG";
        } else {
            choiceBox.setValue("ERROR");
            newLevel = "ERROR";
        }

        assertThat(currentLevel).isNotEqualTo(newLevel);

        loggingSettingsViewController.setChoiceBox(choiceBox);

        final ActionEvent event = new ActionEvent();
        interact(() -> loggingSettingsViewController.onLogLevelChanged(event));

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(settingsMock).addRunnable(captor.capture());
        final Runnable command = captor.getValue();
        command.run();

        currentLevel = LogManager.getRootLogger().getLevel().toString();

        assertThat(currentLevel).isEqualTo(newLevel);
    }

    @Test
    void testInitializationLogLevels() {
        final ChoiceBox<String> choiceBox = new ChoiceBox<>();

        loggingSettingsViewController.setChoiceBox(choiceBox);
        loggingSettingsViewController.initialize(null, null);

        assertThat(choiceBox.getItems()).isEqualTo(LoggingSettingsViewController.getLogLevels());
    }

    @Test
    void testInitializationCurrentLogLevel() {
        final ChoiceBox<String> choiceBox = new ChoiceBox<>();
        final String currentLevel = LogManager.getRootLogger().getLevel().toString();

        loggingSettingsViewController.setChoiceBox(choiceBox);
        loggingSettingsViewController.initialize(null, null);

        assertThat(choiceBox.getValue()).isEqualTo(currentLevel);
    }

}
