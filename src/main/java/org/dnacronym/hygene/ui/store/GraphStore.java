package org.dnacronym.hygene.ui.store;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.ParseException;

import java.io.File;
import java.io.IOException;


/**
 * Deals with storing the {@link GfaFile} (with the graph contained) in memory.
 * <p>
 * The {@link GfaFile} is stored in an {@link ObjectProperty}, allowing easy access.
 */
public class GraphStore {
    public static final String GFA_EXTENSION = "gfa";

    private final ObjectProperty<GfaFile> gfaFileProperty = new SimpleObjectProperty<>();

    /**
     * Load a sequence graph into memory.
     *
     * @param file {@link File} to load. This should be a {@value GFA_EXTENSION} file.
     * @throws IOException if unable to get the GFA file, file is not a gfa file,
     *                     or unable to parse the file.
     * @see GfaFile#parse()
     */
    public final void load(@NonNull final File file) throws IOException {
        try {
            final GfaFile gfaFile = new GfaFile(file.getAbsolutePath());
            gfaFile.parse();

            gfaFileProperty.set(gfaFile);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    /**
     * Get the {@link ObjectProperty} that stores the {@link GfaFile}.
     *
     * @return {@link ObjectProperty} that stores the {@link GfaFile}.
     */
    public final ObjectProperty<GfaFile> getGfaFileProperty() {
        return gfaFileProperty;
    }
}
