package org.dnacronym.hygene.ui.menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.console.ConsoleView;
import org.dnacronym.hygene.ui.help.HelpMenuView;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the tools menu.
 */
public final class ToolsMenuController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(ToolsMenuController.class);

    @Inject
    private ConsoleView consoleView;
    private HelpMenuView helpMenuView;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // Nothing to initialize
    }

    /**
     * Opens an independent stage showing the console window.
     *
     * @param actionEvent {@link ActionEvent} associated with the event
     * @throws IOException if unable to located the FXML resource
     */
    @FXML
    void openConsoleAction(final ActionEvent actionEvent) throws IOException {
        consoleView.bringToFront();

        actionEvent.consume();
    }

    /**
     * Opens an independent stage showing the help menu.
     *
     * @param actionEvent {@link ActionEvent} associated with the event
     * @throws IOException if unable to locate FXML resource
     */
    public void openHelpAction(final ActionEvent actionEvent) throws IOException {
        try {
            if (helpMenuView == null) {
                helpMenuView = new HelpMenuView();
                LOGGER.info("Launched GUI help menu");
            }

            helpMenuView.bringToFront();
        } catch (final UIInitialisationException e) {
            LOGGER.error(e);
        }

        actionEvent.consume();
    }

    /**
     * Returns the {@link ConsoleView} attached to this menu.
     *
     * @return the {@link ConsoleView}
     */
    public ConsoleView getConsoleView() {
        return consoleView;
    }

    /**
     * Gets the {@link HelpMenuView}.
     *
     * @return the {@link HelpMenuView}
     */
    public HelpMenuView getHelpMenuView() {
        return helpMenuView;
    }
}
