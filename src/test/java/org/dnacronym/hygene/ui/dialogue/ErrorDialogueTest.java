package org.dnacronym.hygene.ui.dialogue;

import javafx.scene.control.Alert;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link Dialogue}.
 */
final class ErrorDialogueTest extends UITestBase {
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
}
