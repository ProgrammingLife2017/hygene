package org.dnacronym.hygene.ui.controller.settings;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the border.
 */
public final class AdvancedSettingsViewController extends AbstractSettingsController {
    private static final Logger LOGGER = LogManager.getLogger(AdvancedSettingsViewController.class);

    @FXML
    private CheckBox displayLaneBorders;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        displayLaneBorders.setSelected(getGraphVisualizer().getDisplayBordersProperty().get());
    }

    /**
     * When user clicks on show lane borders {@link CheckBox}.
     */
    @FXML
    void showLaneBordersClicked() {
        getSettings().addRunnable(() -> {
            final boolean newValue = displayLaneBorders.isSelected();
            getGraphVisualizer().getDisplayBordersProperty().setValue(newValue);
            LOGGER.info("Displaying lane borders has now been " + (newValue ? "enabled." : "disabled."));
        });
    }

    /**
     * Gets display lane borders.
     *
     * @return the display lane borders
     */
    CheckBox getDisplayLaneBorders() {
        return displayLaneBorders;
    }

    /**
     * Sets display lane borders.
     *
     * @param displayLaneBorders the display lane borders
     */
    void setDisplayLaneBorders(final CheckBox displayLaneBorders) {
        this.displayLaneBorders = displayLaneBorders;
    }
}
