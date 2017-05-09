package org.dnacronym.hygene.ui;

import javafx.stage.Stage;
import org.dnacronym.hygene.ui.runnable.DNAApplication;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;

import java.util.concurrent.TimeoutException;


/**
 * Abstract test class for UI testing. Initializes and closes and application instance before and after each unit test
 * in test classes that inherit from this class.
 */
public abstract class UITest extends FxRobot {
    private DNAApplication application;
    private Stage primaryStage;


    /**
     * Set up application before each test.
     * Afterwards, calls the {@link #beforeEach()} method.
     *
     * @throws TimeoutException if unable to set up application.
     * @see FxToolkit#setupApplication(Class, String...)
     */
    @BeforeEach
    public final void basicBeforeEach() throws TimeoutException {
        this.primaryStage = FxToolkit.registerPrimaryStage();
        this.application = (DNAApplication) FxToolkit.setupApplication(DNAApplication.class);

        FxToolkit.showStage();

        beforeEach();
    }

    /**
     * Hide the application after each test.
     * Afterwards, calls the {@link #afterEach()} method.
     *
     * @throws TimeoutException if unable to hide application.
     */
    @AfterEach
    public final void basicAfterEach() throws TimeoutException, UIInitialisationException {
        FxToolkit.cleanupApplication(DNAApplication.getInstance());
        FxToolkit.cleanupStages();

        afterEach();
    }


    /**
     * This method is called after the {@link #basicBeforeEach()} method.
     * If this method is not overridden it doesn't do anything.
     */
    public void beforeEach() {
    }

    /**
     * This method is called after the {@link #basicAfterEach()} method.
     * If this method is not overridden it doesn't do anything.
     */
    public void afterEach() {
    }

    /**
     * Get the {@link DNAApplication} used for UI testing.
     *
     * @return {@link DNAApplication} used for UI testing.
     */
    public final DNAApplication getApplication() {
        return application;
    }

    /**
     * Get the {@link Stage} used for UI testing.
     *
     * @return {@link Stage} used for UI testing.
     */
    public final Stage getPrimaryStage() {
        return primaryStage;
    }
}
