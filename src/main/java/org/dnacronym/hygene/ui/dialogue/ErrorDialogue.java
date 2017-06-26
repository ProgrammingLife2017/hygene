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
     * @param exception the {@link Exception} that is the cause of the dialogue. Its message is displayed as message in
     *                  the {@link Dialogue}
     */
    public ErrorDialogue(final Exception exception) {
        this(exception.getMessage());
    }

    public ErrorDialogue(final String message) {
        super("Error", message, Alert.AlertType.ERROR);
    }
}
