package org.dnacronym.insertproductname.ui.store;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    private final ObjectProperty<SequenceGraph> sequenceGraph = new SimpleObjectProperty<>();

    /**
     * Load a {@link SequenceGraph} into memory.
     *
     * @param file {@link File} to load. This should be a {@value GFA_EXTENSION} file.
     * @throws IllegalArgumentException if file is null, doesn't exist or doesn't end with {@value GFA_EXTENSION}.
     * @throws IOException              if unable to get the file
     * @throws ParseException           if unable to parse the GFA file.
     * @see GFAFile#read(String)
     * @see GFAFile#parse()
     */
    public final void load(final File file) throws IllegalArgumentException, IOException, ParseException {
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist.");
        }

        String fileName = file.getName();
        if (!fileName.substring(fileName.lastIndexOf(".") + 1).equals(GFA_EXTENSION)) {
            throw new IllegalArgumentException(fileName + " is not a GFA file.");
        }

        final SequenceGraph sequenceGraph = GFAFile.read(file.getAbsolutePath()).parse();
        this.sequenceGraph.set(sequenceGraph);
    }

    /**
     * Get the {@link ObjectProperty} that stores the {@link SequenceGraph}.
     *
     * @return {@link ObjectProperty} that stores the {@link SequenceGraph}.
     */
    public final ObjectProperty<SequenceGraph> sequenceGraphProperty() {
        return sequenceGraph;
    }
}
