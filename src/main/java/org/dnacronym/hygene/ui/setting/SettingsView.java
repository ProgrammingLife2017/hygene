package org.dnacronym.hygene.ui.setting;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.core.Files;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.net.URL;


/**
 * Controller of the display window.
 */
public final class SettingsView {
    private static final Logger LOGGER = LogManager.getLogger(SettingsView.class);
    private static final String TITLE = "Settings";
    private static final String SETTINGS_VIEW = "/ui/setting/setting_view.fxml";

    private Stage stage;


    /**
     * Create a new instance of a {@link SettingsView}.
     *
     * @param settings {@link Settings} of the application
     */
    public SettingsView(final Settings settings) {
        try {
            final Stage primaryStage = Hygene.getInstance().getPrimaryStage();

            final Stage newStage = new Stage();
            newStage.setResizable(false);
            newStage.setTitle(TITLE);

            final URL resource = Files.getInstance().getResourceUrl(SETTINGS_VIEW);
            final Parent parent = FXMLLoader.load(resource);
            if (parent == null) {
                throw new UIInitialisationException("Root of Settings could not be found.");
            }

            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.initOwner(primaryStage);
            newStage.setScene(new Scene(parent));

            // TODO show prompt box asking the user if they want to close if they have unsaved setting.
            newStage.setOnCloseRequest(request -> settings.clearAll());

            setStage(newStage);
        } catch (final Exception e) {
            LOGGER.error("Unable to initialize SettingsView.", e);
        }
    }


    /**
     * Gets the stage.
     *
     * @return the stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Set the stage.
     *
     * @param stage stage for use by the view
     */
    void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Show the setting window.
     */
    public void show() {
        stage.show();
    }
}
