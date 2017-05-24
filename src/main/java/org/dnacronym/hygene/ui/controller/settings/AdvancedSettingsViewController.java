package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the border.
 */
public final class AdvancedSettingsViewController extends AbstractSettingsController implements Initializable {
    protected static final Logger LOGGER = LogManager.getLogger(AdvancedSettingsViewController.class);

    @FXML
    private CheckBox displayLaneBorders;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        super.initialize(location, resources);
        displayLaneBorders.setSelected(graphVisualizer.getDisplayBordersProperty().get());
    }

    /**
     * When user clicks on show lane borders {@link CheckBox}.
     */
    @FXML
    void showLaneBordersClicked() {
        settings.addRunnable(() -> {
            final boolean newValue = displayLaneBorders.isSelected();
            graphVisualizer.getDisplayBordersProperty().setValue(newValue);
        });
    }
}
