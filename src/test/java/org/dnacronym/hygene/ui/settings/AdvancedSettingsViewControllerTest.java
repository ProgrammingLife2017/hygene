package org.dnacronym.hygene.ui.settings;


import com.google.inject.testing.fieldbinder.Bind;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link AdvancedSettingsViewController}s.
 */
final class AdvancedSettingsViewControllerTest extends UITestBase {
    private AdvancedSettingsViewController advancedSettingsViewController;
    @Bind
    private GraphVisualizer graphVisualizer;
    @Bind
    private Settings settings;
    private CheckBox checkBox;
    private ActionEvent mouseEvent;

    @Override
    public void beforeEach() {
        graphVisualizer = mock(GraphVisualizer.class);
        settings = mock(Settings.class);
        createContextOfTest();

        advancedSettingsViewController = new AdvancedSettingsViewController();
        injectMembers(advancedSettingsViewController);

        final SimpleBooleanProperty displayLaneBorder = new SimpleBooleanProperty();
        displayLaneBorder.setValue(false);
        when(graphVisualizer.getDisplayBordersProperty()).thenReturn(displayLaneBorder);

        checkBox = new CheckBox();
        checkBox.setSelected(true);

        mouseEvent = mock(ActionEvent.class);
    }


    @Test
    void testShowBorders() {
        when(mouseEvent.getSource()).thenReturn(checkBox);
        interact(() -> advancedSettingsViewController.showLaneBordersClicked(mouseEvent));
        verify(settings, times(1)).addRunnable(any(Runnable.class));
    }

    @Test
    void testShowBorderRunnable() {
        assertThat(checkBox.isSelected()).isTrue();
        assertThat(graphVisualizer.getDisplayBordersProperty().getValue()).isFalse();

        when(mouseEvent.getSource()).thenReturn(checkBox);
        interact(() -> advancedSettingsViewController.showLaneBordersClicked(mouseEvent));

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(settings).addRunnable(captor.capture());
        final Runnable command = captor.getValue();
        command.run();

        assertThat(graphVisualizer.getDisplayBordersProperty().getValue()).isTrue();
    }
}
