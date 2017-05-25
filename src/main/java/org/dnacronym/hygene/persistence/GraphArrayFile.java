package org.dnacronym.hygene.persistence;

import org.dnacronym.hygene.parser.ProgressUpdater;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class GraphArrayFile {
    private static final int PROGRESS_UPDATE_INTERVAL = 50000;
    private static final int PROGRESS_TOTAL = 100;

    private static final String NODE_VALUE_SEPARATOR = " ";
    private static final String NODE_ARRAY_SEPARATOR = "\n";

    private File file;

    public GraphArrayFile(final File file) {
        this.file = file;
    }

    public int[][] read(final int graphSize, final ProgressUpdater progressUpdater) throws IOException {
        final int[][] graph = new int[graphSize][];

        final BufferedReader hygeneCache = new BufferedReader(new FileReader(file));

        String node;
        StringTokenizer nodeTokenizer;
        int nodeIndex = 0;
        while ((node = hygeneCache.readLine()) != null) {
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

        return graph;
    }

    public void write(int[][] graph) {
        try (FileWriter writer = new FileWriter(file)) {
            BufferedWriter bufferedWriter = new BufferedWriter(writer, 4 * (int) Math.pow(1024, 2));

            for (final int[] node : graph) {
                final StringBuilder a = new StringBuilder();

                for (final int value : node) {
                    a.append(value).append(NODE_VALUE_SEPARATOR);
                }
                a.append(NODE_ARRAY_SEPARATOR);

                bufferedWriter.write(a.toString());
            }

            bufferedWriter.flush();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }
}
