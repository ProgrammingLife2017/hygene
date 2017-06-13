package org.dnacronym.hygene.coordinatesystem;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.models.GraphIterator;
import org.dnacronym.hygene.models.SequenceDirection;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.MetadataParser;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.parser.ProgressUpdater;
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
    private static final Logger LOGGER = LogManager.getLogger(GfaFile.class);

    private final GfaFile gfaFile;
    private final FileGenomeIndex fileGenomeIndex;

    /**
     * The current total base count per genome.
     */
    private final Map<String, Integer> genomeBaseCounts;
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
        this.genomeNames = new ArrayList<>();
    }


    /**
     * Populate the file index with genome coordinate system index points.
     *
     * @throws ParseException in case of errors during parsing of the GFA file
     */
    public void populateIndex(final ProgressUpdater progressUpdater) throws ParseException, SQLException, IOException {
        if (fileGenomeIndex.isIndexed()) {
            progressUpdater.updateProgress(100, "Genomes already indexed.");
            return;
        }

        fileGenomeIndex.cleanIndex();

        loadGenomeList();
        buildIndex(progressUpdater);

        fileGenomeIndex.markIndexAsComplete();
    }

    /**
     * Returns the node that contains the wanted genome-base point in the coordinate system.
     *
     * @param genome the name of the genome to query for
     * @param base   the base to query for
     * @return if found, the point of the base in that node
     * @throws SQLException in the case of an error during SQL operations
     */
    public Optional<@Nullable GenomePoint> getGenomePoint(final String genome, final int base) throws SQLException {
        final int genomeId = genomeNames.indexOf(genome);

        return Optional.of(fileGenomeIndex.getGenomePoint(genomeId, base));
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
    private void buildIndex(final ProgressUpdater progressUpdater) {
        final GraphIterator graphIterator = new GraphIterator(gfaFile.getGraph());

        graphIterator.visitAll(SequenceDirection.RIGHT, nodeId -> {
            evaluateNode(nodeId);
            progressUpdater.updateProgress(
                    Math.round((100.0f * nodeId) / (gfaFile.getGraph().getNodeArrays().length - 2)),
                    "Indexing the genomes...");
        });
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
                final int genomeIndex;
                final String genomeName;
                if (StringUtils.isNumeric(genome)) {
                    genomeIndex = Integer.parseInt(genome);
                    genomeName = genomeNames.get(genomeIndex);
                } else {
                    genomeIndex = genomeNames.indexOf(genome);
                    genomeName = genome;
                }

                final Integer genomeBaseCount = genomeBaseCounts.get(genomeName);

                if (genomeBaseCount == null) {
                    throw new ParseException("Unrecognized genome found at node " + nodeId + ".");
                }

                fileGenomeIndex.addGenomeIndexPoint(new GenomePoint(genomeIndex, genomeBaseCount, nodeId));

                final int nodeBaseCount = gfaFile.getGraph().getSequenceLength(nodeId);
                genomeBaseCounts.put(genomeName, genomeBaseCount + nodeBaseCount);
            }
        } catch (final ParseException | SQLException e) {
            LOGGER.warn("Failed to read metadata of node " + nodeId + ".", e);
        }
    }

    /**
     * Returns the list of genome names.
     *
     * @return the list of genome names
     */
    public List<String> getGenomeNames() {
        return genomeNames;
    }
}
