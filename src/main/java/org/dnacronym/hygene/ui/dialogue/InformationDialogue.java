package org.dnacronym.hygene.ui.dialogue;

import javafx.scene.control.Alert;


/**
 * Information dialogue.
 * <p>
 * Displayed when we want to inform the user about something.
 */
public final class InformationDialogue extends Dialogue {
    /**
     * Construct a new {@link InformationDialogue}.
     *
     * @param header the header text of the {@link Alert}
     * @param message the message to display in the {@link InformationDialogue}
     */
    public InformationDialogue(final String header, final String message) {
        super(header, message, Alert.AlertType.INFORMATION);
    }
}
