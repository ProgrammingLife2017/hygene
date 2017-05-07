package org.dnacronym.hygene.ui.controller;

import org.dnacronym.hygene.core.Files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.Integer.min;


/**
 * Controller for both storing and retrieving recently opened files.
 */
public final class RecentFiles {
    static final String DATA_FILE_NAME = "recently-opened-files.txt";

    private static final int MAX_NUMBER_ENTRIES = 10;


    /**
     * Prevent instantiation of this class.
     */
    private RecentFiles() {
    }


    /**
     * Reads and returns the list of files stored in the data file.
     *
     * @return the list of files.
     * @throws IOException if an exception occurs during file IO
     */
    public static synchronized List<String> getAll() throws IOException {
        if (!Files.getInstance().getAppDataFile(DATA_FILE_NAME).exists()) {
            reset();
            return new ArrayList<>();
        }

        final String content = Files.getInstance().getAppData(DATA_FILE_NAME);
        final List<String> lines = new ArrayList<>(Arrays.asList(content.split("\n")));

        // Remove any empty lines from the list of lines
        lines.removeAll(Collections.singletonList(""));

        return truncateListOfLines(lines);
    }

    /**
     * Resets the list of files to an empty list.
     *
     * @throws IOException if an exception occurs during file IO
     */
    public static synchronized void reset() throws IOException {
        Files.getInstance().putAppData(DATA_FILE_NAME, "");
    }

    /**
     * Adds the given file path as entry to the data file.
     * <p>
     * This element is prepended to the front of the list. The list is truncated to the maximum size - if the list
     * already had the maximal number of entries, the last item will be (permanently) lost.
     *
     * @param filePath the file path to be added
     * @throws IOException if an exception occurs during file IO
     */
    public static synchronized void add(final String filePath) throws IOException {
        final List<String> lines = getAll();
        lines.add(0, filePath);

        final String fileContents = String.join("\n", truncateListOfLines(lines));
        Files.getInstance().putAppData(DATA_FILE_NAME, String.join("\n", fileContents));
    }


    /**
     * Truncates the given list to a list containing the first MAX_NUMBER_ENTRIES entries of that list.
     * <p>
     * Does not modify the original list.
     *
     * @param lines the original list
     * @return the truncated list.
     */
    private static List<String> truncateListOfLines(final List<String> lines) {
        return lines.subList(0, min(lines.size(), MAX_NUMBER_ENTRIES));
    }
}
