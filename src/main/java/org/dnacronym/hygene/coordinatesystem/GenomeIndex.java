package org.dnacronym.hygene.coordinatesystem;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dnacronym.hygene.graph.Graph;
import org.dnacronym.hygene.graph.GraphIterator;
import org.dnacronym.hygene.graph.SequenceDirection;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.MetadataParseException;
import org.dnacronym.hygene.parser.MetadataParser;
import org.dnacronym.hygene.parser.GfaParseException;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.dnacronym.hygene.persistence.FileDatabase;
import org.dnacronym.hygene.persistence.FileGenomeIndex;
import org.dnacronym.hygene.ui.progressbar.StatusBar;

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
    private final Graph graph;
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
        this.graph = gfaFile.getGraph();
        this.fileGenomeIndex = fileDatabase.getFileGenomeIndex();
        this.genomeBaseCounts = new HashMap<>();
        this.genomeNames = new ArrayList<>();
    }


    /**
     * Populate the file index with genome coordinate system index points.
     *
     * @param progressUpdater the instance that should be informed of the progress of this task
     */
    public void populateIndex(final ProgressUpdater progressUpdater) {
        try {
            loadGenomeList();

            if (fileGenomeIndex.isIndexed()) {
                progressUpdater.updateProgress(StatusBar.PROGRESS_MAX, "Genomes already indexed.");
                return;
            }

            fileGenomeIndex.cleanIndex();
            buildIndex(progressUpdater);
            fileGenomeIndex.markIndexAsComplete();
        } catch (final SQLException | GenomeParseException e) {
            LOGGER.error("Unable to load genome info from file.", e);
        }
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
     * @throws GenomeParseException in case of errors during parsing of the GFA file
     */
    private void loadGenomeList() throws GenomeParseException {
        try {
            final BufferedReader bufferedReader = gfaFile.readFile();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("H\t" + MetadataParser.GENOME_LIST_HEADER_PREFIX)) {
                    parseHeaderLine(line);
                    return;
                }
            }
        } catch (final GfaParseException | IOException e) {
            throw new GenomeParseException("An error while reading the GFA file.", e);
        }
    }

    /**
     * Parses one header line and checks for genome metadata.
     *
     * @param line the line to parse
     * @throws GfaParseException in case of errors during parsing of the header line
     */
    private void parseHeaderLine(final String line) throws GfaParseException {
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
                final String name = nextGenome.lastIndexOf('.') > 0
                        ? nextGenome.substring(0, nextGenome.lastIndexOf('.')) : nextGenome;

                genomeBaseCounts.put(name, 0);
                genomeNames.add(name);
            }
        }

        if (genomeBaseCounts.isEmpty()) {
            throw new GfaParseException("Expected at least one genome to be present in GFA file.");
        }
    }

    /**
     * Iterates through the graph and saves index points at selected base locations.
     *
     * @param progressUpdater the instance that should be informed of the progress of this task
     */
    private void buildIndex(final ProgressUpdater progressUpdater) {
        final GraphIterator graphIterator = new GraphIterator(graph);

        final int[] currentProgress = {-1};
        fileGenomeIndex.setAutoCommit(false);

        graphIterator.visitAll(SequenceDirection.RIGHT, nodeId -> {
            evaluateNode(nodeId);

            final int newProgress = Math.round((100.0f * nodeId) / (graph.getNodeArrays().length - 2));
            if (newProgress > currentProgress[0]) {
                progressUpdater.updateProgress(newProgress, "Indexing genomes...");
                currentProgress[0] = newProgress;
                fileGenomeIndex.commit();
            }
        });

        fileGenomeIndex.setAutoCommit(true);
    }

    /**
     * Evaluates a single node and updates the index accordingly.
     *
     * @param nodeId the ID of the node
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // The instances created are needed for data transfer
    private void evaluateNode(final int nodeId) {
        if (nodeId == 0 || nodeId == graph.getNodeArrays().length - 1) {
            return;
        }

        try {
            final List<String> nodeGenomes = gfaFile.parseNodeMetadata(graph.getByteOffset(nodeId)).getGenomes();

            for (final String genome : nodeGenomes) {
                final String noExtensionGenome = genome.lastIndexOf('.') > 0
                        ? genome.substring(0, genome.lastIndexOf('.')) : genome;

                final int genomeIndex;
                final String genomeName;
                if (StringUtils.isNumeric(noExtensionGenome)) {
                    genomeIndex = Integer.parseInt(noExtensionGenome);
                    genomeName = genomeNames.get(genomeIndex);
                } else {
                    genomeIndex = genomeNames.indexOf(noExtensionGenome);
                    genomeName = noExtensionGenome;
                }

                final Integer genomeBaseCount = genomeBaseCounts.get(genomeName);

                if (genomeBaseCount == null) {
                    throw new GenomeParseException("Unrecognized genome found at node " + nodeId + ".");
                }

                fileGenomeIndex.addGenomeIndexPoint(new GenomePoint(genomeIndex, genomeBaseCount, nodeId));

                final int nodeBaseCount = graph.getSequenceLength(nodeId);
                genomeBaseCounts.put(genomeName, genomeBaseCount + nodeBaseCount);
            }
        } catch (final MetadataParseException | GenomeParseException | SQLException e) {
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
