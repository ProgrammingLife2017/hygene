package org.dnacronym.insertproductname.ui.store;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dnacronym.insertproductname.models.SequenceGraph;
import org.dnacronym.insertproductname.parser.GFAFile;
import org.dnacronym.insertproductname.parser.ParseException;

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
     * Load a {@link SequenceGraph} into memory.
     *
     * @param file {@link File} to load. This should be a {@value GFA_EXTENSION} file.
     * @throws IOException if unable to get the GFA file, file is not a gfa file,
     *                     or unable to parse the file.
     * @see GFAFile#read(String)
     * @see GFAFile#parse()
     */
    public final void load(@NonNull final File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("File does not exist.");
        }

        String fileName = file.getName();
        if (!fileName.substring(fileName.lastIndexOf('.') + 1).equals(GFA_EXTENSION)) {
            throw new IOException(fileName + " is not a GFA file.");
        }

        try {
            final SequenceGraph sequenceGraph = GFAFile.read(file.getAbsolutePath()).parse();
            this.sequenceGraphProperty.set(sequenceGraph);
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
