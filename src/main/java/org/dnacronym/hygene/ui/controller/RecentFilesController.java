package org.dnacronym.hygene.ui.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public void resetRecentlyOpenedFiles() throws IOException {
        final File dataFile = getDataFile();
        createFoldersOnPath(dataFile);
        writeToFile(dataFile, "");
    }

    public void addFileToRecentlyOpenedFiles(final String filePath) throws IOException {
        final File dataFile = getDataFile();
        final List<String> lines = readLinesFromFile(dataFile);
        lines.add(0, filePath);

        final List<String> truncatedListOfLines = lines.subList(0, min(lines.size(), MAX_NUMBER_ENTRIES));
        writeToFile(dataFile, String.join("\n", truncatedListOfLines));
    }

    private List<String> readLinesFromFile(final File file) throws IOException {
        return Files.readAllLines(Paths.get(file.getPath()), Charset.forName(FILE_ENCODING));
    }

    private void writeToFile(final File file, final String contents) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        fileOutputStream.write(contents.getBytes());
        fileOutputStream.close();
    }

    private void createFoldersOnPath(final File file) throws IOException {
        if (!file.getParentFile().mkdirs()) {
            throw new IOException("Failed to create parent folders of file");
        }
    }

    private File getDataFile() {
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
