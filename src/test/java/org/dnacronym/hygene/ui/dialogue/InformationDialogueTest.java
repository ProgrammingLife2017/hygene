package org.dnacronym.hygene.ui.dialogue;

import javafx.scene.control.Alert;
import org.dnacronym.hygene.ui.UITestBase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link WarningDialogue}.
 */
final class InformationDialogueTest extends UITestBase {
    private Dialogue informationDialogue;


    @Override
    public void beforeEach() {
        interact(() -> informationDialogue = new InformationDialogue("header.", "message."));
    }


    @Test
    void testErrorType() {
        assertThat(informationDialogue.getAlert().getAlertType()).isEqualTo(Alert.AlertType.INFORMATION);
    }

    @Test
    void testTitleHeaderText() {
        final Alert alert = informationDialogue.getAlert();

        assertThat(alert.getTitle()).isEqualTo("header.");
        assertThat(alert.getHeaderText()).isEqualTo("message.");
    }
}
