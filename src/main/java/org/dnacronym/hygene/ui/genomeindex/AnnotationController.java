package org.dnacronym.hygene.ui.genomeindex;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.dnacronym.hygene.graph.Graph;
import org.dnacronym.hygene.graph.annotation.Annotation;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphStore;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Controller for the annotation search view.
 */
public final class AnnotationController implements Initializable {
    private static final int TEXT_PADDING = 10;

    @Inject
    private AnnotationSearch annotationSearch;
    @Inject
    private GraphDimensionsCalculator graphDimensionsCalculator;
    @Inject
    private GraphStore graphStore;

    @FXML
    private TextField queryField;
    @FXML
    private TableView<Annotation> resultsTable;
    @FXML
    private TableColumn<Annotation, Color> colorColumn;
    @FXML
    private TableColumn<Annotation, String> nameColumn;
    @FXML
    private TableColumn<Annotation, String> typeColumn;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        resultsTable.setRowFactory(tableView -> {
            final TableRow<Annotation> annotationTableRow = new TableRow<>();
            annotationTableRow.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    final int startNodeId = annotationTableRow.getItem().getStartNodeId();
                    final int endNodeId = annotationTableRow.getItem().getEndNodeId();

                    final int radius = (int) Math.round(((double) endNodeId - startNodeId) / 2);
                    final int middleNodeId = (int) Math.round(startNodeId + (double) radius / 2);

                    final Graph graph = graphStore.getGfaFileProperty().get().getGraph();
                    final int viewPointX = (int) Math.round(
                            ((double) graph.getRealStartXPosition(middleNodeId)
                                    + graph.getRealEndXPosition(middleNodeId)) / 2
                    );

                    graphDimensionsCalculator.getViewPointProperty().set(viewPointX);
                    graphDimensionsCalculator.getViewRadiusProperty().set(radius * 2000);
                }
            });
            return annotationTableRow;
        });

        nameColumn.setCellFactory(this::createWrappableTableCell);
        typeColumn.setCellFactory(this::createWrappableTableCell);

        nameColumn.setCellValueFactory(cell -> {
            if (cell.getValue().getAttributes().get("Name") == null) {
                return new SimpleStringProperty("[unknown]");
            } else {
                return new SimpleStringProperty(cell.getValue().getAttributes().get("Name")[0]);
            }
        });
        typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));

        resultsTable.itemsProperty().bind(annotationSearch.getSearchResults());

        colorColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getColor()));
        colorColumn.setCellFactory(cell -> new TableCell<Annotation, Color>() {
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

        graphStore.getGffFileProperty().addListener((observable, oldValue, newValue) -> annotationSearch.search(""));
    }

    /**
     * When the user wants to search {@link Annotation}s.
     *
     * @param actionEvent the {@link ActionEvent}
     */
    @FXML
    void searchAction(final ActionEvent actionEvent) {
        annotationSearch.search(queryField.getText());

        actionEvent.consume();
    }

    /**
     * Creates a wrapped table cell.
     *
     * @param column the table column in which the cell resides
     * @return a TableCell with the text wrapped inside
     */
    private TableCell<Annotation, String> createWrappableTableCell(final TableColumn<Annotation, String> column) {
        return new TableCell<Annotation, String>() {
            @Override
            protected void updateItem(final String item, final boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    return;
                }

                final Text text = new Text(item);
                text.setWrappingWidth(column.getWidth() - TEXT_PADDING);
                setPrefHeight(text.getLayoutBounds().getHeight());

                setGraphic(text);
            }
        };
    }
}
