package org.dnacronym.hygene.coordinatesystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.GraphIterator;
import org.dnacronym.hygene.models.NodeMetadata;
import org.dnacronym.hygene.models.SequenceDirection;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.MetadataParser;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.persistence.FileDatabase;
import org.dnacronym.hygene.persistence.FileGenomeIndex;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;


/**
 * Class responsible for indexing the genome coordinate system of a file.
 */
public final class GenomeIndex {
    private static final int DEFAULT_BASE_CACHE_INTERVAL = 1000;
    private static final Logger LOGGER = LogManager.getLogger(GfaFile.class);

    private final GfaFile gfaFile;
    private final FileGenomeIndex fileGenomeIndex;

    /**
     * The minimal base distance between two adjacent index points of the same genome.
     */
    private int baseCacheInterval = DEFAULT_BASE_CACHE_INTERVAL;
    /**
     * The current total base count per genome.
     */
    private final Map<String, Integer> genomeBaseCounts;
    /**
     * The base count difference per genome, counted from the last index point of that genome.
     */
    private final Map<String, Integer> genomeBaseDiffCounts;
    /**
     * A list of all genome names (used to map names to numeric indices and back).
     */
    private final List<String> genomeNames;


    /**
     * Constructs a new {@link GenomeIndex} instance.
     *
     * @param gfaFile      the GFA file to index
     * @param fileDatabase the {@link FileDatabase} instance to be used for storing and retrieving data
     */
    public GenomeIndex(final GfaFile gfaFile, final FileDatabase fileDatabase) {
        this.gfaFile = gfaFile;
        this.fileGenomeIndex = fileDatabase.getFileGenomeIndex();
        this.genomeBaseCounts = new HashMap<>();
        this.genomeBaseDiffCounts = new HashMap<>();
        this.genomeNames = new ArrayList<>();
    }


    /**
     * Populate the file index with genome coordinate system index points.
     *
     * @throws ParseException in case of errors during parsing of the GFA file
     */
    public void populateIndex() throws ParseException {
        loadGenomeList();
        buildIndex();
    }

    /**
     * Looks for the node that comes closest to the wanted genome-base point in the coordinate system.
     * <p>
     * Does a directed search from the closest index point to find the node containing the desired genome-base point.
     *
     * @param genome the name of the genome to query for
     * @param base   the base to query for
     * @return if found, the point of the base in that node
     * @throws SQLException in the case of an error during SQL operations
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // The instance created is unique and only created once
    public Optional<GenomePoint> getClosestNode(final String genome, final int base) throws SQLException {
        final int genomeId = getGenomeId(genome);

        final GenomePoint closestIndexPoint = fileGenomeIndex.getClosestNodeToBase(genomeId, base);
        if (closestIndexPoint == null) {
            LOGGER.error("Found no index point for genome " + genome + " and base " + base + ".");
            return Optional.empty();
        }

        final SequenceDirection sequenceDirection = base - closestIndexPoint.getBase() > 0
                ? SequenceDirection.RIGHT : SequenceDirection.LEFT;

        int currentBase = closestIndexPoint.getBase();
        final int[] currentNodeId = {closestIndexPoint.getNodeId()};

        while (Math.abs(currentBase - closestIndexPoint.getBase()) <= DEFAULT_BASE_CACHE_INTERVAL) {
            if (base >= currentBase && base < currentBase + gfaFile.getGraph().getSequenceLength(currentNodeId[0])) {
                return Optional.of(new GenomePoint(genomeId, base, currentNodeId[0], base - currentBase));
            }

            currentBase += gfaFile.getGraph().getSequenceLength(currentNodeId[0]);
            final int oldNodeId = currentNodeId[0];
            gfaFile.getGraph().iterator().visitDirectNeighbours(currentNodeId[0], sequenceDirection, nodeId -> {
                try {
                    final NodeMetadata nodeMetadata = gfaFile.parseNodeMetadata(
                            gfaFile.getGraph().getLineNumber(nodeId));
                    if (nodeMetadata.getGenomes().contains(genome)) {
                        currentNodeId[0] = nodeId;
                    }
                } catch (final ParseException e) {
                    LOGGER.error("Parsing node metadata of " + nodeId + " for genome coordinate lookup failed.", e);
                }
            });

            if (oldNodeId == currentNodeId[0]) {
                // As we are assuming that paths of genomes must be connected, this cannot be the case
                break;
            }
        }

        return Optional.empty();
    }


    /**
     * Retrieves the list of genomes in the GFA file from the header.
     *
     * @throws ParseException in case of errors during parsing of the GFA file
     */
    private void loadGenomeList() throws ParseException {
        final BufferedReader bufferedReader = gfaFile.readFile();

        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("H\t" + MetadataParser.GENOME_LIST_HEADER_PREFIX)) {
                    parseHeaderLine(line);
                    return;
                }
            }
        } catch (final IOException e) {
            throw new ParseException("An error while reading the GFA file.", e);
        }
    }

    /**
     * Parses one header line and checks for genome metadata.
     *
     * @param line the line to parse
     * @throws ParseException in case of errors during parsing of the header line
     */
    private void parseHeaderLine(final String line) throws ParseException {
        final StringTokenizer stringTokenizer = new StringTokenizer(line, "\t");

        stringTokenizer.nextToken(); // Ignore "H" token
        final String headerBody = stringTokenizer.nextToken();
        if (!headerBody.startsWith(MetadataParser.GENOME_LIST_HEADER_PREFIX)) {
            return;
        }

        final String genomeListString = headerBody.substring(MetadataParser.GENOME_LIST_HEADER_PREFIX.length());
        final StringTokenizer bodyTokenizer = new StringTokenizer(genomeListString, ";");

        while (bodyTokenizer.hasMoreTokens()) {
            final String nextGenome = bodyTokenizer.nextToken();

            if (!nextGenome.isEmpty()) {
                genomeBaseCounts.put(nextGenome, 0);
                genomeBaseDiffCounts.put(nextGenome, -1);
                genomeNames.add(nextGenome);
            }
        }

        if (genomeBaseCounts.isEmpty()) {
            throw new ParseException("Expected at least one genome to be present in GFA file.");
        }
    }

    /**
     * Iterates through the graph and saves index points at selected base locations.
     */
    private void buildIndex() {
        final GraphIterator graphIterator = new GraphIterator(gfaFile.getGraph());
        graphIterator.visitAll(SequenceDirection.RIGHT, this::evaluateNode);
    }

    /**
     * Evaluates a single node and updates the index accordingly.
     *
     * @param nodeId the ID of the node
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // The instances created are needed for data transfer
    private void evaluateNode(final int nodeId) {
        if (nodeId == 0 || nodeId == gfaFile.getGraph().getNodeArrays().length - 1) {
            return;
        }

        try {
            final List<String> nodeGenomes = gfaFile.getGraph().getNode(nodeId).retrieveMetadata().getGenomes();

            for (final String genome : nodeGenomes) {
                final Integer genomeBaseDiffCount = genomeBaseDiffCounts.get(genome);
                final Integer genomeBaseCount = genomeBaseCounts.get(genome);

                if (genomeBaseDiffCount == null || genomeBaseCount == null) {
                    throw new ParseException("Unrecognized genome found at node " + nodeId + ".");
                }

                final int nodeBaseCount = gfaFile.getGraph().getSequenceLength(nodeId);

                if (genomeBaseDiffCount == -1) {
                    fileGenomeIndex.addGenomeIndexPoint(new GenomePoint(getGenomeId(genome), 0, nodeId));
                    genomeBaseDiffCounts.put(genome, 0);
                } else if (genomeBaseDiffCount + nodeBaseCount >= baseCacheInterval) {
                    final int baseIndexPosition = genomeBaseDiffCount + nodeBaseCount + genomeBaseCount;
                    fileGenomeIndex.addGenomeIndexPoint(
                            new GenomePoint(getGenomeId(genome), baseIndexPosition, nodeId));
                    genomeBaseDiffCounts.put(genome, 0);
                    genomeBaseCounts.put(genome, baseIndexPosition);
                } else {
                    genomeBaseCounts.put(genome, genomeBaseCount + nodeBaseCount);
                }
            }
        } catch (final ParseException | SQLException e) {
            LOGGER.warn("Failed to read metadata of node " + nodeId + ".", e);
        }
    }

    /**
     * Gets the genome ID belonging to the given genome name.
     *
     * @param genomeName the name of the genome
     * @return the corresponding internal genome ID
     */
    private int getGenomeId(final String genomeName) {
        return genomeNames.indexOf(genomeName);
    }

    /**
     * Returns the base cache interval.
     *
     * @return the base cache interval
     */
    int getBaseCacheInterval() {
        return baseCacheInterval;
    }

    /**
     * Sets the interval of bases to be cached.
     *
     * @param baseCacheInterval the base cache interval
     */
    void setBaseCacheInterval(final int baseCacheInterval) {
        this.baseCacheInterval = baseCacheInterval;
    }
}
