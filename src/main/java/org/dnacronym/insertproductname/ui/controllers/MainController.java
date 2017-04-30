package org.dnacronym.insertproductname.ui.controllers;

import javafx.stage.Stage;

/**
 * Main Controller of the application. Only servers the store the stage of the application.
 */
public class MainController {

    private final Stage stage;

    /**
     * Construct a new main controller. Used by controllers that inherit from this to directly access the
     * stage.
     *
     * @param stage Primary stage of the application.
     */
    public MainController(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Get the stage stored in the main controller. Stage should be set to the primary {@link Stage} from the
     * {@link org.dnacronym.insertproductname.ui.runnable.DNAApplication} class.
     *
     * @return Primary stage of application.
     */
    final Stage getStage() {
        return stage;
    }
}
