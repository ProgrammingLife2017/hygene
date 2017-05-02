package org.dnacronym.insertproductname.ui.visualizer;

import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.models.SequenceNode;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;


/**
 * Used to render {@link SequenceGraph} using the {@link org.graphstream} library.
 * <p>
 * Note that this is a temporary class.
 *
 * @see org.graphstream.graph.Graph
 * @see org.graphstream.graph.Node
 * @see org.graphstream.graph.implementations.SingleGraph
 */
public class GraphStreamVisualiser {
    private static final String TITLE = "Sequence Alignment Graph";
    private static final String STYLESHEET = "/ui/css/graph_style.css";

    private static final int MAX_SEQUENCE_LENGH = 20;

    private boolean antiAliasing = true;
    private boolean prettyRendering = false;

    private Graph graph;


    /**
     * Create a new {@link GraphStreamVisualiser} object.
     *
     * @param antiAliasing    Use anti aliasing
     * @param prettyRendering Use pretty rendering.
     */
    public GraphStreamVisualiser(final boolean antiAliasing, final boolean prettyRendering) {
        this.antiAliasing = antiAliasing;
        this.prettyRendering = prettyRendering;

        initGraph();
    }

    /**
     * Get the interally stored {@link Graph}.
     *
     * @return Graph stored in visualiser.
     */
    public final Graph getGraph() {
        return graph;
    }

    /**
     * Initialize the graph object.
     */
    private void initGraph() {
        graph = new SingleGraph(TITLE);

        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        if (antiAliasing) {
            graph.addAttribute("ui.antialias");
        }
        if (prettyRendering) {
            graph.addAttribute("ui.quality");
        }

        graph.addAttribute("ui.stylesheet", getClass().getResource(STYLESHEET));
    }

    /**
     * Populate the graph with the graph-data provided by a {@code SequenceGraph}.
     *
     * @param graphData a {@code SequenceGraph}.
     */
    public final void populateGraph(final SequenceGraph graphData) {
        graph.clear();

        final SequenceNode currentNode = graphData.getStartNode();
        populateGraph(currentNode);
    }

    /**
     * Method which populates the graph by recursive calling itself on the neighbours of the provided sequence node.
     *
     * @param node the {@code SequenceNode}
     */
    private void populateGraph(final SequenceNode node) {
        addNode(node);

        for (SequenceNode neighbour : node.getRightNeighbours()) {
            if (graph.getNode(neighbour.getId()) == null) {
                populateGraph(neighbour);
            }

            addEdge(node, neighbour);
        }
    }

    /**
     * Add a single {@code SequenceNode} to the graph.
     *
     * @param node the sequence node to add
     */
    private void addNode(final SequenceNode node) {
        final String nodeId = node.getId();

        if (graph.getNode(nodeId) == null) {
            final Node graphNode = graph.addNode(node.getId());
            addNodeClass(graphNode, node);
            graphNode.setAttribute("ui.label",
                    node.getSequence().substring(0, Math.min(MAX_SEQUENCE_LENGH, node.getSequence().length())));
        }
    }

    /**
     * Given the node a class which can be used for styling.
     *
     * @param graphNode    node within the (GUI) graph
     * @param sequenceNode node within the internal graph data structure
     */
    private void addNodeClass(final Node graphNode, final SequenceNode sequenceNode) {
        switch (sequenceNode.getSequence().substring(0, 1)) {
            case "A":
                graphNode.setAttribute("ui.class", "A");
                break;
            case "C":
                graphNode.setAttribute("ui.class", "C");
                break;
            case "T":
                graphNode.setAttribute("ui.class", "T");
                break;
            case "G":
                graphNode.setAttribute("ui.class", "G");
                break;
            default:
                graphNode.setAttribute("ui.class", "");
        }
    }

    /**
     * Add an edge between two nodes in the graph.
     *
     * @param first  First {@link SequenceNode} in graph.
     * @param second Second {@link SequenceNode} in graph.
     */
    private void addEdge(final SequenceNode first, final SequenceNode second) {
        String edgeIdentifier = first.getId() + ":" + second.getId();

        if (graph.getEdge(edgeIdentifier) == null) {
            graph.addEdge(edgeIdentifier, first.getId(), second.getId());
        }
    }
}
