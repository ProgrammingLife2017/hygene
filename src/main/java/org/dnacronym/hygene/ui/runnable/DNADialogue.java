package org.dnacronym.hygene.ui.runnable;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.core.Files;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Hygene Dialogue. A dialogue is displayed onscreen and prevents any further interaction with the application until
 * it is closed.
 * <p>
 * A dialogue can either be of type {@link HygeneDialogueType#ERROR} or {@link HygeneDialogueType#WARNING}.
 */
public final class DNADialogue {
    private static volatile @MonotonicNonNull DNADialogue instance;

    /**
     * Denotes the type of dialogue. A error is more severe, and should be used to relay to the user that something went
     * wrong with the application. A warning is less severe, and should be used to relay to the user that they did
     * something they shouldn't do.
     */
    public enum HygeneDialogueType {
        ERROR,
        WARNING
    }


    /**
     * Prevent instantiation of {@link DNADialogue}.
     */
    private DNADialogue() {
    }


    /**
     * Gets the instance of {@link DNADialogue}.
     *
     * @return the instance of {@link DNADialogue}.
     */
    public static DNADialogue getInstance() {
        if (instance == null) {
            synchronized (Files.class) {
                if (instance == null) {
                    instance = new DNADialogue();
                }
            }
        }
        return instance;
    }

    /**
     * Show a dialogue onscreen. A dialogue prevents any further interaction with the UI until it is closed. Uses the
     * {@link Alert} from JavaFX.
     * <p>
     * An error dialogue should relay to the user that something went wrong with the application. A warning dialogue
     * should relay to the user that they did something they shouldn't do (i.e. trying to perform certain operations
     * without loading a graph).
     *
     * @param type           type of warning dialogue.
     * @param exception      exception associated with this dialogue. The message in the exception determines what is
     *                       displayed in the header of the dialogue.
     * @param showStackTrace true if you want to show the stacktrace to the user, false otherwise.
     */
    public void show(final HygeneDialogueType type, final Exception exception, final boolean showStackTrace) {
        final String headerText = exception.getMessage();
        if (type == HygeneDialogueType.ERROR) {
            show(Alert.AlertType.ERROR, exception, showStackTrace, "Error!", headerText);
        } else {
            show(Alert.AlertType.WARNING, exception, showStackTrace, "Warning!", headerText);
        }
    }

    /**
     * Show a dialogue onscreen that prevents further interaction with the UI until closed.
     *
     * @param alertType      type of warning dialogue.
     * @param exception      exception associated with this dialogue.
     * @param showStackTrace true if you want to show the stacktrace to the user, false otherwise.
     * @param title          title of dialogue.
     * @param headerText     text to display in header of dialogue.
     * @see Alert
     */
    private void show(final Alert.AlertType alertType, final Exception exception, final boolean showStackTrace,
                      final String title, final @Nullable String headerText) {
        final Alert alert = new Alert(alertType);
        alert.setTitle(title);

        if (headerText != null) {
            alert.setHeaderText(headerText);
        }

        if (showStackTrace) {
            final Label label = new Label("The exception stacktrace was:");

            final StringWriter stringWriter = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(printWriter);
            final String exceptionText = stringWriter.toString();

            final TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            final GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);
        }

        alert.showAndWait();
    }
}
