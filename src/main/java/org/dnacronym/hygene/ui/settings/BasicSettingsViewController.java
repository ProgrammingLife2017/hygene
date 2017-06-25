package org.dnacronym.hygene.ui.settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.colorscheme.ColorScheme;
import org.dnacronym.hygene.graph.colorscheme.fixed.FixedColorScheme;
import org.dnacronym.hygene.graph.colorscheme.minmax.ColorSchemeIncomingEdges;
import org.dnacronym.hygene.graph.colorscheme.minmax.ColorSchemeOutgoingEdges;
import org.dnacronym.hygene.graph.colorscheme.minmax.ColorSchemeSequenceLength;
import org.dnacronym.hygene.graph.colorscheme.minmax.ColorSchemeTotalEdges;
import org.dnacronym.hygene.ui.graph.GraphMovementCalculator;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;

import javax.inject.Inject;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * Settings controller for the basic settings.
 */
public final class BasicSettingsViewController implements Initializable {
    private static final Color MIN_COLOR = Color.ALICEBLUE;
    private static final Color MAX_COLOR = Color.rgb(3, 73, 58);
    public static final List<Pair<String, ColorScheme>> NODE_COLOR_SCHEMES = Collections.unmodifiableList(Arrays.asList(
            new Pair<>("Total Number of Edges", new ColorSchemeTotalEdges(10, MIN_COLOR, MAX_COLOR)),
            new Pair<>("Fixed Color", new FixedColorScheme(MAX_COLOR)),
            new Pair<>("Number of Incoming Edges", new ColorSchemeIncomingEdges(5, MIN_COLOR, MAX_COLOR)),
            new Pair<>("Number of Outgoing Edges", new ColorSchemeOutgoingEdges(5, MIN_COLOR, MAX_COLOR)),
            new Pair<>("Length of Sequence", new ColorSchemeSequenceLength(5000, MIN_COLOR, MAX_COLOR))
    ));

    private static final Logger LOGGER = LogManager.getLogger(BasicSettingsViewController.class);

    @Inject
    private Settings settings;
    @Inject
    private GraphVisualizer graphVisualizer;
    @Inject
    private GraphMovementCalculator graphMovementCalculator;

    @FXML
    private Slider nodeHeight;
    @FXML
    private ComboBox<Pair<String, ColorScheme>> nodeColorScheme;
    @FXML
    private ColorPicker edgeColor;
    @FXML
    private Slider panningSensitivity;
    @FXML
    private Slider zoomingSensitivity;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        nodeHeight.setValue(graphVisualizer.getNodeHeightProperty().get());
        edgeColor.setValue(graphVisualizer.getEdgeColorProperty().get());
        panningSensitivity.setValue(graphMovementCalculator.getPanningSensitivityProperty().get());
        zoomingSensitivity.setValue(graphMovementCalculator.getZoomingSensitivityProperty().get());

        setUpNodeColorSchemeComboBox();
    }

    /**
     * Sets up the {@link ComboBox} listing all node color scheme choices.
     */
    private void setUpNodeColorSchemeComboBox() {
        nodeColorScheme.setConverter(new StringConverter<Pair<String, ColorScheme>>() {
            @Override
            public String toString(final Pair<String, ColorScheme> object) {
                return object.getKey();
            }

            @Override
            public Pair<String, ColorScheme> fromString(final String string) {
                return NODE_COLOR_SCHEMES.stream()
                        .filter(pair -> pair.getKey().equals(string))
                        .collect(Collectors.toList()).get(0);
            }
        });
        nodeColorScheme.getItems().addAll(NODE_COLOR_SCHEMES);
        nodeColorScheme.getSelectionModel().selectFirst();
    }

    /**
     * When user finishes sliding the node height {@link Slider}.
     *
     * @param mouseEvent the {@link MouseEvent}
     */
    @FXML
    void nodeHeightSliderDone(final MouseEvent mouseEvent) {
        settings.addRunnable(() -> {
            final double newValue = ((Slider) mouseEvent.getSource()).getValue();
            graphVisualizer.getNodeHeightProperty().setValue(newValue);
            LOGGER.info("Node height has now been set to " + newValue + ".");
        });
    }

    /**
     * When the user finishes picking the color for edges in the {@link ColorPicker}.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void edgeColorDone(final ActionEvent actionEvent) {
        settings.addRunnable(() -> {
            final Color newValue = ((ColorPicker) actionEvent.getSource()).getValue();
            graphVisualizer.getEdgeColorProperty().setValue(newValue);
            LOGGER.info("Edge color has now been set to " + newValue + ".");
        });
    }

    /**
     * When the user finishes sliding the panning sensitivity {@link Slider}.
     *
     * @param mouseEvent the {@link MouseEvent}
     */
    @FXML
    void panningSensitivitySliderDone(final MouseEvent mouseEvent) {
        settings.addRunnable(() -> {
            final double newValue = ((Slider) mouseEvent.getSource()).getValue();
            graphMovementCalculator.getPanningSensitivityProperty().setValue(newValue);
            LOGGER.info("Panning sensitivity has been set to " + newValue + ".");
        });
    }

    /**
     * When the user finishes sliding the zooming sensitivity {@link Slider}.
     *
     * @param mouseEvent {@link MouseEvent} associated with this event
     */
    @FXML
    void zoomingSensitivitySliderDone(final MouseEvent mouseEvent) {
        settings.addRunnable(() -> {
            final double newValue = ((Slider) mouseEvent.getSource()).getValue();
            graphMovementCalculator.getZoomingSensitivityProperty().setValue(newValue);
            LOGGER.info("Zooming sensitivity has been set to " + newValue + ".");
        });
    }

    /**
     * When the user selects a {@link ColorScheme} from the {@link ComboBox}.
     *
     * @param actionEvent the event
     */
    @FXML
    void onNodeColorSchemeChanged(final ActionEvent actionEvent) {
        settings.addRunnable(() -> {
            Node.setColorScheme(nodeColorScheme.getValue().getValue());
            LOGGER.info("Node color scheme has been set to " + nodeColorScheme.getValue().getKey() + ".");
            graphVisualizer.draw();
        });
    }
}
