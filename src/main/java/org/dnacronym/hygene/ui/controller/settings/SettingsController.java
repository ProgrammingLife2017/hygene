package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.dnacronym.hygene.core.Files;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;
import org.dnacronym.hygene.ui.store.Settings;

import java.net.URL;


/**
 * Controller of the display window.
 */
public final class SettingsController {
    private static final Logger LOGGER = LogManager.getLogger(SettingsController.class);
    private static final String TITLE = "Settings";
    private static final String SETTINGS_VIEW = "/ui/view/setting/setting_view.fxml";

    private @MonotonicNonNull Settings settings;

    private @MonotonicNonNull Stage stage;


    /**
     * Create a new instance of a {@link SettingsController}.
     */
    public SettingsController() {
        try {
            settings = Hygene.getInstance().getSettings();
            final Stage primaryStage = Hygene.getInstance().getPrimaryStage();

            stage = new Stage();
            stage.setTitle(TITLE);

            final URL resource = Files.getInstance().getResourceUrl(SETTINGS_VIEW);
            final Parent parent = FXMLLoader.load(resource);
            if (parent == null) {
                throw new UIInitialisationException("Root of Settings could not be found.");
            }

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(parent));

            stage.show();
        } catch (Exception e) {
            LOGGER.error("Unable to initialize SettingsController.", e);
        }
    }

    /**
     * Show the settings window.
     */
    public void show() {
        stage.show();
    }
}
