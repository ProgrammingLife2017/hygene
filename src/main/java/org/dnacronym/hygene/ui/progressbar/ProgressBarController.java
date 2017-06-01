package org.dnacronym.hygene.ui.progressbar;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * This class creates progress bars and starts tasks that are measured with the progress bar.
 */
public final class ProgressBarController implements Initializable {
    @FXML
    public Text progressText;
    @FXML
    private ProgressBar progressBar;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // There is nothing to initialize
    }

    /**
     * Binds the progress property and displays the dialog.
     *
     * @param task an executable task to measure progress on
     */
    void activateProgressBar(final Task<?> task) {
        progressBar.progressProperty().bind(task.progressProperty());
    }

    /**
     * Updates the progress text.
     *
     * @param text the text to be displayed
     */
    void updateProgressText(final String text) {
        progressText.setText(text);
    }
}
