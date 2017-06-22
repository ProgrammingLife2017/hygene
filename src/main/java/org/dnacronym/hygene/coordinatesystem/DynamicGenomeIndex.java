package org.dnacronym.hygene.coordinatesystem;

import org.dnacronym.hygene.graph.Graph;
import org.dnacronym.hygene.graph.GraphIterator;
import org.dnacronym.hygene.graph.SequenceDirection;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.MetadataParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;


/**
 * .
 */
public class DynamicGenomeIndex {
    private final GfaFile gfaFile;
    private final Graph graph;
    private final String genomeName;
    private final String genomeIndex;
    private Set<Integer> visited;
    private Set<Integer> nodesInGenome;
    private TreeMap<Integer, Integer> baseCounts;
    private int currentNode;


    /**
     * Constructs a {@link DynamicGenomeIndex} instance.
     *
     * @param gfaFile    the GFA file
     * @param genomeName the name of the genome to be indexed
     */
    public DynamicGenomeIndex(final GfaFile gfaFile, final String genomeName) {
        this.gfaFile = gfaFile;
        this.graph = gfaFile.getGraph();
        this.genomeName = genomeName;
        this.genomeIndex = gfaFile.getGenomeMapping().entrySet().stream()
                .filter(entry -> entry.getValue().equals(genomeName))
                .map(Map.Entry::getKey).findFirst().orElse(genomeName);

        visited = new HashSet<>();
        baseCounts = new TreeMap<>();
        nodesInGenome = new HashSet<>();
        currentNode = -1;
    }


    /**
     * Collects all nodes that belong to the current genome.
     *
     * @throws MetadataParseException if the syntax of the GFA file is invalid in some form
     * @throws IOException            if an error occurs during IO operations
     */
    @SuppressWarnings("squid:S2583") // false positive due to currentNode being updated in a lambda
    public void buildIndex() throws MetadataParseException, IOException {
        final GraphIterator graphIterator = new GraphIterator(gfaFile.getGraph());

        if (graph.getNodeArrays().length == 2) {
            return;
        }
        collectNodesOfGenome();

        // Pick random node in genomeName
        currentNode = nodesInGenome.iterator().next();

        // Get left-most node of genomeName
        graphIterator.visitIndirectNeighbours(currentNode, SequenceDirection.LEFT, neighbour -> {
            if (neighbour != 0 && containsGenome(neighbour)) {
                currentNode = neighbour;
            }
        });

        int currentBaseCount = 1;
        visited.add(currentNode);
        while (currentNode != graph.getNodeArrays().length - 1) {
            baseCounts.put(currentBaseCount, currentNode);
            currentBaseCount += graph.getSequenceLength(currentNode);

            final int oldNode = currentNode;
            graphIterator.visitDirectNeighbours(currentNode, SequenceDirection.RIGHT, neighbour -> {
                if (neighbour == graph.getNodeArrays().length - 1 || !containsGenome(neighbour)) {
                    return;
                }

                final boolean[] flag = {false};

                graphIterator.visitDirectNeighbours(neighbour, SequenceDirection.LEFT, neighbourOfNeighbour -> {
                    if (!visited.contains(neighbourOfNeighbour) && containsGenome(neighbourOfNeighbour)) {
                        flag[0] = true;
                    }
                });

                if (flag[0]) {
                    return;
                }

                currentNode = neighbour;
            });
            if (oldNode == currentNode) {
                break;
            }

            visited.add(currentNode);
        }
    }

    /**
     * Returns the ID of the node this base belongs to.
     *
     * @param base the base coordinate within the current genome
     * @return the ID of the node this base belongs to
     */
    public int getNodeByBase(final int base) {
        return baseCounts.floorEntry(base).getValue();
    }

    /**
     * Returns the offset within a node that a point in the coordinate system has.
     *
     * @param base the base coordinate within the current genome
     * @return the base offset within the node
     */
    public int getBaseOffsetWithinNode(final int base) {
        return base - baseCounts.floorKey(base);
    }

    /**
     * Checks whether a certain node is in the genome.
     *
     * @param nodeId the ID of that node
     * @return {@code true} iff. that node is in the genome
     */
    private boolean containsGenome(final int nodeId) {
        return nodesInGenome.contains(nodeId);
    }

    /**
     * Collects all nodes that belong to the current genome.
     *
     * @throws MetadataParseException if the syntax of the GFA file is invalid in some form
     * @throws IOException            if an error occurs during IO operations
     */
    private void collectNodesOfGenome() throws MetadataParseException, IOException {
        final int[] counter = {0};
        Files.lines(Paths.get(gfaFile.getFileName())).forEach(line -> {
            if (line.startsWith("S")) {
                counter[0]++;

                final int oriIndex = line.indexOf("ORI:Z:") + 6;
                final String genomesRaw = line.substring(oriIndex);
                final int tabIndex = genomesRaw.indexOf('\t');
                final String genomes;
                if (tabIndex == -1) {
                    genomes = genomesRaw;
                } else {
                    genomes = genomesRaw.substring(0, tabIndex);
                }

                handleGenomeString(genomes, counter[0]);
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
            if (token.equals(genomeIndex) || token.equals(genomeName)) {
                nodesInGenome.add(currentId);
                break;
            }
        }
    }
}
