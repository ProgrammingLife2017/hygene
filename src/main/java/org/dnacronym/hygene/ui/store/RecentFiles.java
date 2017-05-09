package org.dnacronym.hygene.ui.store;

import org.dnacronym.hygene.core.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.min;


/**
 * Class for both storing and retrieving recently opened files.
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
    public static synchronized List<File> getAll() throws IOException {
        if (!Files.getInstance().getAppDataFile(DATA_FILE_NAME).exists()) {
            reset();
            return new ArrayList<>();
        }

        final String content = Files.getInstance().getAppData(DATA_FILE_NAME);
        final List<String> lines = new ArrayList<>(Arrays.asList(content.split("\n")));

        // Remove any empty lines from the list of lines
        lines.removeIf(""::equals);

        // Remove paths not ending with the proper extension to restrict effects of manual manipulation of this list
        lines.removeIf(line -> !line.toLowerCase().endsWith("." + GraphStore.GFA_EXTENSION));

        final List<File> files = lines.stream().map(File::new).collect(Collectors.toList());
        return truncate(files);
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
     * Adds the given file as entry to the data file.
     * <p>
     * This element is prepended to the front of the list. The list is truncated to the maximum size - if the list
     * already had the maximal number of entries, the last item will be (permanently) lost.
     *
     * @param file the file to be added
     * @throws IOException if an exception occurs during file IO
     */
    public static synchronized void add(final File file) throws IOException {
        final List<File> files = getAll();
        files.add(0, file);

        final List<String> lines = truncate(files).stream().map(File::getPath).collect(Collectors.toList());
        final String fileContents = String.join("\n", lines);
        Files.getInstance().putAppData(DATA_FILE_NAME, fileContents);
    }


    /**
     * Truncates the given list to a list containing the first MAX_NUMBER_ENTRIES entries of that list.
     * <p>
     * Does not modify the original list.
     *
     * @param fileList the original list
     * @return the truncated list.
     */
    private static List<File> truncate(final List<File> fileList) {
        return fileList.subList(0, min(fileList.size(), MAX_NUMBER_ENTRIES));
    }
}
