package org.dnacronym.hygene.ui.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.core.Files;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;


/**
 * This class creates progress bars and starts tasks that are measured with the progress bar.
 */
public class ProgressBarController {
    private static final int PROGRESS_TOTAL = 100;
    private static final String PROGRESS_BAR_VIEW = "/ui/view/progress_bar_view.fxml";

    private Stage dialogStage;

    @FXML
    private @MonotonicNonNull ProgressBar progressBar;

    /**
     * Initializes the new {@link Stage} and creates the {@link Scene}.
     */
    public ProgressBarController() {
        this.dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
    }

    /**
     * Creates an instance of the {@link ProgressBarController} and starts executing the task in a new thread.
     *
     * @param task a lambda that accepts an instance of {@link ProgressUpdater}
     * @throws UIInitialisationException if the progress view cannot be loaded
     */
    public static void performTask(final Consumer<ProgressUpdater> task) throws UIInitialisationException {
        final ProgressBarController progressBarController = new ProgressBarController();

        progressBarController.createScene();

        Task<Void> progressTask = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException, IOException, UIInitialisationException {
                task.accept(progress -> {
                    if (progress == 100) {
                        Platform.runLater(() -> progressBarController.dialogStage.close());
                    }
                    updateProgress(progress, PROGRESS_TOTAL);
                });
                return null;
            }
        };

        progressBarController.activateProgressBar(progressTask);

        new Thread(progressTask).start();
    }

    /**
     * Sets the {@link Scene} of the dialog stage.
     *
     * @throws UIInitialisationException if the progress view cannot be loaded
     */
    private void createScene() throws UIInitialisationException {
        try {
            final URL resource = Files.getInstance().getResourceUrl(PROGRESS_BAR_VIEW);
            final FXMLLoader root = new FXMLLoader();
            root.setLocation(resource);
            root.setController(this);

            dialogStage.setScene(new Scene(root.load()));
        } catch (final IOException e) {
            throw new UIInitialisationException("Progress bar view could not be loaded.");
        }
    }

    /**
     * Binds the progress property and displays the dialog.
     *
     * @param task an executable task to measure progress on
     * @throws UIInitialisationException if the progress view cannot be loaded
     */
    private void activateProgressBar(final Task<?> task) throws UIInitialisationException {
        Optional.ofNullable(progressBar).orElseThrow(
                () -> new UIInitialisationException("Progress bar view could not be loaded, progress bar is missing.")
        ).progressProperty().bind(task.progressProperty());

        dialogStage.show();
    }
}
