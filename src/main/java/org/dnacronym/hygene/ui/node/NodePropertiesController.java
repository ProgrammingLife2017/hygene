package org.dnacronym.hygene.ui.node;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.graph.layout.FafospLayerer;
import org.dnacronym.hygene.graph.annotation.Annotation;
import org.dnacronym.hygene.graph.node.GfaNode;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.ui.graph.GraphAnnotation;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the node properties window. Shows the properties of the selected node.
 */
public final class NodePropertiesController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(NodePropertiesController.class);

    @Inject
    private GraphVisualizer graphVisualizer;
    @Inject
    private SequenceVisualizer sequenceVisualizer;
    @Inject
    private GraphAnnotation graphAnnotation;

    @FXML
    private Label nodeId;
    @FXML
    private Label sequencePreview;
    @FXML
    private Label leftNeighbours;
    @FXML
    private Label rightNeighbours;
    @FXML
    private Label position;
    @FXML
    private TableView<Annotation> annotationTable;
    @FXML
    private TableColumn<Annotation, String> nameAnnotation;
    @FXML
    private TableColumn<Annotation, String> typeAnnotation;
    @FXML
    private TableColumn<Annotation, Color> colorAnnotation;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final ObjectProperty<GfaNode> selectedNodeProperty = graphVisualizer.getSelectedSegmentProperty();

        nameAnnotation.setCellValueFactory(cell -> {
            final String[] name = cell.getValue().getAttributes().get("Name");
            return new SimpleStringProperty(name == null ? "" : name[0]);
        });
        nameAnnotation.setCellFactory(this::wrappableTableCell);

        typeAnnotation.setCellValueFactory(cell -> {
            final String type = cell.getValue().getType();
            return new SimpleStringProperty(type == null ? "" : type);
        });
        typeAnnotation.setCellFactory(this::wrappableTableCell);

        colorAnnotation.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getColor()));
        colorAnnotation.setCellFactory(column -> new TableCell<Annotation, Color>() {
            @Override
            protected void updateItem(final Color color, final boolean empty) {
                super.updateItem(color, empty);
                if (color == null || empty) {
                    setBackground(Background.EMPTY);
                } else {
                    setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
                }
            }
        });

        selectedNodeProperty.addListener((observable, oldNode, newNode) -> updateFields(newNode));
    }


    /**
     * Create a table cell that wraps the text inside.
     *
     * @param param the table column
     * @return a table cell that wraps the text inside
     */
    TableCell<Annotation, String> wrappableTableCell(final TableColumn<Annotation, String> param) {
        return new TableCell<Annotation, String>() {
            @Override
            protected void updateItem(final String item, final boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    return;
                }
                final Text text = new Text(item);
                text.setWrappingWidth(param.getWidth());
                setPrefHeight(text.getLayoutBounds().getHeight());
                setGraphic(text);
            }
        };
    }

    /**
     * Set the {@link GraphVisualizer}, whose selected node can be bound to the UI elements in the controller.
     *
     * @param graphVisualizer {@link GraphVisualizer} who's selected node we are interested in
     * @see GraphVisualizer#selectedSegmentProperty
     */
    void setGraphVisualiser(final GraphVisualizer graphVisualizer) {
        this.graphVisualizer = graphVisualizer;
    }

    /**
     * Updates the fields that describe the properties of the {@link GfaNode}.
     * <p>
     * If this {@link GfaNode} is {@code null}, the fields are simply cleared.
     *
     * @param node the {@link GfaNode} whose properties should be displayed
     */
    void updateFields(final GfaNode node) {
        if (node == null) {
            clearNodeFields();
            return;
        }

        if (node.hasMetadata()) {
            nodeId.setText(node.getMetadata().getName());
            sequencePreview.setText(String.valueOf(node.getMetadata().getSequence()));
        } else {
            LOGGER.error("Node " + node.getSegmentIds().toString() + " does not have metadata.");
        }

        leftNeighbours.setText(String.valueOf(node.getIncomingEdges().size()));
        rightNeighbours.setText(String.valueOf(node.getOutgoingEdges().size()));

        position.setText(node.getSegmentIds().toString());

        annotationTable.setItems(FXCollections.observableArrayList(
                node instanceof Segment
                        ? graphAnnotation.getAnnotationsOfNode(((Segment) node).getId())
                        : FXCollections.emptyObservableList()));
    }


    /**
     * Clear all text fields used to describe node properties.
     */
    private void clearNodeFields() {
        nodeId.setText("");
        sequencePreview.setText("");
        leftNeighbours.setText("");
        rightNeighbours.setText("");
        position.setText("");
    }

    /**
     * When the user clicks on the view sequence {@link javafx.scene.control.Button}.
     *
     * @param event the {@link MouseEvent}
     */
    @FXML
    void onFocusAction(final ActionEvent actionEvent) {
        final GfaNode selectedNode = graphVisualizer.getSelectedSegmentProperty().get();
        if (selectedNode != null) {
            final long selectedNodePosition = FafospLayerer.LAYER_WIDTH * graphVisualizer.getGraph()
                    .getUnscaledXPosition(selectedNode.getSegmentIds().get(0));
            graphDimensionsCalculator.getViewPointProperty().set(selectedNodePosition);
        }

        actionEvent.consume();
    }

    /**
     * When the user clicks on the view sequence {@link javafx.scene.control.Button}.
     *
     * @param event the {@link MouseEvent}
     */
    @FXML
    void onViewSequence(final MouseEvent event) {
        sequenceVisualizer.getVisibleProperty().set(!sequenceVisualizer.getVisibleProperty().get());
        event.consume();
    }
}
