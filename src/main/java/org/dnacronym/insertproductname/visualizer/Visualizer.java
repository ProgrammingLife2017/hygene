package org.dnacronym.insertproductname.visualizer;

import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.models.SequenceNode;
import org.dnacronym.insertproductname.parser.AssemblyParser;
import org.dnacronym.insertproductname.parser.GFAParser;
import org.dnacronym.insertproductname.parser.ParseException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class for creating a {@code Viewer} object. The viewer can be embedded in the GUI to display the sequence graph.
 */
public class Visualizer {
    private final static String TITLE = "Sequence Alignment Graph";
    private final static Boolean ENABLE_ANTI_ALIASING = true;
    private final static Boolean ENABLE_PRETTY_RENDERING = false;

    private Graph graph;
    private Viewer viewer;

    /**
     * Construct a new Visualizer from a {@code SequenceGraph}.
     *
     * @param graphData the graph data represented by a @{code SequenceGraph}
     */
    public Visualizer(SequenceGraph graphData) {
        // Init the graph
        initGraph();

        populateGraph(graphData);
    }

    // Basic runner method for debugging without running the GUI, will be removed once the graph will be embedded
    // in the GUI.
    public static void main(String[] args) throws IOException, ParseException {
        String text = new String(Files.readAllBytes(Paths.get("src\\\\main\\\\resources\\\\example-graphs\\\\TB10.gfa")), StandardCharsets.UTF_8);

        GFAParser parser = new GFAParser();
        AssemblyParser ap = new AssemblyParser();
        SequenceGraph graph = ap.parse(parser.parse(text));

        Visualizer v = new Visualizer(graph);

        Viewer view = v.graph.display();
        view.getDefaultView().resizeFrame(1200, 750);
        view.getDefaultView().getCamera().setViewPercent(0.01);
    }

    /**
     * Initialize the graph object.
     */
    private void initGraph() {
        graph = new SingleGraph(TITLE);

        // This will enable or disable the custom advanced styling options
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        // Will enable anti aliasing
        if (ENABLE_ANTI_ALIASING) {
            graph.addAttribute("ui.antialias");
        }

        // This will inform that more fancy rendering algorithms can be used (which are slower)
        if (ENABLE_PRETTY_RENDERING) {
            graph.addAttribute("ui.quality");
        }

        // Add CSS
        graph.addAttribute("ui.stylesheet", "url('src\\main\\resources\\graph-css\\styles.css')");
    }

    /**
     * Populate the graph with the graph-data provided by a {@code SequenceGraph} object.
     *
     * @param graphData the {@code SequenceGraph}
     */
    public void populateGraph(SequenceGraph graphData) {
        // Start node
        SequenceNode currentNode = graphData.getStartNode();
        populateGraph(currentNode);
    }

    /**
     * Method which populates the graph by recursive calling itself on the neighbours of the provided sequence node.
     *
     * @param node the {@code SequenceNode}
     */
    public void populateGraph(SequenceNode node) {
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
    public void addNode(SequenceNode node) {
        String nodeId = node.getId();

        // Test whether the node was already added to the graph
        if (graph.getNode(nodeId) != null) {
            return;
        }

        Node graphNode = graph.addNode(node.getId());
        addNodeClass(graphNode, node);
        graphNode.setAttribute("ui.label", node.getSequence().substring(0, Math.min(20, node.getSequence().length())));
    }

    /**
     * Given the node a class which can be used for styling.
     *
     * @param graphNode node within the (GUI) graph
     * @param sequenceNode node within the internal graph data structure
     */
    public void addNodeClass(Node graphNode, SequenceNode sequenceNode) {
        String classLabel = "";
        switch (sequenceNode.getSequence().substring(0, 1)) {
            case "A":
                classLabel = "A";
                break;
            case "C":
                classLabel = "C";
                break;
            case "T":
                classLabel = "T";
                break;
            case "G":
                classLabel = "G";
                break;
            default:
                break;
        }
        graphNode.setAttribute("ui.class", classLabel);
    }

    /**
     * Add a edge between two sequence nodes to the graph.
     *
     * @param a sequence node a
     * @param b sequence node b
     */
    public void addEdge(SequenceNode a, SequenceNode b) {
        String edgeIdentifier = a.getId() + ":" + b.getId();
        if (graph.getEdge(edgeIdentifier) == null) {
            graph.addEdge(edgeIdentifier, a.getId(), b.getId());
        }
    }
}
