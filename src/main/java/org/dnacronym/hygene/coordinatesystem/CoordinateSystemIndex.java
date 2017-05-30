package org.dnacronym.hygene.coordinatesystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.models.GraphIterator;
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
import java.util.StringTokenizer;


/**
 * Class responsible for indexing the genome coordinate system of a file.
 */
public final class CoordinateSystemIndex {
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
     * Constructs a new {@link CoordinateSystemIndex} instance.
     *
     * @param gfaFile      the GFA file to index
     * @param fileDatabase the {@link FileDatabase} instance to be used for storing and retrieving data
     */
    public CoordinateSystemIndex(final GfaFile gfaFile, final FileDatabase fileDatabase) {
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
     *
     * @param genome the name of the genome to query for
     * @param base   the base to query for
     * @return the ID of the closest node to the desired point
     * @throws SQLException in the case of an error during SQL operations
     */
    public int getClosestNodeId(final String genome, final int base) throws SQLException {
        return fileGenomeIndex.getClosestNodeToBase(getGenomeId(genome), base);
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
                genomeBaseDiffCounts.put(nextGenome, -baseCacheInterval);
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
        graphIterator.visitAll(SequenceDirection.RIGHT, nodeId -> {
            if (nodeId == 0 || nodeId == gfaFile.getGraph().getNodeArrays().length - 1) {
                return;
            }

            try {
                final List<String> nodeGenomes = gfaFile.getGraph().getNode(nodeId).retrieveMetadata().getGenomes();

                for (final String genome : nodeGenomes) {
                    final Integer previousBaseCount = genomeBaseDiffCounts.get(genome);
                    final Integer genomeTotalCount = genomeBaseCounts.get(genome);

                    if (previousBaseCount == null || genomeTotalCount == null) {
                        throw new ParseException("Unrecognized genome found at node " + nodeId + ".");
                    }

                    final int nodeBaseCount = gfaFile.getGraph().getSequenceLength(nodeId);
                    if (previousBaseCount + nodeBaseCount >= baseCacheInterval) {
                        final int baseIndexPosition = previousBaseCount + nodeBaseCount + genomeTotalCount;
                        fileGenomeIndex.addGenomeIndexPoint(getGenomeId(genome), baseIndexPosition, nodeId);
                        genomeBaseDiffCounts.put(genome, 0);
                        genomeBaseCounts.put(genome, baseIndexPosition);
                    } else {
                        genomeBaseCounts.put(genome, previousBaseCount + nodeBaseCount);
                    }
                }
            } catch (final ParseException | SQLException e) {
                LOGGER.warn("Failed to read metadata of node " + nodeId + ".", e);
            }
        });
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
     * Returns the baseCacheInterval.
     *
     * @return the baseCacheInterval
     */
    public int getBaseCacheInterval() {
        return baseCacheInterval;
    }

    /**
     * The interval of bases to be cached.
     *
     * @param baseCacheInterval the base cache interval
     */
    public void setBaseCacheInterval(final int baseCacheInterval) {
        this.baseCacheInterval = baseCacheInterval;
    }
}
