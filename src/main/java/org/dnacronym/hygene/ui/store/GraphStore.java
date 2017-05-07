package org.dnacronym.hygene.ui.store;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dnacronym.hygene.models.SequenceGraph;
import org.dnacronym.hygene.parser.GfaFile;
import org.dnacronym.hygene.parser.ParseException;

import java.io.File;
import java.io.IOException;


/**
 * Deals with storing the {@link SequenceGraph} in memory.
 * <p>
 * The {@link SequenceGraph} is stored in an {@link ObjectProperty}, allowing easy access.
 */
public class GraphStore {
    public static final String GFA_EXTENSION = "gfa";

    private final ObjectProperty<SequenceGraph> sequenceGraphProperty = new SimpleObjectProperty<>();

    /**
     * Load a {@code SequenceGraph} into memory.
     *
     * @param file {@link File} to load. This should be a {@value GFA_EXTENSION} file.
     * @throws IOException if unable to get the GFA file, file is not a gfa file,
     *                     or unable to parse the file.
     * @see GfaFile#read(String)
     * @see GfaFile#parse()
     */
    public final void load(@NonNull final File file) throws IOException {
        try {
            sequenceGraphProperty.set(GfaFile.read(file.getAbsolutePath()).parse());
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    /**
     * Get the {@link ObjectProperty} that stores the {@link SequenceGraph}.
     *
     * @return {@link ObjectProperty} that stores the {@link SequenceGraph}.
     */
    public final ObjectProperty<SequenceGraph> getSequenceGraphProperty() {
        return sequenceGraphProperty;
    }
}
