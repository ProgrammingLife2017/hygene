package org.dnacronym.hygene.ui.console;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.fxmisc.richtext.StyleClassedTextArea;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the console window.
 */
public final class ConsoleController implements Initializable {
    private static final String HYGENE_CONSOLE_LOGO = new StringBuilder()
            .append("   %......%\n")
            .append("   %      %       __  __   \n")
            .append("    %,,,,%       / / / /_  ______ ____  ____  ___ \n")
            .append("   %      %     / /_/ / / / / __ `/ _ \\/ __ \\/ _ \\\n")
            .append("   %,,,,,,%    / __  / /_/ / /_/ /  __/ / / /  __/\n")
            .append("   %      %   /_/ /_/\\__, /\\__, /\\___/_/ /_/\\___/   \n")
            .append("    %,,,,%          /____//____/ \n")
            .append("   %      %\n")
            .append("   %......%\n\n").toString();

    @Inject
    private GraphVisualizer graphVisualizer;

    @FXML
    private StyleClassedTextArea consoleContent;

    @FXML
    private TextField consoleInput;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        logSelectedSequence();

        consoleContent.setEditable(false);
        consoleContent.setFocusTraversable(false);

        appendLogItem(HYGENE_CONSOLE_LOGO);
    }

    /**
     * When a user clicks on a node in the graph the base pairs of that node will be displayed.
     */
    private void logSelectedSequence() {
        graphVisualizer.getSelectedSegmentProperty().addListener((observable, oldNode, newNode) -> {
            if (newNode != null) {
                appendLogItem(new ConsoleMessage("Selected node " + newNode.getSegmentIds().toString() + "\n"));
            }
        });
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
     * Append a new Console Message to the console.
     *
     * @param message the message
     */
    void appendLogItem(final ConsoleMessage message) {
        if (consoleContent != null) {
            consoleContent.appendText(message.getMessage());
            consoleContent.setStyleClass(0, consoleContent.getCaretPosition(), message.getStyleClass());
        }
    }

    /**
     * Append a new message to the console.
     *
     * @param message the message
     */
    void appendLogItem(final String message) {
        appendLogItem(new ConsoleMessage(message));
    }

    /**
     * Clears the console.
     */
    public void clearConsole() {
        consoleContent.clear();
    }

    /**
     * Handle the user's console input; one {@link KeyEvent} at the time.
     *
     * @param keyEvent the {@link KeyEvent}
     */
    public void handleInput(final KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            final String input = consoleInput.getCharacters().toString();

            appendLogItem("> " + input + "\n");

            parseCommand(input);

            consoleInput.clear();
        }
    }

    /**
     * Parses a command.
     *
     * @param command the command
     */
    public void parseCommand(final String command) {
        switch (command) {
            case "clear":
                clearConsole();
                break;
            case "exit":
                Platform.runLater(Platform::exit);
                break;
            default:
                appendLogItem(command + " is not recognized as an internal or external command.\n");
                break;
        }
    }
}
