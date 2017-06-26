package org.dnacronym.hygene.coordinatesystem;

import org.dnacronym.hygene.graph.Graph;
import org.dnacronym.hygene.graph.GraphIterator;
import org.dnacronym.hygene.graph.SequenceDirection;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.ProgressUpdater;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;


/**
 * Represents a dynamic genome index for single genomes.
 */
public final class GenomeIndex {
    public static final String GENOME_LIST_HEADER_PREFIX = "ORI:Z:";

    private final GfaFile gfaFile;
    private final Graph graph;
    private final String name;
    private final String index;
    private final Set<Integer> visited;
    private final Set<Integer> nodesInGenome;
    /**
     * Maps from base counts to node IDs.
     */
    @SuppressWarnings("PMD.LooseCoupling") // We explicitly want to use the TreeMap as a type here
    private final TreeMap<Integer, Integer> baseCounts;
    private int currentNode;


    /**
     * Constructs a {@link GenomeIndex} instance.
     *
     * @param gfaFile    the GFA file
     * @param name the name of the genome to be indexed
     */
    public GenomeIndex(final GfaFile gfaFile, final String name) {
        this.gfaFile = gfaFile;
        this.graph = gfaFile.getGraph();
        this.name = name;
        this.index = gfaFile.getGenomeMapping().entrySet().stream()
                .filter(entry -> entry.getValue().equals(name))
                .map(Map.Entry::getKey).findFirst().orElse(name);

        visited = new HashSet<>();
        baseCounts = new TreeMap<>();
        nodesInGenome = new HashSet<>();
        currentNode = -1;
    }


    /**
     * Collects all nodes that belong to the current genome.
     *
     * @param progressUpdater the instance that should be informed of the progress of this task
     * @throws IOException if an error occurs during IO operations
     */
    public void buildIndex(final ProgressUpdater progressUpdater) throws IOException {
        if (graph.getNodeArrays().length == 2) {
            return;
        }
        final GraphIterator graphIterator = new GraphIterator(gfaFile.getGraph());

        collectNodesOfGenome(progressUpdater);

        findLeftMostNode(graphIterator);

        int currentBaseCount = 1;
        visited.add(currentNode);
        while (currentNode != graph.getNodeArrays().length - 1) {
            baseCounts.put(currentBaseCount, currentNode);
            currentBaseCount += graph.getSequenceLength(currentNode);

            final int oldNode = currentNode;

            findNextNode(graphIterator);

            if (oldNode == currentNode) {
                break;
            }

            visited.add(currentNode);
        }
    }

    /**
     * Finds the node belonging to the given base, if it exists.
     *
     * @param base the base number
     * @return the node belonging to the given base or -1 if no node has been found
     */
    public int getNodeByBase(final int base) {
        final Map.Entry<Integer, Integer> baseCountEntry = baseCounts.floorEntry(base);
        if (baseCountEntry == null) {
            return -1;
        }
        return baseCountEntry.getValue();
    }

    /**
     * Returns the offset within a node that a point in the coordinate system has.
     *
     * @param base the base coordinate within the current genome
     * @return the base offset within the node, or -1 if no node has been found
     */
    public int getBaseOffsetWithinNode(final int base) {
        final Integer floorKey = baseCounts.floorKey(base);
        if (floorKey == null) {
            return -1;
        }
        return base - floorKey;
    }

    /**
     * Collects all nodes that belong to the current genome and stores it in {@code nodesInGenome}.
     *
     * @param progressUpdater the instance that should be informed of the progress of this task
     * @throws IOException            if an error occurs during IO operations
     */
    @SuppressWarnings("squid:S1188")
    private void collectNodesOfGenome(final ProgressUpdater progressUpdater) throws IOException {
        final int[] counter = {0};
        final int[] currentProgress = {-1};
        Files.lines(Paths.get(gfaFile.getFileName())).forEach(line -> {
            if (line.charAt(0) != 'S') {
                return;
            }
            counter[0]++;

            final int oriIndex = line.indexOf(GENOME_LIST_HEADER_PREFIX) + GENOME_LIST_HEADER_PREFIX.length();
            final String genomesRaw = line.substring(oriIndex);
            final int tabIndex = genomesRaw.indexOf('\t');
            final String genomes;
            if (tabIndex == -1) {
                genomes = genomesRaw;
            } else {
                genomes = genomesRaw.substring(0, tabIndex);
            }

            handleGenomeString(genomes, counter[0]);

            final int newProgress = Math.round((100.0f * counter[0]) / (graph.getNodeArrays().length - 2));
            if (newProgress > currentProgress[0]) {
                progressUpdater.updateProgress(newProgress, "Indexing genomes...");
                currentProgress[0] = newProgress;
            }
        });
    }

    /**
     * Evaluates the genome field string of a node.
     *
     * @param genomes   the string of genomes
     * @param currentId the ID of the node that is being evaluated
     */
    private void handleGenomeString(final String genomes, final int currentId) {
        final StringTokenizer st = new StringTokenizer(genomes, ";");
        String token;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.equals(index) || token.equals(name)) {
                nodesInGenome.add(currentId);
                break;
            }
        }
    }

    /**
     * Finds the next node to the right of the current node.
     * <p>
     * If the found node has a left neighbour which was not yet visited, but is part of the genome, the found node is
     * flagged and then skipped. The next node is stored in {@code currentNode}.
     *
     * @param graphIterator the graph iterator
     */
    private void findNextNode(final GraphIterator graphIterator) {
        graphIterator.visitDirectNeighbours(currentNode, SequenceDirection.RIGHT, neighbour -> {
            if (neighbour == graph.getNodeArrays().length - 1 || !nodesInGenome.contains(neighbour)) {
                return;
            }

            final boolean[] flag = {false};

            graphIterator.visitDirectNeighbours(neighbour, SequenceDirection.LEFT, neighbourOfNeighbour -> {
                if (!visited.contains(neighbourOfNeighbour) && nodesInGenome.contains(neighbourOfNeighbour)) {
                    flag[0] = true;
                }
            });

            if (flag[0]) {
                return;
            }

            currentNode = neighbour;
        });
    }

    /**
     * Finds the left most node in the graph.
     * <p>
     * It first picks a random node within the genome, and then it goes as much to the left as possible to find the
     * left most node.
     *
     * @param graphIterator the graph iterator
     */
    private void findLeftMostNode(final GraphIterator graphIterator) {
        currentNode = nodesInGenome.iterator().next();

        graphIterator.visitIndirectNeighbours(currentNode, SequenceDirection.LEFT, neighbour -> {
            if (neighbour != 0 && nodesInGenome.contains(neighbour)) {
                currentNode = neighbour;
            }
        });
    }
}
