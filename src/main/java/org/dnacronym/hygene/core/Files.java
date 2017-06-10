package org.dnacronym.hygene.core;

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
    private static Files instance = new Files();


    /**
     * Makes class non instantiable.
     */
    private Files() {
    }


    /**
     * Gets the instance of {@link Files}.
     *
     * @return the instance of {@link Files}
     */
    public static Files getInstance() {
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
     * @return the contents of that file
     * @throws IOException if an exception occurs during file IO
     */
    public String getAppData(final String fileName) throws IOException {
        final File file = getAppDataFile(fileName);
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
     * Returns a {@link File} instance for the given data file name.
     *
     * @param fileName the file name
     * @return the application data {@link File} object
     */
    public File getAppDataFile(final String fileName) {
        final String operatingSystemName = System.getProperty("os.name").toUpperCase();

        String baseDirectory = System.getProperty("user.home");

        // Use the AppData directory if on Windows
        if (operatingSystemName.contains("WIN")) {
            baseDirectory = System.getenv("AppData");
        }

        return new File(String.format("%s/%s", baseDirectory, APPLICATION_FOLDER_NAME), fileName);
    }

    /**
     * Returns a {@link File} instance for the given data file name.
     *
     * @param prefix the prefix of the temporary filename
     * @return the application data {@link File} object
     * @throws IOException if the temporary file cannot be created
     */
    public File getTemporaryFile(final String prefix) throws IOException {
        return File.createTempFile(prefix, ".tmp");
    }

    /**
     * Reads the given file and saves its contents to a {@link String}s.
     *
     * @param file the file to be read from
     * @return a {@link String}s, representing the contents of the file
     * @throws IOException if an exception occurs during file IO
     */
    private String readFile(final File file) throws IOException {
        final byte[] rawContents = java.nio.file.Files.readAllBytes(Paths.get(file.getPath()));
        return new String(rawContents, FILE_ENCODING);
    }
}
