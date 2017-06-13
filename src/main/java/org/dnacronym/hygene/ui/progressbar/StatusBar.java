package org.dnacronym.hygene.ui.progressbar;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import org.dnacronym.hygene.parser.ProgressUpdater;

import java.util.function.Consumer;


/**
 * Deals with the on-screen progress bar in the application.
 */
public final class StatusBar {
    public static final int PROGRESS_MAX = 100;

    private final DoubleProperty progressProperty;
    private final StringProperty statusProperty;


    /**
     * Creates an instance of {@link StatusBar}.
     */
    public StatusBar() {
        progressProperty = new SimpleDoubleProperty();
        statusProperty = new SimpleStringProperty();
    }


    /**
     * Monitors a given task, and updates the current status and progress accordingly.
     *
     * @param task the {@link Consumer<ProgressUpdater>} to monitor
     */
    public void monitorTask(final Consumer<ProgressUpdater> task) {
        final Task<Void> progressTask = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
                task.accept((progress, message) -> Platform.runLater(() -> {
                    this.updateProgress(progress, PROGRESS_MAX);
                    statusProperty.set(message);
                }));
                return null;
            }
        };

        progressProperty.unbind();
        progressProperty.bind(progressTask.progressProperty());

        new Thread(progressTask).start();
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
