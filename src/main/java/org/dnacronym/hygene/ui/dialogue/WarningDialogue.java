package org.dnacronym.hygene.ui.dialogue;

import javafx.scene.control.Alert;


/**
 * Warning dialogue.
 * <p>
 * Displayed when the user did something or something minor went wrong.
 */
public final class WarningDialogue extends Dialogue {
    /**
     * Construct a new {@link WarningDialogue}.
     *
     * @param message the message to display in the {@link WarningDialogue}
     */
    public WarningDialogue(final String message) {
        super("Warning", message, Alert.AlertType.WARNING);
    }
}
