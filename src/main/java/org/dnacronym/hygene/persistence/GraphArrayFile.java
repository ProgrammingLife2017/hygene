package org.dnacronym.hygene.persistence;

import org.dnacronym.hygene.parser.ProgressUpdater;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Represents a file containing a cached graph array representation of a GFA file.
 */
public final class GraphArrayFile {
    private static final int PROGRESS_UPDATE_INTERVAL = 50000;
    private static final int PROGRESS_TOTAL = 100;

    private static final String NODE_VALUE_SEPARATOR = " ";
    private static final String NODE_ARRAY_SEPARATOR = "\n";

    private static final int WRITE_BUFFER_SIZE = 4 * (int) Math.pow(1024, 2);

    private final File file;


    /**
     * Constructs and initializes a {@link GraphArrayFile} object.
     *
     * @param file the cache file
     */
    public GraphArrayFile(final File file) {
        this.file = file;
    }


    /**
     * Reads a cached graph and parses it to the internal graph array data structure.
     *
     * @param graphSize       the number of nodes in the graph
     * @param progressUpdater a {@link ProgressUpdater} to notify interested parties on progress updates
     * @return internal graph array data structure from cache file
     * @throws IOException if the cache file cannot be read
     */
    public int[][] read(final int graphSize, final ProgressUpdater progressUpdater) throws IOException {
        final int[][] graph = new int[graphSize][];

        try (final BufferedReader cache = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(new FileInputStream(file)), StandardCharsets.UTF_8))) {
            String node;
            StringTokenizer nodeTokenizer;
            int nodeIndex = 0;
            while ((node = cache.readLine()) != null) {
                if (nodeIndex % PROGRESS_UPDATE_INTERVAL == 0) {
                    progressUpdater.updateProgress(PROGRESS_TOTAL * nodeIndex / graphSize);
                }

                nodeTokenizer = new StringTokenizer(node, NODE_VALUE_SEPARATOR);

                graph[nodeIndex] = new int[nodeTokenizer.countTokens()];

                int valueIndex = 0;
                while (nodeTokenizer.hasMoreTokens()) {
                    graph[nodeIndex][valueIndex] = Integer.parseInt(nodeTokenizer.nextToken());
                    valueIndex++;
                }

                nodeIndex++;
            }
        }

        return graph;
    }

    /**
     * Writes a representation of the internal graph array data structure to the cache file.
     *
     * @param graph the internal graph array data structure
     * @throws IOException if we cannot write to the cache file
     */
    public void write(final int[][] graph) throws IOException {
        try (final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                new GZIPOutputStream(new FileOutputStream(file)), StandardCharsets.UTF_8), WRITE_BUFFER_SIZE)) {
            for (final int[] node : graph) {
                final StringBuilder stringBuilder = new StringBuilder();

                for (final int value : node) {
                    stringBuilder.append(value).append(NODE_VALUE_SEPARATOR);
                }
                stringBuilder.append(NODE_ARRAY_SEPARATOR);

                bufferedWriter.write(stringBuilder.toString());
            }

            bufferedWriter.flush();
        }
    }

    /**
     * Gets absolute path of cache file.
     *
     * @return absolute path of cache file
     */
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }
}
