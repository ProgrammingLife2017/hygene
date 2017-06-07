package org.dnacronym.hygene.graph;

import org.dnacronym.hygene.models.NodeMetadata;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.MetadataParser;
import org.dnacronym.hygene.parser.ParseException;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Class to be used for a query on node metadata of a graph.
 */
public final class Query {
    private static final int BATCH_SIZE = 1000;

    private final GfaFile gfaFile;
    private final int numNodesInGraph;


    /**
     * Constructs a new {@link Query} instance.
     *
     * @param gfaFile the file to be queried
     */
    public Query(GfaFile gfaFile) {
        this.gfaFile = gfaFile;
        this.numNodesInGraph = gfaFile.getGraph().getNodeArrays().length;
    }


    /**
     * Executes a query on the GFA file.
     *
     * @param isInQuery predicate indicating whether a node with certain metadata should be in the query results
     * @return the set of node IDs that conform to this predicate
     * @throws ParseException if the GFA file is invalid in some form
     */
    public Set<Integer> executeQuery(final Predicate<NodeMetadata> isInQuery) throws ParseException {
        final MetadataParser metadataParser = new MetadataParser();

        final int numBatches = numNodesInGraph / BATCH_SIZE;
        final Set<Integer> nodeIds = new HashSet<>();

        for (int batch = 0; batch < numBatches; batch++) {
            final List<Integer> batchNodeIds = IntStream.rangeClosed(
                    batch * BATCH_SIZE + 1,
                    Math.max(batch * (BATCH_SIZE + 1), numNodesInGraph - 2))
                    .boxed().collect(Collectors.toList());

            final Map<Integer, Integer> sortedNodeIds = batchNodeIds.stream()
                    .sorted(Comparator.comparingInt(nodeId -> gfaFile.getGraph().getLineNumber(nodeId)))
                    .collect(Collectors.toMap(
                            nodeId -> nodeId,
                            nodeId -> gfaFile.getGraph().getLineNumber(nodeId),
                            (oldValue, newValue) -> oldValue,
                            LinkedHashMap::new
                    ));
            final Map<Integer, NodeMetadata> batchMetadata = metadataParser.parseNodeMetadata(gfaFile, sortedNodeIds);
            nodeIds.addAll(
                    batchMetadata.entrySet().stream()
                            .filter(entry -> isInQuery.test(entry.getValue()))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toSet())
            );
        }

        return nodeIds;
    }
}
