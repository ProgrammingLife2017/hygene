package org.dnacronym.hygene.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.ui.console.ConsoleMessage;
import org.dnacronym.hygene.ui.console.JFXAppender;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.visualizer.GraphVisualizer;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the console window.
 */
public final class ConsoleController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ConsoleController.class);

    private @MonotonicNonNull GraphVisualizer graphVisualizer;

    @FXML
    private @MonotonicNonNull TextFlow consoleTextFlow;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        try {
            setGraphVisualizer(Hygene.getInstance().getGraphVisualizer());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialise ConsoleController.", e);
            return;
        }

        JFXAppender.getLatestLogEvent().addListener((observable, oldValue, newValue) -> appendLogItem(newValue));

        if (graphVisualizer != null) {
            graphVisualizer.getSelectedNodeProperty().addListener((observable, oldNode, newNode) ->
                    appendLogItem(new ConsoleMessage("Sequence: " + newNode.getSequence())));
        }
    }

    /**
     * Set the {@link GraphVisualizer} in the controller.
     *
     * @param graphVisualizer {@link GraphVisualizer} to store in the {@link ConsoleController}.
     */
    void setGraphVisualizer(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
    }

    /**
     * Append a new Console Message to the consoleTextFlow.
     *
     * @param message the message
     */
    void appendLogItem(final ConsoleMessage message) {

        if (consoleTextFlow != null) {
            consoleTextFlow.getChildren().add(message.getNode());
        }
    }
}
