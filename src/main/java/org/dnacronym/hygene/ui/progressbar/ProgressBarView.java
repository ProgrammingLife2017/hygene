package org.dnacronym.hygene.ui.progressbar;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.core.Files;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public final class ProgressBarView {
    private static final Logger LOGGER = LogManager.getLogger(ProgressBarView.class);

    private static final String PROGRESS_BAR_VIEW = "/ui/progressbar/progress_bar_view.fxml";
    private static final int PROGRESS_TOTAL = 100;

    private final FXMLLoader fxmlLoader;
    private final Stage dialogStage;


    /**
     * Create instance of {@link ProgressBarView}.
     *
     * @throws UIInitialisationException if unable to load {@value PROGRESS_BAR_VIEW}
     */
    public ProgressBarView() throws UIInitialisationException {
        try {
            dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            final URL resource = Files.getInstance().getResourceUrl(PROGRESS_BAR_VIEW);
            fxmlLoader = new FXMLLoader(resource);
        } catch (final IOException e) {
            throw new UIInitialisationException("Progress bar view could not be loaded.", e);
        }
    }

    /**
     * Show {@link Stage}.
     */
    public void show() {
        dialogStage.show();
    }

    /**
     * Creates an instance of the {@link ProgressBarController} and starts executing the task in a new thread.
     *
     * @param task a lambda that accepts an instance of {@link ProgressUpdater}
     */
    public void performTask(final Consumer<ProgressUpdater> task) {
        final Task<Void> progressTask = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException, IOException, UIInitialisationException {
                task.accept(progress -> {
                    if (progress == PROGRESS_TOTAL) {
                        Platform.runLater(dialogStage::close);
                    }
                    this.updateProgress(progress, PROGRESS_TOTAL);
                });
                return null;
            }
        };

        try {
            final Parent root = fxmlLoader.load();
            dialogStage.setScene(new Scene(root));

            final ProgressBarController progressBarController = fxmlLoader.getController();
            progressBarController.activateProgressBar(progressTask);

            new Thread(progressTask).start();
        } catch (final IOException e) {
            LOGGER.error("Unable to load progress bar view.", e);
        }
    }
}
