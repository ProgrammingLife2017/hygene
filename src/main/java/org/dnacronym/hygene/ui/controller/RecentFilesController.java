package org.dnacronym.hygene.ui.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.min;


/**
 * Controller for both storing and retrieving recently opened files.
 */
public class RecentFilesController {
    private static final String APPLICATION_FOLDER_NAME = "hygene";
    private static final String DATA_FILE_NAME = "recently-opened-files.txt";
    private static final String FILE_ENCODING = "UTF-8";
    private static final int MAX_NUMBER_ENTRIES = 10;

    private final File dataFile;


    /**
     * Constructs a {@code RecentFilesController}.
     * <p>
     * No actual file loading / reading is performed during this construction.
     */
    public RecentFilesController() {
        final String filePath = getAppDataFolderPath() + "/" + DATA_FILE_NAME;
        this.dataFile = new File(filePath);
    }


    /**
     * Reads and returns the list of files stored in the data file.
     *
     * @return the list of files.
     * @throws IOException if an exception occurs during file IO
     */
    public List<String> getFiles() throws IOException {
        if (!dataFile.exists()) {
            resetFileList();
            return new ArrayList<>();
        }

        final List<String> lines = readLinesFromFile(dataFile);
        return truncateListOfLines(lines);
    }

    /**
     * Resets the list of files to an empty list.
     *
     * @throws IOException if an exception occurs during file IO
     */
    public void resetFileList() throws IOException {
        writeToFile(dataFile, "");
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
    public void addFile(final String filePath) throws IOException {
        if (!dataFile.exists()) {
            resetFileList();
        }

        final List<String> lines = readLinesFromFile(dataFile);
        lines.add(0, filePath);
        writeToFile(dataFile, String.join("\n", truncateListOfLines(lines)));
    }

    /**
     * Returns the data file.
     *
     * @return the data file.
     */
    public final File getDataFile() {
        return dataFile;
    }

    /**
     * Reads all lines from the given file to a list of {@code String}s.
     *
     * @param file the file to be read from
     * @return a list of {@code String}s, containing all lines of the file
     * @throws IOException if an exception occurs during file IO
     */
    private List<String> readLinesFromFile(final File file) throws IOException {
        return Files.readAllLines(Paths.get(file.getPath()), Charset.forName(FILE_ENCODING));
    }

    /**
     * Writes the given {@code String} to the file.
     * <p>
     * Overwrites the current content of the file with the new content.
     *
     * @param file    the file to write to
     * @param content the new content to be written
     * @throws IOException if an exception occurs during file IO
     */
    private void writeToFile(final File file, final String content) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        fileOutputStream.write(content.getBytes());
        fileOutputStream.close();
    }

    /**
     * Truncates the given list to a list containing the first MAX_NUMBER_ENTRIES entries of that list.
     * <p>
     * Does not modify the original list.
     *
     * @param lines the original list
     * @return the truncated list.
     */
    private List<String> truncateListOfLines(final List<String> lines) {
        return lines.subList(0, min(lines.size(), MAX_NUMBER_ENTRIES));
    }

    /**
     * Returns the base directory for placing application data.
     *
     * @return the full path to a folder in which to store application data.
     */
    private String getAppDataFolderPath() {
        final String operatingSystemName = (System.getProperty("os.name")).toUpperCase();

        String baseDirectory;
        if (operatingSystemName.contains("WIN")) {
            baseDirectory = System.getenv("AppData");
        } else {
            // Assume OS to be macOS or Linux
            baseDirectory = System.getProperty("user.home");
        }

        return baseDirectory + "/" + APPLICATION_FOLDER_NAME;
    }
}
