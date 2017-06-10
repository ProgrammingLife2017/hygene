package org.dnacronym.hygene.ui.recent;

import org.dnacronym.hygene.core.Files;

import java.io.File;
import java.io.IOException;


/**
 * Class responsible for storing and retrieving the recently opened directory (in the {@link javafx.stage.FileChooser}).
 */
public final class RecentDirectory {
    static final String DATA_FILE_NAME = "-recently-opened-directory.txt";


    /**
     * Prevent instantiation of this class.
     */
    private RecentDirectory() {
    }


    /**
     * Reads and returns the most recently opened directory.
     * <p>
     * Returns the home directory if no data file was found.
     *
     * @param type the file type used to distinguish recent directories of different file types
     * @return if the data file exists, the most recently opened directory, else the home directory
     * @throws IOException if an exception occurs during file IO
     */
    public static synchronized File get(final String type) throws IOException {
        if (!Files.getInstance().getAppDataFile(type + DATA_FILE_NAME).exists()) {
            return new File(System.getProperty("user.home"));
        }

        final String content = Files.getInstance().getAppData(type + DATA_FILE_NAME);

        return new File(content.trim());
    }

    /**
     * Stores the given directory in the data file.
     *
     * @param type the file type used to distinguish recent directories of different file types
     * @param directory the directory to be stored
     * @throws IOException if an exception occurs during file IO
     */
    public static synchronized void store(final String type, final File directory) throws IOException {
        Files.getInstance().putAppData(type + DATA_FILE_NAME, directory.getAbsolutePath());
    }
}
