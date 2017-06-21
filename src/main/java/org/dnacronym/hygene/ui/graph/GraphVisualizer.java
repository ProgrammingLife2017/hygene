package org.dnacronym.hygene.ui.graph;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.core.HygeneEventBus;
import org.dnacronym.hygene.event.SnapshotButtonWasPressed;
import org.dnacronym.hygene.graph.Graph;
import org.dnacronym.hygene.graph.edge.DummyEdge;
import org.dnacronym.hygene.graph.edge.Edge;
import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.node.Segment;
import org.dnacronym.hygene.ui.bookmark.BookmarkStore;
import org.dnacronym.hygene.ui.drawing.EdgeDrawingToolkit;
import org.dnacronym.hygene.ui.drawing.NodeDrawingToolkit;
import org.dnacronym.hygene.ui.path.GenomePath;
import org.dnacronym.hygene.ui.query.Query;
import org.dnacronym.hygene.ui.settings.BasicSettingsViewController;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * A simple canvas that allows drawing of primitive shapes.
 * <p>
 * It observes the {@link Graph} in {@link GraphDimensionsCalculator}. When the list of nodes queried nodes changes
 * in {@link GraphDimensionsCalculator}, then it will clear the {@link Canvas} and draw the new {@link Segment}s on the
 * {@link Canvas} using a {@link GraphicsContext}.
 *
 * @see Canvas
 * @see GraphicsContext
 * @see GraphDimensionsCalculator
 */
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.GodClass", "PMD.TooManyFields"}) // This will be fixed at a later date.
public final class GraphVisualizer {
    private static final Logger LOGGER = LogManager.getLogger(GraphVisualizer.class);

    private static final double DEFAULT_NODE_HEIGHT = 20;
    private static final double DEFAULT_DASH_LENGTH = 10;

    private static final double DEFAULT_EDGE_THICKNESS = 3;
    private static final Color DEFAULT_EDGE_COLOR = Color.GREY;
    private static final int EDGE_OPACITY_OFFSET = 20;
    private static final double EDGE_OPACITY_ALPHA = 1.5;
    private static final double EDGE_OPACITY_BETA = 4.25;

    private static final int MAX_PATH_THICKNESS_DRAWING_RADIUS = 150;

    private final GraphDimensionsCalculator graphDimensionsCalculator;
    private final Query query;

    private final ColorRoulette colorRoulette;

    private final ObjectProperty<Segment> selectedSegmentProperty;
    private final ObjectProperty<Edge> selectedEdgeProperty;
    private final ObjectProperty<String> selectedPathProperty;

    private final ObservableList<GenomePath> genomePaths;
    private final ObservableMap<String, Color> selectedGenomePaths;

    private final ObjectProperty<Segment> hoveredSegmentProperty;

    private final ObjectProperty<Color> edgeColorProperty;

    private final DoubleProperty nodeHeightProperty;

    private final BooleanProperty displayLaneBordersProperty;

    private ObjectProperty<Map<String, String>> genomeMapping;

    private Graph graph;

    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private final NodeDrawingToolkit nodeDrawingToolkit;
    private final EdgeDrawingToolkit edgeDrawingToolkit;
    private final GraphAnnotationVisualizer graphAnnotationVisualizer;
    private final BookmarkStore bookmarkStore;

    private RTree rTree;


    /**
     * Create a new {@link GraphVisualizer} instance.
     * <p>
     * The passed {@link GraphStore} is observed by this class. If the {@link GraphStore}
     * {@link org.dnacronym.hygene.parser.GfaFile} is updated, it will prompt a redraw. Changing the properties of this
     * class will also prompt a redraw if the {@link org.dnacronym.hygene.parser.GfaFile} in {@link GraphStore} is not
     * {@code null}.
     *
     * @param graphDimensionsCalculator {@link GraphDimensionsCalculator} used to calculate node positions
     * @param query                     the {@link Query} used to get the currently queried nodes
     * @param bookmarkStore             the {@link BookmarkStore} used to draw bookmark indications
     */
    @Inject
    public GraphVisualizer(final GraphDimensionsCalculator graphDimensionsCalculator, final Query query,
                           final BookmarkStore bookmarkStore) {
        HygeneEventBus.getInstance().register(this);
        this.graphDimensionsCalculator = graphDimensionsCalculator;
        this.query = query;
        this.bookmarkStore = bookmarkStore;
        this.colorRoulette = new ColorRoulette();

        selectedSegmentProperty = new SimpleObjectProperty<>();

        selectedEdgeProperty = new SimpleObjectProperty<>();
        selectedSegmentProperty.addListener((observable, oldValue, newValue) -> draw());
        hoveredSegmentProperty = new SimpleObjectProperty<>();
        hoveredSegmentProperty.addListener((observable, oldValue, newValue) -> draw());

        selectedPathProperty = new SimpleObjectProperty<>();
        selectedPathProperty.addListener(observable -> draw());
        genomePaths = FXCollections.observableArrayList(new HashSet<>());
        selectedGenomePaths = FXCollections.observableHashMap();
        selectedGenomePaths.addListener((MapChangeListener<String, Color>) change -> draw());

        edgeColorProperty = new SimpleObjectProperty<>(DEFAULT_EDGE_COLOR);
        nodeHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);
        graphDimensionsCalculator.getNodeHeightProperty().bind(nodeHeightProperty);

        edgeColorProperty.addListener((observable, oldValue, newValue) -> draw());
        nodeHeightProperty.addListener((observable, oldValue, newValue) -> draw());

        Node.setColorScheme(BasicSettingsViewController.NODE_COLOR_SCHEMES.get(0).getValue());

        displayLaneBordersProperty = new SimpleBooleanProperty();
        displayLaneBordersProperty.addListener((observable, oldValue, newValue) -> draw());

        genomeMapping = new SimpleObjectProperty<>();

        graphDimensionsCalculator.getGraphProperty()
                .addListener((observable, oldValue, newValue) -> {
                    setGraph(newValue);
                    genomeMapping.set(newValue.getGfaFile().getGenomeMapping());
                });
        graphDimensionsCalculator.getObservableQueryNodes()
                .addListener((ListChangeListener<Node>) change -> draw());

        query.getQueriedNodes().addListener((ListChangeListener<Integer>) observable -> draw());

        nodeDrawingToolkit = new NodeDrawingToolkit();
        edgeDrawingToolkit = new EdgeDrawingToolkit();
        graphAnnotationVisualizer = new GraphAnnotationVisualizer(graphDimensionsCalculator);
    }


    /**
     * Draw a node on the canvas.
     * <p>
     * Node outlines are also drawn when one of the following conditions are met:
     * If selected, it is {@link NodeDrawingToolkit.HighlightType#SELECTED}.<br>
     * If it is not selected, and highlighted, it is {@link NodeDrawingToolkit.HighlightType#HIGHLIGHTED}.<br>
     * If it is not highlighted, and queried, it is {@link NodeDrawingToolkit.HighlightType#QUERIED}.<br>
     * If it is not queried, and bookmarked, is it {@link NodeDrawingToolkit.HighlightType#BOOKMARKED}.
     * <p>
     * The node is afterwards added to the {@link RTree}.
     *
     * @param node       the node to draw
     * @param bookmarked the boolean indicating whether this node is bookmarked
     * @param queried    the boolean indicating whether this node has been queried
     */
    private void drawNode(final Node node, final boolean bookmarked, final boolean queried) {
        if (!(node instanceof Segment)) {
            return;
        }

        final Segment segment = (Segment) node;
        final double nodeX = graphDimensionsCalculator.computeXPosition(node);
        final double nodeY = graphDimensionsCalculator.computeYPosition(node);
        final double nodeWidth = graphDimensionsCalculator.computeWidth(node);

        nodeDrawingToolkit.drawNode(nodeX, nodeY, nodeWidth, node.getColor());

        if (segment.equals(selectedSegmentProperty.get())) {
            nodeDrawingToolkit.drawNodeHighlight(nodeX, nodeY, nodeWidth, NodeDrawingToolkit.HighlightType.SELECTED);
        } else if (segment.equals(hoveredSegmentProperty.get())) {
            nodeDrawingToolkit.drawNodeHighlight(nodeX, nodeY, nodeWidth, NodeDrawingToolkit.HighlightType.HIGHLIGHTED);
        } else if (queried) {
            nodeDrawingToolkit.drawNodeHighlight(nodeX, nodeY, nodeWidth, NodeDrawingToolkit.HighlightType.QUERIED);
        } else if (bookmarked) {
            nodeDrawingToolkit.drawNodeHighlight(nodeX, nodeY, nodeWidth, NodeDrawingToolkit.HighlightType.BOOKMARKED);
        }

        if (segment.hasMetadata()) {
            final String sequence = segment.getMetadata().getSequence();
            nodeDrawingToolkit.drawNodeSequence(nodeX, nodeY, nodeWidth, sequence);
        }

        rTree.addNode(segment.getId(), nodeX, nodeY, nodeWidth, nodeHeightProperty.get());
    }

    /**
     * Draws an edge on the canvas.
     * <p>
     * The edge is afterwards added to the {@link RTree}.
     *
     * @param edge the edge to be drawn
     */
    private void drawEdge(final Edge edge) {
        final Node fromNode = edge.getFrom();
        final Node toNode = edge.getTo();

        final double fromX = graphDimensionsCalculator.computeRightXPosition(fromNode);
        final double fromY = graphDimensionsCalculator.computeMiddleYPosition(fromNode);
        final double toX = graphDimensionsCalculator.computeXPosition(toNode);
        final double toY = graphDimensionsCalculator.computeMiddleYPosition(toNode);

        if (fromNode instanceof Segment && toNode instanceof Segment) {
            final int fromSegmentId = ((Segment) fromNode).getId();
            final int toSegmentId = ((Segment) toNode).getId();

            rTree.addEdge(fromSegmentId, toSegmentId, fromX, fromY, toX, toY);
        }

        edgeDrawingToolkit.drawEdge(fromX, fromY, toX, toY, computeEdgeThickness(edge), computeEdgeColors(edge));
    }

    /**
     * Computes the {@link Color} of the {@link Edge}.
     *
     * @param edge the {@link Edge}
     * @return list of {@link Edge} colors
     */
    private List<Color> computeEdgeColors(final Edge edge) {
        final List<Color> edgeColors;

        if (hovered(edge)) {
            edgeColors = Collections.singletonList(NodeDrawingToolkit.HighlightType.HIGHLIGHTED.getColor());
        } else if (edge.inGenome(selectedPathProperty.get())
                && graphDimensionsCalculator.getRadiusProperty().get() < MAX_PATH_THICKNESS_DRAWING_RADIUS) {
            edgeColors = Collections.singletonList(correctColorForEdgeOpacity(Color.BLUE));
        } else if (edge.getGenomes() != null) {
            Set<String> selectedGenomesInEdge = Sets.intersection(edge.getGenomes(), selectedGenomePaths.keySet());

            if (selectedGenomesInEdge.isEmpty()) {
                edgeColors = Collections.singletonList(getEdgeColor());
            } else {
                edgeColors = selectedGenomesInEdge.stream()
                        .map(path -> correctColorForEdgeOpacity(selectedGenomePaths.get(path)))
                        .collect(Collectors.toList());
            }
        } else {
            edgeColors = Collections.singletonList(getEdgeColor());
        }

        return edgeColors;
    }

    /**
     * Checks whether the {@link Edge} if part of the {@link Segment} being hovered.
     *
     * @param edge the {@link Edge}
     * @return if the edge is part of the {@link Segment} being hovered
     */
    boolean hovered(final Edge edge) {
        if (edge instanceof DummyEdge) {
            return (((DummyEdge) edge).getOriginalEdge().getFrom().equals(hoveredSegmentProperty.get())
                    || ((DummyEdge) edge).getOriginalEdge().getTo().equals(hoveredSegmentProperty.get()));
        }
        return edge.getFrom().equals(hoveredSegmentProperty.get()) || edge.getTo().equals(hoveredSegmentProperty.get());
    }

    /**
     * Computes the thickness of an edge based on the {@link Edge} importance.
     * <p>
     * The thickness is computed as the number unique genomes that run through this path divided by the total
     * number of unique paths.
     *
     * @param edge the edge
     * @return the edge thickness
     */
    @SuppressWarnings("MagicNumber")
    public double computeEdgeThickness(final Edge edge) {
        final int numberOfGenomes = graph.getGfaFile().getGenomeMapping().size();

        return Math.max(DEFAULT_EDGE_THICKNESS,
                0.5 * ((double) edge.getImportance()) / numberOfGenomes * nodeHeightProperty.get());
    }

    /**
     * Computes the opacity based on the graph radius, which is an approximation of the current zoom level.
     * <p>
     * The formula used to compute the edge opacity is as follows:
     * <p>
     * opacity(radius) = 1 - 1 / ( 1 + e^( -(alpha * ln( max(1, hops - offset) - beta) ) )
     * <p><ul>
     * <li>{@code offset} is roughly the amount of hops after which the opacity scaling will start.
     * <li>{@code alpha} affects the slope of the curve.
     * <li>{@code beta} increasing beta will essentially cause the curve to lift up a bit smoothing it out.
     * </ul>
     *
     * @return the edge opacity
     */
    private double computeEdgeOpacity() {
        return 1.0 - 1.0 / (1.0 + Math.exp(-(EDGE_OPACITY_ALPHA
                * Math.log(Math.max(1, graphDimensionsCalculator.getRadiusProperty().get() - EDGE_OPACITY_OFFSET))
                - EDGE_OPACITY_BETA)));
    }

    /**
     * Correct a {@link Color} for the current edge opacity.
     *
     * @param color the {@link Color}
     * @return the {@link Color} corrected for edge opacity
     */
    private Color correctColorForEdgeOpacity(final Color color) {
        return color.deriveColor(1, 1, 1, computeEdgeOpacity());
    }

    /**
     * Retrieves the {@link Color} of an edge.
     *
     * @return the {@link Color}
     */
    private Color getEdgeColor() {
        return edgeColorProperty.getValue().deriveColor(1, 1, 1, computeEdgeOpacity());
    }

    /**
     * Clear the canvas, and resets the {@link RTree}.
     */
    private void clear() {
        rTree = new RTree();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Populate the graphs primitives with the given graph.
     * <p>
     * First clears the graph before drawing. If {@link Graph} is null, only clears the canvas.
     *
     * @throws IllegalStateException if the {@link Canvas} has not been set
     */
    public void draw() {
        if (canvas == null || graphicsContext == null) {
            throw new IllegalStateException("Attempting to draw whilst canvas not set.");
        }

        clear();
        nodeDrawingToolkit.setNodeHeight(nodeHeightProperty.get());
        nodeDrawingToolkit.setCanvasHeight(canvas.getHeight());
        graphAnnotationVisualizer.setCanvasWidth(canvas.getWidth());


        // Edges should be drawn before nodes, don't combine this with node drawing loop
        for (final Node node : graphDimensionsCalculator.getObservableQueryNodes()) {
            node.getOutgoingEdges().forEach(this::drawEdge);
        }

        for (final Node node : graphDimensionsCalculator.getObservableQueryNodes()) {
            drawNode(node,
                    bookmarkStore != null && bookmarkStore.containsBookmark(node),
                    node instanceof Segment && query.getQueriedNodes().contains(((Segment) node).getId()));
        }

        if (displayLaneBordersProperty.get()) {
            drawLaneBorders(
                    graphDimensionsCalculator.getLaneCountProperty().get(),
                    graphDimensionsCalculator.getLaneHeightProperty().get());
        }
    }

    /**
     * Listens for {@link SnapshotButtonWasPressed} events.
     *
     * @param event the {@link SnapshotButtonWasPressed} event
     */
    @Subscribe
    public void onScreenshotButtonWasPressed(final SnapshotButtonWasPressed event) {
        Snapshot.forGfaFile(graph.getGfaFile()).take(canvas);
    }

    /**
     * Draw the border between bands as {@link Color#BLACK}.
     *
     * @param laneCount  amount of bands onscreen
     * @param laneHeight height of each lane
     */
    private void drawLaneBorders(final int laneCount, final double laneHeight) {
        final Paint originalStroke = graphicsContext.getStroke();
        final double originalLineWidth = graphicsContext.getLineWidth();

        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.setLineWidth(1);
        graphicsContext.setLineDashes(DEFAULT_DASH_LENGTH);

        for (int band = 1; band < laneCount; band++) {
            graphicsContext.strokeLine(
                    0,
                    band * laneHeight,
                    canvas.getWidth(),
                    band * laneHeight
            );
        }

        graphicsContext.setStroke(originalStroke);
        graphicsContext.setLineWidth(originalLineWidth);
        graphicsContext.setLineDashes(1);
    }

    /**
     * Set the {@link Graph} for use in the {@link GraphVisualizer}.
     * <p>
     * The {@link Graph} is used to get node colors.
     *
     * @param graph the {@link Graph} for use in the {@link GraphVisualizer}
     */
    void setGraph(final Graph graph) {
        this.graph = graph;

        genomePaths.clear();
        List<GenomePath> genomePathList = graph.getGfaFile().getGenomeMapping().entrySet().stream()
                .map(entry -> new GenomePath(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        genomePathList.forEach(path -> path.isSelectedProperty().addListener((o, oldIsSelected, newIsSelected) -> {
            if (newIsSelected) {
                final Color genomeColor = colorRoulette.getNext();
                selectedGenomePaths.put(path.getIndex(), genomeColor);
                selectedGenomePaths.put(path.getName(), genomeColor);
            } else {
                selectedGenomePaths.remove(path.getIndex());
                selectedGenomePaths.remove(path.getName());
            }
            LOGGER.info(selectedGenomePaths);
        }));

        genomePaths.addAll(genomePathList);

        draw();
    }

    /**
     * Set {@link Canvas} which the {@link GraphVisualizer} use to draw.
     *
     * @param canvas canvas to be used to {@link GraphVisualizer}
     */
    public void setCanvas(final Canvas canvas) {
        this.canvas = canvas;
        this.graphicsContext = canvas.getGraphicsContext2D();
        this.nodeDrawingToolkit.setGraphicsContext(graphicsContext);
        this.edgeDrawingToolkit.setGraphicsContext(graphicsContext);
        this.graphAnnotationVisualizer.setGraphicsContext(graphicsContext);

        canvas.setOnMouseClicked(event -> {
            if (rTree == null) {
                return;
            }

            selectedSegmentProperty.setValue(null);
            selectedEdgeProperty.setValue(null);

            rTree.find(event.getX(), event.getY(),
                    this::setSelectedSegment,
                    (fromNodeId, toNodeId) -> graphDimensionsCalculator.getObservableQueryNodes().stream()
                            .filter(node -> node instanceof Segment)
                            .filter(node -> ((Segment) node).getId() == fromNodeId)
                            .findFirst()
                            .ifPresent(node -> node.getOutgoingEdges().stream()
                                    .filter(edge -> edge.getTo() instanceof Segment
                                            && ((Segment) edge.getTo()).getId() == toNodeId)
                                    .findFirst()
                                    .ifPresent(selectedEdgeProperty::setValue)
                            )
            );
        });
        canvas.setOnMouseMoved(event -> {
            if (rTree == null) {
                return;
            }
            hoveredSegmentProperty.set(null);
            rTree.find(event.getX(), event.getY(), this::setHoveredSegmentProperty);
        });
        canvas.setOnMouseExited(event -> hoveredSegmentProperty.set(null));

        graphDimensionsCalculator.setCanvasSize(canvas.getWidth(), canvas.getHeight());
        canvas.widthProperty().addListener((observable, oldValue, newValue) ->
                graphDimensionsCalculator.setCanvasSize(newValue.doubleValue(), canvas.getHeight()));
        canvas.heightProperty().addListener((observable, oldValue, newValue) ->
                graphDimensionsCalculator.setCanvasSize(canvas.getWidth(), newValue.doubleValue()));
    }

    /**
     * Updates the selected {@link Segment} to the node with the given id.
     *
     * @param nodeId node the id of the newly selected {@link Segment}
     */
    public void setSelectedSegment(final int nodeId) {
        final FilteredList<Node> segment = graphDimensionsCalculator.getObservableQueryNodes()
                .filtered(node -> node instanceof Segment && ((Segment) node).getId() == nodeId);

        if (segment.isEmpty()) {
            LOGGER.error("Cannot select node that is not in subgraph.");
            return;
        }

        selectedSegmentProperty.set((Segment) segment.get(0));
    }

    /**
     * Updates the hovered {@link Segment} to the node with the given id.
     *
     * @param nodeId node the id of the newly hovered {@link Segment}
     */
    public void setHoveredSegmentProperty(final int nodeId) {
        final FilteredList<Node> segment = graphDimensionsCalculator.getObservableQueryNodes()
                .filtered(node -> node instanceof Segment && ((Segment) node).getId() == nodeId);

        if (segment.isEmpty()) {
            LOGGER.error("Cannot select node that is not in subgraph.");
            return;
        }

        hoveredSegmentProperty.set((Segment) segment.get(0));
    }

    /**
     * The property of the selected node.
     * <p>
     * This node is updated every time the user clicks on a node in the canvas.
     *
     * @return the selected {@link Segment} by the user, which can be {@code null}
     */
    public ObjectProperty<Segment> getSelectedSegmentProperty() {
        return selectedSegmentProperty;
    }

    /**
     * The property of the selected edge.
     * <p>
     * This edge is updated every time the user clicks on an edge in the canvas.
     *
     * @return Selected {@link Edge} by the user, which can be {@code null}
     */
    public ObjectProperty<Edge> getSelectedEdgeProperty() {
        return selectedEdgeProperty;
    }

    /**
     * The property of the selected path.
     * <p>
     * This path is updated every time the user selects a new path for the path menu.
     *
     * @return the path
     */
    public ObjectProperty<String> getSelectedPathProperty() {
        return selectedPathProperty;
    }

    /**
     * The property of representing the selected paths.
     *
     * @return property of representing the selected paths.
     */
    public ObservableList<GenomePath> getSelectedPathsProperty() {
        return genomePaths;
    }

    /**
     * The property of onscreen edge {@link Color}s.
     *
     * @return property which decides the {@link Color} of edges
     */
    public ObjectProperty<Color> getEdgeColorProperty() {
        return edgeColorProperty;
    }

    /**
     * Gets the {@link Graph}.
     *
     * @return the graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * The property of onscreen node heights.
     *
     * @return property which decides the height of nodes
     */
    public DoubleProperty getNodeHeightProperty() {
        return nodeHeightProperty;
    }

    /**
     * The property which determines whether to display the border between lanes as black lines.
     *
     * @return property which decides whether to display the border between lanes
     */
    public BooleanProperty getDisplayBordersProperty() {
        return displayLaneBordersProperty;
    }

    /**
     * The property which contains a map of genome mappings.
     *
     * @return property which contain a map genome mappings.
     */
    public ObjectProperty<Map<String, String>> getGenomeMappingProperty() {
        return genomeMapping;
    }
}
