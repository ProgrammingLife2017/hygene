package org.dnacronym.hygene.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Hygene Dialogue. A dialogue is displayed onscreen and prevents any further interaction with the application until
 * it is closed.
 * <p>
 * A dialogue can either be of type {@link HygeneDialogueType#ERROR} or {@link HygeneDialogueType#WARNING}.
 */
public class HygeneDialogue {
    public enum HygeneDialogueType {
        ERROR,
        WARNING
    }

    /**
     * Show a dialogue onscreen. A dialogue prevents any further interaction with the UI until it is closed.
     *
     * @param type           type of warning dialogue.
     * @param exception      exception associated with this dialogue.
     * @param showStackTrace true if you want to show the stacktrace to the user, false otherwise.
     */
    public static void show(final HygeneDialogueType type, final Exception exception, final boolean showStackTrace) {
        switch (type) {
            case ERROR:
                show(Alert.AlertType.ERROR, exception, showStackTrace);
                break;
            case WARNING:
                show(Alert.AlertType.WARNING, exception, showStackTrace);
                break;
        }
    }

    private static void show(final Alert.AlertType alertType, final Exception exception, final boolean showStackTrace) {
        final Alert alert = new Alert(alertType);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Look, an Exception Dialog");
        alert.setContentText("Could not find file blabla.txt!");

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
