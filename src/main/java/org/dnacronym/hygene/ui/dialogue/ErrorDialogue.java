package org.dnacronym.hygene.ui.dialogue;

import javafx.scene.control.Alert;


/**
 * Error dialogue.
 * <p>
 * Displayed when something went wrong with the application itself.
 */
public final class ErrorDialogue extends Dialogue {
    /**
     * Construct a new {@link ErrorDialogue}.
     *
     * @param exception exception that is the cause of the dialogue. It's message is displayed as message in the
     *                  {@link Dialogue}.
     */
    public ErrorDialogue(final Exception exception) {
        super("Error", exception.getMessage(), Alert.AlertType.ERROR);
    }
}
