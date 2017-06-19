package org.dnacronym.hygene.ui.settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Settings controller for more advanced features.
 */
public final class AdvancedSettingsViewController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(AdvancedSettingsViewController.class);

    @Inject
    private Settings settings;
    @Inject
    private GraphVisualizer graphVisualizer;

    @FXML
    private CheckBox displayLaneBorders;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        displayLaneBorders.setSelected(graphVisualizer.getDisplayBordersProperty().get());
    }

    /**
     * When user interacts with the show lane borders {@link CheckBox}.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void showLaneBordersClicked(final ActionEvent actionEvent) {
        settings.addRunnable(() -> {
            final boolean newValue = ((CheckBox) actionEvent.getSource()).isSelected();
            graphVisualizer.getDisplayBordersProperty().setValue(newValue);
            LOGGER.info("Displaying lane borders has now been " + (newValue ? "enabled." : "disabled."));
        });
    }
}
