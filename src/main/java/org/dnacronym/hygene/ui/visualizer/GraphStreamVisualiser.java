package org.dnacronym.hygene.ui.visualizer;

import org.dnacronym.hygene.core.Files;
import org.dnacronym.hygene.models.SequenceGraph;
import org.dnacronym.hygene.models.SequenceNode;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.FileNotFoundException;


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

    private static final String UI_CLASS = "ui.class";
    private static final String UI_ANTI_ANTIALIAS = "ui.antialias";
    private static final String UI_QUALITY = "ui.quality";
    private static final String UI_LABEL = "ui.label";
    private static final String UI_STYLESHEET = "ui.stylesheet";

    private static final int MAX_SEQUENCE_LENGTH = 20;

    private Graph graph;


    /**
     * Create a new {@link GraphStreamVisualiser} object.
     */
    public GraphStreamVisualiser() {
        graph = new SingleGraph(TITLE);

        graph.addAttribute(UI_ANTI_ANTIALIAS);
        graph.addAttribute(UI_QUALITY);

        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        try {
            graph.addAttribute(UI_STYLESHEET, Files.getInstance().getResourceUrl(STYLESHEET));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
     * Populate the graph with the graph-data provided by a {@code SequenceGraph}.
     *
     * @param graphData a {@code SequenceGraph}.
     */
    public final void populateGraph(final SequenceGraph graphData) {
        graph.clear();

        populateGraph(graphData.getSourceNode());
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
            graphNode.setAttribute(UI_LABEL,
                    node.getSequence().substring(0, Math.min(MAX_SEQUENCE_LENGTH, node.getSequence().length())));
        }
    }

    /**
     * Assign a (css) class to a node which can be used for styling.
     *
     * @param graphNode    node within the (GUI) graph
     * @param sequenceNode node within the internal graph data structure
     */
    private void addNodeClass(final Node graphNode, final SequenceNode sequenceNode) {
        if (sequenceNode.getSequence().length() > 0) {
            graphNode.setAttribute(UI_CLASS, String.valueOf(sequenceNode.getSequence().charAt(0)));
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
