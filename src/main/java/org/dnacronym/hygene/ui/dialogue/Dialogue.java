package org.dnacronym.hygene.ui.dialogue;

import javafx.application.Platform;
import javafx.scene.control.Alert;


/**
 * A dialogue is displayed on-screen and prevents any further interaction with the application until it is closed.
 */
public abstract class Dialogue {
    private Alert alert;


    /**
     * Construct a new {@link Dialogue}.
     *
     * @param title      title of the {@link Alert}
     * @param headerText header text of the {@link Alert}
     * @param alertType  {@link javafx.scene.control.Alert.AlertType} of the alert
     */
    Dialogue(final String title, final String headerText, final Alert.AlertType alertType) {
        setAlert(new Alert(alertType), title, headerText);
    }


    /**
     * Set {@link Alert} for use by {@link Dialogue}.
     *
     * @param alert      {@link Alert} for use by {@link Dialogue}
     * @param title      title of the {@link Alert}
     * @param headerText header text of the {@link Alert}
     */
    final void setAlert(final Alert alert, final String title, final String headerText) {
        alert.setTitle(title);
        alert.setHeaderText(headerText);

        this.alert = alert;
    }

    /**
     * Get the {@link Alert} the {@link Dialogue} uses.
     *
     * @return {@link Alert} the {@link Dialogue} uses
     */
    final Alert getAlert() {
        return alert;
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
    public final void show() {
        Platform.runLater(() -> show(alert));
    }

    /**
     * Show a dialogue onscreen that prevents further interaction with the UI until closed.
     *
     * @param alert alert to display
     */
    final void show(final Alert alert) {
        alert.showAndWait();
    }
}
