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
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;


/**
 * Wrapper class that creates a stage for displaying of a progressbar to the user whilst the file loads.
 */
public final class ProgressBarView {
    private static final Logger LOGGER = LogManager.getLogger(ProgressBarView.class);

    private static final String PROGRESS_BAR_VIEW = "/ui/progressbar/progress_bar_view.fxml";
    private static final int PROGRESS_TOTAL = 100;

    private FXMLLoader fxmlLoader;
    private Stage stage;
    private ProgressBarController progressBarController;


    /**
     * Create instance of {@link ProgressBarView}.
     */
    public ProgressBarView() {
        try {
            final Stage newStage = new Stage();
            newStage.setResizable(false);

            final URL resource = getClass().getResource(PROGRESS_BAR_VIEW);
            fxmlLoader = new FXMLLoader(resource);

            final Stage primaryStage = Hygene.getInstance().getPrimaryStage();
            newStage.initOwner(primaryStage);
            newStage.initStyle(StageStyle.UTILITY);
            newStage.initModality(Modality.APPLICATION_MODAL);

            final double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2;
            final double centerYPosition = primaryStage.getY() + primaryStage.getHeight() / 2;

            newStage.setOnShowing(event -> newStage.hide());
            newStage.setOnShown(event -> {
                newStage.setX(centerXPosition - newStage.getWidth() / 2);
                newStage.setY(centerYPosition - newStage.getHeight() / 2);
                newStage.show();
            });

            setStage(newStage);
        } catch (final UIInitialisationException e) {
            LOGGER.error("Progress bar view could not be loaded.", e);
        }
    }


    /**
     * Sets the {@link Stage}.
     *
     * @param stage {@link Stage} for use by the view
     */
    void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the {@link ProgressBarController}.
     *
     * @param progressBarController {@link ProgressBarController} for use by the view
     */
    void setController(final ProgressBarController progressBarController) {
        this.progressBarController = progressBarController;
    }

    /**
     * Show {@link Stage}.
     */
    public void show() {
        stage.show();
    }

    /**
     * Creates an instance of the {@link ProgressBarController} and starts executing the task in a new thread.
     *
     * @param task a lambda that accepts an instance of {@link ProgressUpdater}
     */
    public void monitorTask(final Consumer<ProgressUpdater> task) {
        final Task<Void> progressTask = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException, IOException, UIInitialisationException {
                task.accept((progress, message) -> {
                    if (progress == PROGRESS_TOTAL) {
                        Platform.runLater(stage::close);
                    }
                    this.updateProgress(progress, PROGRESS_TOTAL);

                    progressBarController.updateProgressText(message);
                });
                return null;
            }
        };

        initialize(progressTask);
    }

    /**
     * Initialize the given {@link Task}, and set the {@link Scene} of the {@link Stage}.
     *
     * @param progressTask {@link Task} to initialize
     */
    private void initialize(final Task<Void> progressTask) {
        try {
            final Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root));

            setController(fxmlLoader.getController());
            progressBarController.activateProgressBar(progressTask);

            new Thread(progressTask).start();
        } catch (final IOException e) {
            LOGGER.error("Unable to load progress bar view.", e);
        }
    }
}
