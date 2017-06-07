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
import java.util.regex.Pattern;
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
    public Query(final GfaFile gfaFile) {
        this.gfaFile = gfaFile;
        this.numNodesInGraph = gfaFile.getGraph().getNodeArrays().length;
    }


    /**
     * Executes the given regex query on all node names.
     *
     * @param regex the regex to test against
     * @return the IDs of nodes with names matching the regex
     * @throws ParseException if the GFA file is invalid in some form
     */
    public Set<Integer> executeNameRegexQuery(final String regex) throws ParseException {
        return executeQuery(nodeMetadata -> {
            final Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(nodeMetadata.getName()).matches();
        });
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

        for (int batchIndex = 0; batchIndex < numBatches; batchIndex++) {
            final Map<Integer, Integer> sortedNodeIds = getLineNumbersOfBatch(batchIndex);
            final Map<Integer, NodeMetadata> batchMetadata = metadataParser.parseNodeMetadata(gfaFile, sortedNodeIds);
            nodeIds.addAll(batchMetadata.entrySet().stream()
                    .filter(entry -> isInQuery.test(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet()));
        }

        return nodeIds;
    }

    /**
     * Returns a map of node IDs to line numbers, sorted by line number.
     *
     * @param batchIndex the index of the batch to generate mappings for
     * @return the sorted map of node IDs to line numbers
     */
    private Map<Integer, Integer> getLineNumbersOfBatch(final int batchIndex) {
        final List<Integer> batchNodeIds = IntStream.rangeClosed(
                batchIndex * BATCH_SIZE + 1, Math.max(batchIndex * (BATCH_SIZE + 1), numNodesInGraph - 2)
        ).boxed().collect(Collectors.toList());

        return batchNodeIds.stream()
                .sorted(Comparator.comparingInt(nodeId -> gfaFile.getGraph().getLineNumber(nodeId)))
                .collect(Collectors.toMap(
                        nodeId -> nodeId,
                        nodeId -> gfaFile.getGraph().getLineNumber(nodeId),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }
}
