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
    public static final String GFA_EXTENSION = ".gfa";

    private final ObjectProperty<SequenceGraph> sequenceGraph = new SimpleObjectProperty<>();

    public final void load(File file) throws IOException, ParseException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist.");
        }

        String fileName = file.getName();
        if (fileName.substring(fileName.lastIndexOf(".") + 1).equals(GFA_EXTENSION)) {
            throw new IllegalArgumentException(fileName + " is not a GFA file.");
        }

        final SequenceGraph sequenceGraph = GFAFile.read(file.getAbsolutePath()).parse();
        this.sequenceGraph.set(sequenceGraph);
    }

    public ObjectProperty<SequenceGraph> sequenceGraphProperty() {
        return sequenceGraph;
    }
}
