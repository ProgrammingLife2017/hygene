package org.dnacronym.hygene.ui.runnable;

import javafx.application.Platform;
import javafx.scene.control.Alert;


/**
 * A dialogue is displayed on-screen and prevents any further interaction with the application until it is closed.
 * <p>
 * A dialogue can either be of dialogueType {@link DialogueType#ERROR} or {@link DialogueType#WARNING}.
 */
public class Dialogue {
    private final DialogueType dialogueType;
    private final Exception exception;


    /**
     * Construct a new {@link Dialogue}.
     *
     * @param dialogueType Type of the dialogue.
     * @param exception    exception that is the cause of the dialogue.
     */
    public Dialogue(final DialogueType dialogueType, final Exception exception) {
        this.exception = exception;
        this.dialogueType = dialogueType;
    }


    /**
     * Show a dialogue onscreen.
     * <p>
     * A dialogue prevents any further interaction with the UI until it is closed. Uses the
     * {@link Alert} from JavaFX.
     * <p>
     * Uses {@link Platform#runLater(Runnable)}. This ensures this can be called from any thread.
     *
     * @see Platform#runLater(Runnable)
     */
    public final void show() {
        Platform.runLater(() -> show(dialogueType.alertType,
                dialogueType.title,
                exception.getMessage() != null ? exception.getMessage() : ""));
    }

    /**
     * Show a dialogue onscreen that prevents further interaction with the UI until closed.
     *
     * @param alertType  dialogueType of warning dialogue.
     * @param title      title of dialogue.
     * @param headerText text to display in header of dialogue.
     * @see Alert
     */
    private void show(final Alert.AlertType alertType, final String title, final String headerText) {
        final Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);

        alert.showAndWait();
    }

    /**
     * Denotes the dialogueType of dialogue.
     */
    public enum DialogueType {
        /**
         * An error is more severe, and should be used to relay to the user that something went
         * wrong with the application.
         */
        ERROR("Error", Alert.AlertType.ERROR),
        /**
         * A warning is less severe, and should be used to relay to the user that they did
         * something they shouldn't do.
         */
        WARNING("Warning", Alert.AlertType.WARNING);

        private final String title;
        private final Alert.AlertType alertType;

        /**
         * @param title     name of the dialogue type.
         * @param alertType JavaFX {@link javafx.scene.control.Alert.AlertType} of this dialogue type.
         */
        DialogueType(final String title, final Alert.AlertType alertType) {
            this.title = title;
            this.alertType = alertType;
        }
    }
}
