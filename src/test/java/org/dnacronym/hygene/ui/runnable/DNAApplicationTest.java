package org.dnacronym.hygene.ui.runnable;

import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;

import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


/**
 * Unit tests for {@link DNAApplication}s.
 */
public class DNAApplicationTest extends FxRobot {
    private DNAApplication application;
    private Stage primaryStage;


    /**
     * Set up application before each test.
     * Afterwards, calls the {@link #overridableBeforeEach()} method.
     *
     * @throws TimeoutException if unable to set up application.
     * @see FxToolkit#setupApplication(Class, String...)
     */
    @BeforeEach
    public final void beforeEach() throws TimeoutException {
        this.primaryStage = FxToolkit.registerPrimaryStage();
        this.application = (DNAApplication) FxToolkit.setupApplication(DNAApplication.class);

        FxToolkit.showStage();

        overridableBeforeEach();
    }

    /**
     * Hide the application after each test.
     * Afterwards, calls the {@link #overridableAfterEach()} method.
     *
     * @throws TimeoutException if unable to hide application.
     */
    @AfterEach
    public final void afterEach() throws TimeoutException, UIInitialisationException {
        FxToolkit.cleanupApplication(DNAApplication.getInstance());
        FxToolkit.cleanupStages();

        overridableAfterEach();
    }


    /**
     * This method is called after the {@link #beforeEach()} method.
     * If this method is not overridden it doesn't do anything.
     */
    public void overridableBeforeEach() {
    }

    /**
     * This method is called after the {@link #afterEach()} method.
     * If this method is not overridden it doesn't do anything.
     */
    public void overridableAfterEach() {
    }

    /**
     * Get the application used for UI testing.
     *
     * @return {@link DNAApplication} used by the UI tests.
     */
    public final DNAApplication getApplication() {
        return application;
    }


    @Test
    public final void testGetApplication() throws UIInitialisationException {
        assertThat(DNAApplication.getInstance()).isEqualTo(getApplication());
        assertThat(DNAApplication.getInstance().getPrimaryStage()).isEqualTo(primaryStage);
    }
}
