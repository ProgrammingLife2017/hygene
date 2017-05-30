package org.dnacronym.hygene.ui.dialogue;

import javafx.scene.control.Alert;
import org.dnacronym.hygene.ui.UITestBase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for {@link WarningDialogue}.
 */
final class WarningDialogueTest extends UITestBase {
    private Dialogue errorDialogue;


    @Override
    public void beforeEach() {
        interact(() -> errorDialogue = new WarningDialogue("message."));
    }


    @Test
    void testErrorType() {
        assertThat(errorDialogue.getAlert().getAlertType()).isEqualTo(Alert.AlertType.WARNING);
    }

    @Test
    void testTitleHeaderText() {
        final Alert alert = errorDialogue.getAlert();

        assertThat(alert.getTitle()).isEqualTo("Warning");
        assertThat(alert.getHeaderText()).isEqualTo("message.");
    }

    @Test
    void testShow() {
        final Alert mockAlert = mock(Alert.class);

        interact(() -> errorDialogue.setAlert(mockAlert, "", ""));
        errorDialogue.show();

        verify(mockAlert).showAndWait();
    }
}
