package org.dnacronym.hygene.graph;

import org.dnacronym.hygene.graph.metadata.NodeMetadata;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.MetadataParseException;
import org.dnacronym.hygene.parser.MetadataParser;

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
public final class SearchQuery {
    private static final int BATCH_SIZE = 1000;

    private final GfaFile gfaFile;
    private final Graph graph;
    private final int numberOfNodesInGraph;


    /**
     * Constructs a new {@link SearchQuery} instance.
     *
     * @param gfaFile the file to be queried
     */
    public SearchQuery(final GfaFile gfaFile) {
        this.gfaFile = gfaFile;
        this.graph = gfaFile.getGraph();
        this.numberOfNodesInGraph = graph.getNodeArrays().length;
    }


    /**
     * Executes the given regex query on all node names.
     *
     * @param regex the regex to test against
     * @return the IDs of nodes with names matching the regex
     * @throws MetadataParseException if the GFA file is invalid in some form
     */
    public Set<Integer> executeNameRegexQuery(final String regex) throws MetadataParseException {
        return executeQuery(nodeMetadata -> Pattern.compile(regex).matcher(nodeMetadata.getName()).matches());
    }


    /**
     * Executes the given regex query on all node sequences.
     *
     * @param regex the regex to test against
     * @return the IDs of nodes with sequences matching the regex
     * @throws MetadataParseException if the GFA file is invalid in some form
     */
    public Set<Integer> executeSequenceRegexQuery(final String regex) throws MetadataParseException {
        return executeQuery(nodeMetadata -> Pattern.compile(regex).matcher(nodeMetadata.getSequence()).matches());
    }

    /**
     * Executes a query on the GFA file.
     *
     * @param isInQuery predicate indicating whether a node with certain metadata should be in the query results
     * @return the set of node IDs that conform to this predicate
     * @throws MetadataParseException if the GFA file is invalid in some form
     */
    public Set<Integer> executeQuery(final Predicate<NodeMetadata> isInQuery) throws MetadataParseException {
        final MetadataParser metadataParser = new MetadataParser();
        final int numBatches = numberOfNodesInGraph / BATCH_SIZE + 1;
        final Set<Integer> nodeIds = new HashSet<>();

        for (int batchIndex = 0; batchIndex < numBatches; batchIndex++) {
            final Map<Integer, Long> sortedNodeIds = getByteOffsetsBatch(batchIndex);
            final Map<Integer, NodeMetadata> batchMetadata = metadataParser.parseNodeMetadata(gfaFile, sortedNodeIds);
            nodeIds.addAll(batchMetadata.entrySet().stream()
                    .filter(entry -> isInQuery.test(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet()));
        }

        return nodeIds;
    }

    /**
     * Returns a map of node IDs to byte offsets, sorted by byte offset.
     *
     * @param batchIndex the index of the batch to generate mappings for
     * @return the sorted map of node IDs to byte offsets
     */
    private Map<Integer, Long> getByteOffsetsBatch(final int batchIndex) {
        final List<Integer> batchNodeIds = IntStream
                .rangeClosed(batchIndex * BATCH_SIZE + 1,
                        Math.max(batchIndex * (BATCH_SIZE + 1), numberOfNodesInGraph - 2))
                .boxed().collect(Collectors.toList());

        return batchNodeIds.stream()
                .sorted(Comparator.comparingLong(nodeId -> graph.getByteOffset(nodeId)))
                .collect(Collectors.toMap(
                        nodeId -> nodeId,
                        nodeId -> graph.getByteOffset(nodeId),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }
}
