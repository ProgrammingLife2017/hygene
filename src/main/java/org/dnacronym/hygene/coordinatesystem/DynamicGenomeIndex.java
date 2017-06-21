package org.dnacronym.hygene.coordinatesystem;

import org.dnacronym.hygene.graph.Graph;
import org.dnacronym.hygene.graph.GraphIterator;
import org.dnacronym.hygene.graph.SequenceDirection;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.MetadataParseException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    }


    public void buildIndex() {
        final GraphIterator graphIterator = new GraphIterator(gfaFile.getGraph());

        if (graph.getNodeArrays().length == 2) {
            return;
        }
        currentBaseCount = 0;
        visited = new HashSet<>();
        baseCounts = new TreeMap<>();

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

    public int getNodeId(final int base) {
        return baseCounts.floorEntry(base).getValue();
    }

    private boolean containsGenome(final int nodeId) {
        try {
            final List<String> nodeGenomes = gfaFile.parseNodeMetadata(graph.getByteOffset(nodeId)).getGenomes();
            return nodeGenomes.contains(genomeIndex) || nodeGenomes.contains(genomeName);
        } catch (final MetadataParseException e) {
            return false;
        }
    }
}
