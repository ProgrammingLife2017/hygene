package org.dnacronym.hygene.ui.dialogue;

import javafx.application.Platform;
import javafx.scene.control.Alert;


/**
 * A dialogue is displayed on-screen and prevents any further interaction with the application until it is closed.
 */
public abstract class Dialogue {
    private String title;
    private String headerText;
    private Alert.AlertType alertType;


    /**
     * Construct a new {@link Dialogue}.
     */
    Dialogue(final String title, final String headerText, final Alert.AlertType alertType) {
        this.title = title;
        this.headerText = headerText;
        this.alertType = alertType;
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
     * @see Alert
     */
    public void show() {
        Platform.runLater(() -> show(new Alert(alertType), title, headerText));
    }

    /**
     * Show a dialogue onscreen that prevents further interaction with the UI until closed.
     *
     * @param alert      alert to display
     * @param title      title of dialogue
     * @param headerText text to display in header of dialogue
     */
    void show(final Alert alert, final String title, final String headerText) {
        alert.setTitle(title);
        alert.setHeaderText(headerText);

        alert.showAndWait();
    }
}
