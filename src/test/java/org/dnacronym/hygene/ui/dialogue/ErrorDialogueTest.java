package org.dnacronym.hygene.ui.dialogue;

import javafx.scene.control.Alert;
import org.dnacronym.hygene.ui.UITest;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for the {@link Dialogue} class.
 * <p>
 * Due to the nature of ui {@link Dialogue}, this class consists mostly of black box tests.
 */
final class ErrorDialogueTest extends UITest {
    private Dialogue errorDialogue;
    private Exception exception;


    @Override
    public void beforeEach() {
        exception = new UIInitialisationException("test exception.");
        interact(() -> errorDialogue = new ErrorDialogue(exception));
    }


    @Test
    void testErrorType() {
        assertThat(errorDialogue.getAlert().getAlertType()).isEqualTo(Alert.AlertType.ERROR);
    }

    @Test
    void testTitleHeaderText() {
        final Alert alert = errorDialogue.getAlert();

        assertThat(alert.getTitle()).isEqualTo("Error");
        assertThat(alert.getHeaderText()).isEqualTo("test exception.");
    }

    @Test
    void testShow() {
        final Alert mockAlert = mock(Alert.class);

        interact(() -> errorDialogue.setAlert(mockAlert, "", ""));
        errorDialogue.show();

        verify(mockAlert).showAndWait();
    }
}
