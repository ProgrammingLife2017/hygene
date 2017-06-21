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
    private int currentBaseCount;
    private TreeMap<Integer, Integer> baseCounts;
    int currentNode = -1;


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
    }


    public void buildIndex() throws MetadataParseException {
        final GraphIterator graphIterator = new GraphIterator(gfaFile.getGraph());

        if (graph.getNodeArrays().length == 2) {
            return;
        }
        currentBaseCount = 0;

        collectNodesOfGenome();

        // Pick random node in genomeName
        for (int i = 1; i < graph.getNodeArrays().length - 1; i++) {
            if (containsGenome(i)) {
                currentNode = i;
                break;
            }
        }

        // Get left-most node of genomeName
        graphIterator.visitIndirectNeighbours(currentNode, SequenceDirection.LEFT, neighbour -> {
            if (neighbour != 0 && containsGenome(neighbour)) {
                currentNode = neighbour;
            }
        });

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

    public int getNodeByBase(final int base) {
        return baseCounts.floorEntry(base).getValue();
    }

    private boolean containsGenome(final int nodeId) {
        return nodesInGenome.contains(nodeId);
    }

    private void collectNodesOfGenome() throws MetadataParseException {
        try {
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

                    final StringTokenizer st = new StringTokenizer(genomes, ";");
                    String token;
                    while (st.hasMoreTokens()) {
                        token = st.nextToken();
                        if (token.equals(genomeIndex) || token.equals(genomeName)) {
                            nodesInGenome.add(counter[0]);
                            break;
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
