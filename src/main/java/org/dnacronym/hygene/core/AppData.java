package org.dnacronym.hygene.core;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Provides access to the application data on the local filesystem.
 * <p>
 * This class is a thread-safe singleton.
 */
public final class AppData {
    private static final Charset FILE_ENCODING = StandardCharsets.UTF_8;
    private static final String APPLICATION_FOLDER_NAME = "hygene";
    private static AppData instance = new AppData();


    /**
     * Makes class non instantiable.
     */
    private AppData() {
    }


    /**
     * Gets the instance of {@link AppData}.
     *
     * @return the instance of {@link AppData}
     */
    public static AppData getInstance() {
        return instance;
    }

    /**
     * Returns the contents of the given application data file.
     * <p>
     * Returns an empty string if no file with the given name is found.
     *
     * @param fileName the name of the data file to be read
     * @return the contents of that file
     * @throws IOException if an exception occurs during file IO
     */
    public String read(final String fileName) throws IOException {
        final File file = getFile(fileName);
        return readFile(file);
    }

    /**
     * Writes the given {@link String} to the file with given {@code fileName}.
     * <p>
     * Overwrites the current content of the file with the new content.
     *
     * @param fileName the name of the file to write to
     * @param content  the new content to be written
     * @throws IOException if an exception occurs during file IO
     */
    public void put(final String fileName, final String content) throws IOException {
        final File file = getFile(fileName);
        final File parent = file.getParentFile();

        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create directories on path of file");
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file, false)) {
            fileOutputStream.write(content.getBytes(FILE_ENCODING));
        }
    }

    /**
     * Returns a {@link File} instance for the given data file name.
     *
     * @param fileName the file name
     * @return the application data {@link File} object
     */
    public File getFile(final String fileName) {
        String baseDirectory = System.getProperty("user.home");

        // Use the AppData directory if on Windows
        if (SystemUtils.IS_OS_WINDOWS) {
            baseDirectory = System.getenv("AppData");
        }

        return new File(String.format("%s/%s", baseDirectory, APPLICATION_FOLDER_NAME), fileName);
    }

    /**
     * Reads the given file and saves its contents to a {@link String}s.
     *
     * @param file the file to be read from
     * @return a {@link String}s, representing the contents of the file
     * @throws IOException if an exception occurs during file IO
     */
    private String readFile(final File file) throws IOException {
        final byte[] rawContents = Files.readAllBytes(Paths.get(file.getPath()));
        return new String(rawContents, FILE_ENCODING);
    }
}
