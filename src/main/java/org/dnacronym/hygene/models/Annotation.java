package org.dnacronym.hygene.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Annotation for genes.
 * <p>
 * These genes are retrieved from GFF files.
 *
 * @see <a href="https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md">GFF v3 specification</a>
 * @see org.dnacronym.hygene.parser.GffParser
 */
@SuppressWarnings("URF_UNREAD_FIELD") // Temporary
public final class Annotation {
    private static final Set<String> VALID_ATTRIBUTES = new HashSet<>(Arrays.asList(
            "ID", "Name", "Alias", "Parent", "Target", "Gap", "Derives_from", "Note", "Dbxref", "Ontology_term",
            "Is_circular"));

    private final String seqId;

    private final Map<String, String> attributes;


    /**
     * Creates an instance of {@link Annotation}.
     *
     * @param seqId the id of the genome
     */
    public Annotation(final String seqId) {
        this.seqId = seqId;

        attributes = new HashMap<>();
    }


    /**
     * Returns the sequence id of this annotation.
     *
     * @return the sequence id of this annotation
     */
    public String getSeqId() {
        return seqId;
    }

    /**
     * Sets an attribute of the annotation.
     *
     * @param name  the name of the attribute
     * @param value the value of the attribute
     */
    public void setAttribute(final String name, final String value) {
        if (!VALID_ATTRIBUTES.contains(name)) {
            throw new IllegalArgumentException("The tag: " + name + " is not a valid attribute tag. Must be one of: "
                    + VALID_ATTRIBUTES.toString());
        }

        attributes.put(name, value);
    }

    /**
     * Returns the attributes of this annotation.
     *
     * @return the attributes of the annotation
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }
}
