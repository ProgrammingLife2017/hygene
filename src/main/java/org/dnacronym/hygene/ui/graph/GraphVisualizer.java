package org.dnacronym.hygene.ui.graph;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.core.HygeneEventBus;
import org.dnacronym.hygene.events.NodeMetadataCacheUpdateEvent;
import org.dnacronym.hygene.graph.NewNode;
import org.dnacronym.hygene.graph.Segment;
import org.dnacronym.hygene.models.Edge;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.Node;
import org.dnacronym.hygene.models.NodeMetadataCache;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.bookmark.SimpleBookmark;
import org.dnacronym.hygene.ui.node.NodeDrawingToolkit;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple canvas that allows drawing of primitive shapes.
 * <p>
 * It observes the {@link Graph} in {@link GraphDimensionsCalculator}. When the list of nodes qeuried nodes changes
 * in {@link GraphDimensionsCalculator}, then it will clear the {@link Canvas} and draw the new {@link Node}s on the
 * {@link Canvas} using a {@link GraphicsContext}.
 *
 * @see Canvas
 * @see GraphicsContext
 * @see GraphDimensionsCalculator
 */
public final class GraphVisualizer {
    private static final Logger LOGGER = LogManager.getLogger(GraphVisualizer.class);

    private static final double DEFAULT_NODE_HEIGHT = 20;
    private static final double DEFAULT_DASH_LENGTH = 10;
    /**
     * Range used when new graph is set, unless graph contains too few nodes.
     */
    private static final int MAX_GRAPH_RADIUS_NODE_TEXT = 100;

    private static final double DEFAULT_EDGE_WIDTH = 1;
    private static final Color DEFAULT_EDGE_COLOR = Color.GREY;
    private static final int EDGE_OPACITY_OFFSET = 80;
    private static final double EDGE_OPACITY_ALPHA = 1.08;
    private static final double EDGE_OPACITY_BETA = 4.25;

    private final GraphDimensionsCalculator graphDimensionsCalculator;

    private final ObjectProperty<Node> selectedNodeProperty;
    private final ObjectProperty<Edge> selectedEdgeProperty;
    private final List<Integer> bookmarkedNodes;

    private final ObjectProperty<Color> edgeColorProperty;
    private final DoubleProperty nodeHeightProperty;

    private final BooleanProperty displayLaneBordersProperty;

    private Graph graph;
    private NodeMetadataCache nodeMetadataCache;

    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private final NodeDrawingToolkit nodeDrawingToolkit;

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
     */
    public GraphVisualizer(final GraphDimensionsCalculator graphDimensionsCalculator) {
        HygeneEventBus.getInstance().register(this);

        this.graphDimensionsCalculator = graphDimensionsCalculator;

        selectedNodeProperty = new SimpleObjectProperty<>();
        selectedEdgeProperty = new SimpleObjectProperty<>();

        getSelectedNodeProperty().addListener((observable, oldValue, newValue) -> draw());

        edgeColorProperty = new SimpleObjectProperty<>(DEFAULT_EDGE_COLOR);
        nodeHeightProperty = new SimpleDoubleProperty(DEFAULT_NODE_HEIGHT);
        graphDimensionsCalculator.getNodeHeightProperty().bind(nodeHeightProperty);

        edgeColorProperty.addListener((observable, oldValue, newValue) -> draw());
        nodeHeightProperty.addListener((observable, oldValue, newValue) -> draw());

        displayLaneBordersProperty = new SimpleBooleanProperty();
        displayLaneBordersProperty.addListener((observable, oldValue, newValue) -> draw());

        graphDimensionsCalculator.getGraphProperty()
                .addListener((observable, oldValue, newValue) -> setGraph(newValue));
        graphDimensionsCalculator.getObservableQueryNodes()
                .addListener((ListChangeListener<NewNode>) change -> draw());

        nodeDrawingToolkit = new NodeDrawingToolkit();
    }


    /**
     * Draw a node on the canvas.
     * <p>
     * The node is afterwards added to the {@link RTree}.
     *
     * @param node       id of node to draw
     * @param charWidth  the width of character
     * @param charHeight the height of a character
     * @param highlight  the boolean indicating whether to highlight this node
     * @param bookmarked the boolean indicating whether this node is bookmarked
     * @param drawText   the boolean indicating whether this node should have text
     */
    private void drawNode(final NewNode node, final double charWidth, final double charHeight, final boolean highlight,
                          final boolean bookmarked, final boolean drawText) {
        final double rectX = graphDimensionsCalculator.computeXPosition(node);
        final double rectY = graphDimensionsCalculator.computeYPosition(node);
        final double rectWidth = graphDimensionsCalculator.computeWidth(node);
        final double rectHeight = nodeHeightProperty.get();

        graphicsContext.setFill(node.getColor().getFXColor());
        graphicsContext.fillRoundRect(rectX, rectY, rectWidth, rectHeight, ARC_SIZE, ARC_SIZE);

        if (!(node instanceof Segment)) {
            return;
        }

        if (highlight) {
            nodeDrawingToolkit.highlightNode(rectX, rectY, rectWidth, rectHeight, NodeColor.BRIGHT_GREEN.getFXColor());
        }
        final int nodeId = ((Segment) node).getId();
        if (nodeMetadataCache.has(nodeId)
                && graphDimensionsCalculator.getRadiusProperty().get() < MAX_GRAPH_RADIUS_NODE_TEXT) {
            graphicsContext.setFill(Color.BLACK);

            final int charCount = (int) Math.max((rectWidth - ARC_SIZE) / charWidth, 0);

            final double fontX = rectX + 0.5 * (rectWidth + (ARC_SIZE / 4.0) - charCount * charWidth);
            final double fontY = rectY + 0.5 * graphDimensionsCalculator.getNodeHeightProperty().getValue()
                    + 0.25 * charHeight;

        if (drawText) {
            try {
                final String sequence = nodeMetadataCache.getOrRetrieve(nodeId).retrieveMetadata().getSequence();
                nodeDrawingToolkit.drawText(rectX, rectY, rectWidth, rectHeight, charWidth, charHeight, sequence);
            } catch (final ParseException e) {
                LOGGER.error("An parse exception occurred while attempting"
                        + " to retrieve node's " + nodeId + " metadata from drawing", e);
            }
        }
        if (bookmarked) {
            nodeDrawingToolkit.drawBookmarkFlag(rectX, rectY, rectWidth,
                    graphDimensionsCalculator.getLaneHeightProperty().get() - nodeHeightProperty.get(),
                    canvas.getHeight());
        }

        rTree.addNode(nodeId, rectX, rectY, rectWidth, rectHeight);
    }

        rTree.addNode(nodeId, nodeX, nodeY, nodeWidth, nodeHeightProperty.get());
    }

    /**
     * Draws an edge on the canvas.
     * <p>
     * The edge is afterwards added to the {@link RTree}.
     *
     * @param fromNode edge origin node ID
     * @param toNode   edge destination node ID
     */
    private void drawEdge(final NewNode fromNode,
                          final NewNode toNode) {
        final double fromX = graphDimensionsCalculator.computeRightXPosition(fromNode);
        final double fromY = graphDimensionsCalculator.computeMiddleYPosition(fromNode);
        final double toX = graphDimensionsCalculator.computeXPosition(toNode);
        final double toY = graphDimensionsCalculator.computeMiddleYPosition(toNode);

        graphicsContext.setStroke(getEdgeColor());
        graphicsContext.setLineWidth(DEFAULT_EDGE_WIDTH);
        graphicsContext.strokeLine(fromX, fromY, toX, toY);

        if (fromNode instanceof Segment && toNode instanceof Segment) {
            rTree.addEdge(((Segment) fromNode).getId(), ((Segment) toNode).getId(), fromX, fromY, toX, toY);
        }
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

        // TODO refactor UI package to decentralize all classes, to avoid strong coupling and getters
        final List<Integer> bookmarkedNodeIds = new ArrayList<>();
        try {
            final List<SimpleBookmark> bookmarks = Hygene.getInstance().getSimpleBookmarkStore().getSimpleBookmarks();
            for (final SimpleBookmark simpleBookmark : bookmarks) {
                bookmarkedNodeIds.add(simpleBookmark.getBookmark().getNodeId());
            }
        } catch (final UIInitialisationException e) {
            LOGGER.error("Unable to get bookmarks.", e);
        }

        clear();nodeDrawingToolkit.setCanvasHeight(canvas.getHeight());
        nodeDrawingToolkit.setNodeHeight(nodeHeightProperty.get());
        nodeDrawingToolkit.setLaneHeight(graphDimensionsCalculator.getLaneHeightProperty().get());

        for (final NewNode node : graphDimensionsCalculator.getObservableQueryNodes()) {
            drawNode(nodeId, charWidth, charHeight,
                    selectedNodeProperty.get() != null && selectedNodeProperty.get().getId() == nodeId,
                    bookmarkedNodeIds.contains(nodeId),
                    nodeMetadataCache.has(nodeId)
                            && graphDimensionsCalculator.getRadiusProperty().get() < MAX_GRAPH_RADIUS_NODE_TEXT);

            node.getOutgoingEdges().forEach(edge -> drawEdge(node, edge.getTo()));
        }

        if (displayLaneBordersProperty.get()) {
            drawLaneBorders(
                    graphDimensionsCalculator.getLaneCountProperty().get(),
                    graphDimensionsCalculator.getLaneHeightProperty().get());
        }
    }

    /**
     * Will listen for {@link NodeMetadataCacheUpdateEvent}, if so we redraw the graph to reflect the changes.
     *
     * @param event the {@link NodeMetadataCacheUpdateEvent}
     */
    @Subscribe
    public void onNodeMetadataCacheUpdate(final NodeMetadataCacheUpdateEvent event) {
        Platform.runLater(this::draw);
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

        if (nodeMetadataCache != null) {
            HygeneEventBus.getInstance().unregister(nodeMetadataCache);
        }
        nodeMetadataCache = new NodeMetadataCache(graph);
        HygeneEventBus.getInstance().register(nodeMetadataCache);

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

        canvas.setOnMouseClicked(event -> {
            if (rTree == null) {
                return;
            }

            selectedNodeProperty.setValue(null);
            selectedEdgeProperty.setValue(null);

            rTree.find(event.getX(), event.getY(),
                    this::setSelectedNode,
                    (fromNodeId, toNodeId) -> graph.getNode(fromNodeId).getOutgoingEdges().stream()
                            .filter(edge -> edge.getTo() == toNodeId)
                            .findFirst()
                            .ifPresent(selectedEdgeProperty::setValue)
            );
        });

        graphDimensionsCalculator.setCanvasSize(canvas.getWidth(), canvas.getHeight());
        canvas.widthProperty().addListener((observable, oldValue, newValue) ->
                graphDimensionsCalculator.setCanvasSize(newValue.doubleValue(), canvas.getHeight()));
        canvas.heightProperty().addListener((observable, oldValue, newValue) ->
                graphDimensionsCalculator.setCanvasSize(canvas.getWidth(), newValue.doubleValue()));
    }

    /**
     * Update the selected {@link Node} to the node with the given id.
     *
     * @param nodeId node id of the new selected {@link Node}
     */
    public void setSelectedNode(final int nodeId) {
        try {
            selectedNodeProperty.set(nodeMetadataCache.getOrRetrieve(nodeId));
        } catch (final ParseException e) {
            LOGGER.info("Metadata of selected node " + nodeId + " could not be parsed.");
        }
    }

    /**
     * The property of the selected node.
     * <p>
     * This node is updated every time the user clicks on a node in the canvas.
     *
     * @return Selected {@link Node} by the user, which can be {@code null}
     */
    public ObjectProperty<Node> getSelectedNodeProperty() {
        return selectedNodeProperty;
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
     * The property of onscreen edge {@link Color}s.
     *
     * @return property which decides the {@link Color} of edges
     */
    public ObjectProperty<Color> getEdgeColorProperty() {
        return edgeColorProperty;
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
}
