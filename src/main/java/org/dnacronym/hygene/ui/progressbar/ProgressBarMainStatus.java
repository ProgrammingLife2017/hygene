package org.dnacronym.hygene.ui.progressbar;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;


/**
 * Deals with the on-screen progress bar in the application.
 */
public class ProgressBarMainStatus {
    private final DoubleProperty progressProperty;
    private final StringProperty statusProperty;


    /**
     * Creates an instance of {@link ProgressBarMainStatus}.
     */
    public ProgressBarMainStatus() {
        progressProperty = new SimpleDoubleProperty();
        statusProperty = new SimpleStringProperty();
    }


    /**
     * Binds the progress property and displays the dialog.
     *
     * @param task an executable task to measure progress on
     */
    void activateProgressBar(final Task<?> task) {
        progressProperty.bind(task.progressProperty());
    }

    /**
     * Updates the progress text.
     *
     * @param text the text to be displayed
     */
    void updateProgressText(final String text) {
        statusProperty.set(text);
    }

    /**
     * The {@link ReadOnlyDoubleProperty} which decides the current progress of the last set task.
     *
     * @return the {@link ReadOnlyDoubleProperty} which decides the current progress of the current task
     */
    public ReadOnlyDoubleProperty getProgressProperty() {
        return progressProperty;
    }

    /**
     * The {@link ReadOnlyStringProperty} which decides the current status of the current task.
     *
     * @return the {@link ReadOnlyStringProperty} which decides the current status of the current task
     */
    public ReadOnlyStringProperty getStatusProperty() {
        return statusProperty;
    }
}
