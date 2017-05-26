package org.dnacronym.hygene.ui.console;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dnacronym.hygene.ui.dialogue.ErrorDialogue;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Controller for the console window.
 */
public final class ConsoleController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ConsoleController.class);

    private GraphVisualizer graphVisualizer;

    @FXML
    private StyleClassedTextArea consoleContent;

    @FXML
    private TextField consoleInput;


    /**
     * Create instance of {@link ConsoleController}.
     */
    public ConsoleController() {
        try {
            setGraphVisualizer(Hygene.getInstance().getGraphVisualizer());
        } catch (final UIInitialisationException e) {
            LOGGER.error("Failed to initialise ConsoleController.", e);
            new ErrorDialogue(e).show();
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        JFXAppender.getLatestLogEvent().addListener((observable, oldValue, newValue) -> appendLogItem(newValue));

        enableAutoScrolling();

        logSelectedSequence();
    }

    /**
     * Allows the text the console window to automatically scroll down when new input is introduced.
     */
    private void enableAutoScrolling() {
//        consoleTextFlow.getChildren().addListener(
//                (ListChangeListener<Node>) (change -> {
//                    consoleTextFlow.layout();
//                    consoleScrollPane.layout();
//                    consoleScrollPane.setVvalue(1.0f);
//                }));
//        consoleScrollPane.setContent(consoleTextFlow);
    }

    /**
     * When a user clicks on a node in the graph the base pairs of that node will be displayed.
     */
    private void logSelectedSequence() {
        graphVisualizer.getSelectedNodeProperty().addListener((observable, oldNode, newNode) ->
                Optional.ofNullable(newNode).ifPresent(node ->
                        appendLogItem(new ConsoleMessage("Sequence: " + node.getSequenceLength() + "\n"))));
    }

    /**
     * Set the {@link GraphVisualizer} in the controller.
     *
     * @param graphVisualizer {@link GraphVisualizer} to store in the {@link ConsoleController}
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
        consoleContent.appendText("dwadwd");
        consoleContent.setC
//        if (consoleTextFlow != null) {
//            consoleTextFlow.appendText(message.toString());
//        }
    }

    /**
     * Handle the user's console input; one {@link KeyEvent} at the time.
     *
     * @param keyEvent the {@link KeyEvent}
     */
    public void handleInput(@NonNull final KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            final String input = consoleInput.getCharacters().toString();

            consoleInput.clear();

            // Todo: Handle commands
            appendLogItem(new ConsoleMessage("> " + input + "\n"));
        }
    }
}
