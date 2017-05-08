package org.dnacronym.hygene.core;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Optional;


/**
 * Provides access to and helper methods for the local filesystem.
 * <p>
 * This class is a thread-safe singleton.
 */
public final class Files {
    private static final Charset FILE_ENCODING = StandardCharsets.UTF_8;
    private static final String APPLICATION_FOLDER_NAME = "hygene";
    private static volatile @MonotonicNonNull Files instance;


    /**
     * Makes class non instantiable.
     */
    private Files() {
    }


    /**
     * Gets the instance of {@code Files}.
     *
     * @return the instance of {@code Files}
     */
    public static Files getInstance() {
        if (instance == null) {
            synchronized (Files.class) {
                if (instance == null) {
                    instance = new Files();
                }
            }
        }
        return instance;
    }

    /**
     * Gets the resource url (in file:// format) of application resources.
     *
     * @param fileName name of the file
     * @return resource url (in file:// format) to file
     * @throws FileNotFoundException if the given file name does not exist
     */
    public URL getResourceUrl(final String fileName) throws FileNotFoundException {
        return Optional.ofNullable(getClass().getResource(fileName)).orElseThrow(
                () -> new FileNotFoundException("File " + fileName + " not found in resources folder")
        );
    }

    /**
     * Returns the contents of the given application data file.
     * <p>
     * Returns an empty string if no file with the given name is found.
     *
     * @param fileName the name of the data file to be read
     * @return the contents of that file.
     * @throws IOException if an exception occurs during file IO
     */
    public String getAppData(final String fileName) throws IOException {
        final File file = getAppDataFile(fileName);

        if (!file.exists()) {
            return "";
        } else {
            return readFile(file);
        }
    }

    /**
     * Writes the given {@code String} to the file with given {@code fileName}.
     * <p>
     * Overwrites the current content of the file with the new content.
     *
     * @param fileName the name of the file to write to
     * @param content  the new content to be written
     * @throws IOException if an exception occurs during file IO
     */
    public void putAppData(final String fileName, final String content) throws IOException {
        final File file = getAppDataFile(fileName);
        final File parent = file.getParentFile();

        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create directories on path of file");
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file, false)) {
            fileOutputStream.write(content.getBytes(FILE_ENCODING));
        }
    }

    /**
     * Returns a {@code File} instance for the given data file name.
     *
     * @param fileName the file name
     * @return the application data {@code File} object.
     */
    public File getAppDataFile(final String fileName) {
        final String operatingSystemName = (System.getProperty("os.name")).toUpperCase();

        final String baseDirectory;
        if (operatingSystemName.contains("WIN")) {
            baseDirectory = System.getenv("AppData");
        } else {
            // Assume OS to be macOS or Linux
            baseDirectory = System.getProperty("user.home");
        }

        return new File(baseDirectory + "/" + APPLICATION_FOLDER_NAME, fileName);
    }


    /**
     * Reads the given file and saves its contents to a {@code String}s.
     *
     * @param file the file to be read from
     * @return a {@code String}s, representing the contents of the file.
     * @throws IOException if an exception occurs during file IO
     */
    private String readFile(final File file) throws IOException {
        final byte[] rawContents = java.nio.file.Files.readAllBytes(Paths.get(file.getPath()));
        return new String(rawContents, FILE_ENCODING);
    }
}
