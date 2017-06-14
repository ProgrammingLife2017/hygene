package org.dnacronym.hygene.ui.graph;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.GffFile;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.parser.ProgressUpdater;
import org.dnacronym.hygene.ui.runnable.Hygene;
import org.dnacronym.hygene.ui.runnable.UIInitialisationException;

import java.io.File;
import java.io.IOException;


/**
 * Deals with storing the {@link GfaFile} (with the graph contained) in memory.
 * <p>
 * The {@link GfaFile} is stored in an {@link ObjectProperty}, allowing easy access.
 */
public final class GraphStore {
    public static final String GFA_FILE_EXTENSION = "gfa";
    public static final String GFA_FILE_NAME = "GFA";
    public static final String GFF_FILE_EXTENSION = "gff";
    public static final String GFF_FILE_NAME = "GFF";

    private final ObjectProperty<GfaFile> gfaFileProperty = new SimpleObjectProperty<>();

    private final ListProperty<GffFile> gffFiles = new SimpleListProperty<>();
    private final ReadOnlyListWrapper<GffFile> readOnlyGffFiles = new ReadOnlyListWrapper<>(gffFiles);


    /**
     * Loads a sequence graph into memory.
     *
     * @param file            {@link File} to load. This should be a {@value GFA_FILE_EXTENSION} file
     * @param progressUpdater a {@link ProgressUpdater} to notify interested parties on progress updates
     * @throws IOException if unable to get the GFA file, file is not a gfa file, or unable to parse the file
     * @see GfaFile#parse(ProgressUpdater)
     */
    public void loadGfaFile(@NonNull final File file, final ProgressUpdater progressUpdater) throws IOException {
        try {
            final GfaFile gfaFile = new GfaFile(file.getAbsolutePath());
            gfaFile.parse(progressUpdater);

            Platform.runLater(() -> gfaFileProperty.set(gfaFile));

            Hygene.getInstance().formatTitle(file.getPath());
        } catch (final ParseException | UIInitialisationException e) {
            throw new IOException(e);
        }
    }

    /**
     * Loads a {@link org.dnacronym.hygene.models.FeatureAnnotation} file into memory.
     *
     * @param file {@link File} to load. This should be a {@value GFF_FILE_EXTENSION} file
     * @throws IOException if unable to get the GFA file, file is not a gfa file, or unable to parse the file
     * @see GfaFile#parse(ProgressUpdater)
     */
    public void loadGffFile(@NonNull final File file, final ProgressUpdater progressUpdater) throws IOException {
        try {
            final GffFile gffFile = new GffFile(file.getAbsolutePath());
            gffFile.parse(progressUpdater);

            Platform.runLater(() -> gffFiles.add(gffFile));
        } catch (final ParseException e) {
            throw new IOException(e);
        }
    }

    /**
     * Gets the {@link ObjectProperty} that stores the {@link GfaFile}.
     *
     * @return the {@link ObjectProperty} that stores the {@link GfaFile}
     */
    public ObjectProperty<GfaFile> getGfaFileProperty() {
        return gfaFileProperty;
    }

    /**
     * Gets the {@link ReadOnlyListWrapper} that stores all the {@link GffFile}s loaded into memory.
     *
     * @return the {@link ReadOnlyListWrapper} that stores all the {@link GffFile}s loaded into memory
     */
    public ReadOnlyListWrapper<GffFile> getGffFiles() {
        return readOnlyGffFiles;
    }
}
