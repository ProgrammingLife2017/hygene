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


    public RecentFilesController() {
        this.dataFile = generateFileObject();
    }


    public List<String> getRecentlyOpenedFiles() throws IOException {
        if (!dataFile.exists()) {
            resetRecentlyOpenedFiles();
            return new ArrayList<>();
        }

        final List<String> lines = readLinesFromFile(dataFile);
        return truncateListOfLines(lines);
    }

    public void resetRecentlyOpenedFiles() throws IOException {
        writeToFile(dataFile, "");
    }

    public void addFileToRecentlyOpenedFiles(final String filePath) throws IOException {
        if (!dataFile.exists()) {
            resetRecentlyOpenedFiles();
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

    private List<String> readLinesFromFile(final File file) throws IOException {
        return Files.readAllLines(Paths.get(file.getPath()), Charset.forName(FILE_ENCODING));
    }

    private void writeToFile(final File file, final String contents) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        fileOutputStream.write(contents.getBytes());
        fileOutputStream.close();
    }

    private List<String> truncateListOfLines(final List<String> lines) {
        return lines.subList(0, min(lines.size(), MAX_NUMBER_ENTRIES));
    }

    private File generateFileObject() {
        final String filePath = getAppDataFolderPath() + "/" + DATA_FILE_NAME;
        return new File(filePath);
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
