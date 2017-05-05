package org.dnacronym.hygene.core;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Optional;

/**
 * Provides access to and helper methods for the local filesystem.
 * <p>
 * This class is a thread-safe singleton.
 */
public final class Files {
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
}
