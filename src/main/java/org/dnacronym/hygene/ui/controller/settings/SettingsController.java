package org.dnacronym.hygene.ui.controller.settings;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import java.util.ResourceBundle;


/**
 * Controller of the display window.
 */
public final class SettingsController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(SettingsController.class);
    private static final String TITLE = "Settings";
    private static final String SETTINGS_VIEW = "/ui/view/setting/setting_view.fxml";

    private @MonotonicNonNull Settings settings;

    private @MonotonicNonNull Stage primaryStage;
    private @MonotonicNonNull Stage stage;

    @FXML
    private @MonotonicNonNull Button apply;


    /**
     *
     */
    public SettingsController() {
        try {
            settings = Hygene.getInstance().getSettings();
            primaryStage = Hygene.getInstance().getPrimaryStage();
        } catch (Exception e) {
            LOGGER.error("Unable to initialize SettingsController.", e);
            return;
        }
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        if (apply != null) {
            apply.disableProperty().bind(Bindings.isEmpty(settings.getCommands()));
        }
    }

    /**
     * Show the settings window.
     */
    public void show() {
        final URL resource;
        try {
            resource = Files.getInstance().getResourceUrl(SETTINGS_VIEW);
            final FXMLLoader loader = new FXMLLoader(resource);
            final Parent parent = loader.load();
            if (parent == null) {
                throw new UIInitialisationException("Root of Settings could not be found.");
            }

            stage = new Stage();
            stage.setTitle(TITLE);

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            stage.setScene(new Scene(parent));

            stage.show();
        } catch (Exception e) {
            LOGGER.error("Unable to show settings view.", e);
        }
    }

    /**
     *
     */
    @FXML
    void okAction() {
        settings.executeAll();
        if (stage != null) {
            stage.close();
        }
    }

    /**
     *
     */
    @FXML
    void cancelAction() {
        settings.clearAll();
        if (stage != null) {
            stage.close();
        }
    }

    /**
     *
     */
    @FXML
    void applyAction() {
        settings.executeAll();
    }
}
