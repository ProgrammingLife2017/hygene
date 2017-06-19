package org.dnacronym.hygene.ui;

import com.gluonhq.ignite.guice.GuiceContext;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


/**
 * Abstract test class for UI testing. Initializes and closes and application instance before and after each unit test
 * in test classes that inherit from this class.
 */
public abstract class UITestBase extends FxRobot {
    private Hygene application;
    private Stage primaryStage;
    private GuiceContext context;


    /**
     * Set up application before each test.
     * Afterwards, calls the {@link #beforeEach()} method.
     *
     * @throws TimeoutException if unable to set up application
     * @throws UIInitialisationException if ui was not properly initialized
     * @see FxToolkit#setupApplication(Class, String...)
     */
    @BeforeEach
    public final void basicBeforeEach() throws TimeoutException, UIInitialisationException {
        this.primaryStage = FxToolkit.registerPrimaryStage();
        this.application = (Hygene) FxToolkit.setupApplication(Hygene.class);

        this.context = Hygene.getInstance().getContext();

        FxToolkit.showStage();

        beforeEach();
    }

    /**
     * Hide the application after each test.
     * Afterwards, calls the {@link #afterEach()} method.
     *
     * @throws TimeoutException if unable to hide application
     */
    @AfterEach
    public final void basicAfterEach() throws TimeoutException, UIInitialisationException {
        FxToolkit.cleanupApplication(Hygene.getInstance());
        FxToolkit.cleanupStages();

        afterEach();
    }


    /**
     * This method is called after the {@link #basicBeforeEach()} method.
     * If this method is not overridden it doesn't do anything.
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract") // Implementation is optional
    public void beforeEach() {
        // Does nothing by default
    }

    /**
     * This method is called after the {@link #basicAfterEach()} method.
     * If this method is not overridden it doesn't do anything.
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract") // Implementation is optional
    public void afterEach() {
        // Does nothing by default
    }

    /**
     * Get the {@link Hygene} used for UI testing.
     *
     * @return {@link Hygene} used for UI testing
     */
    public final Hygene getApplication() {
        return application;
    }

    /**
     * Get the {@link Stage} used for UI testing.
     *
     * @return {@link Stage} used for UI testing
     */
    public final Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Injects members of the object annotated with {@code @Inject}.
     *
     * @param instance the object of which the members need to be injected
     */
    protected final void injectMembers(final Object instance) {
        final CompletableFuture<Object> future = new CompletableFuture<>();

        Platform.runLater(() -> {
            context.injectMembers(instance);

            future.complete(null);
        });

        try {
            future.get();
        } catch (final InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates instance of the given class.
     *
     * @param className name of the class
     * @param <T>       type of the class
     * @return instance of the given class
     */
    protected final <T> T createInstance(final Class<T> className) {
        return context.getInstance(className);
    }

    /**
     * Creates a context based on the fields of the current test.
     */
    protected final void createContextOfTest() {
        context = new GuiceContext(this, () -> Arrays.asList(BoundFieldModule.of(this)));
        context.init();
    }
}
