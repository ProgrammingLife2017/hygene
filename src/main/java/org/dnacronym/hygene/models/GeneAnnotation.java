package org.dnacronym.hygene.models;

import java.util.HashMap;
import java.util.Map;


/**
 * A gene annotation is an object representing a single GFF file.
 * <p>
 * A gene annotation consists of {@link FeatureAnnotation}s, which form a graph representing the gene.
 *
 * @see org.dnacronym.hygene.parser.GffParser
 */
public final class GeneAnnotation {
    private final String seqId;

    private final Map<String, FeatureAnnotation> featureAnnotations;


    /**
     * Creates an instance of {@link FeatureAnnotation}.
     *
     * @param seqId the id of the genome
     */
    public GeneAnnotation(final String seqId) {
        this.seqId = seqId;
        featureAnnotations = new HashMap<>();
    }


    /**
     * Returns the sequence id of this annotation.
     * <p>
     * The ID of the landmark used to establish the coordinate system for the current feature. IDs may contain any
     * characters, but must escape any characters not in the set [a-zA-Z0-9.:^*$@!+_?-|]. In particular, IDs may not
     * contain unescaped whitespace and must not begin with an unescaped ">".
     *
     * @return the sequence id of this annotation
     */
    public String getSeqId() {
        return seqId;
    }

    /**
     * Adds a {@link FeatureAnnotation} to this {@link GeneAnnotation}.
     * <p>
     * This adds to the graph structure of the {@link GeneAnnotation}, as it is appended to the relevant parent. If it
     * has no parent, it is simply stored internally.
     *
     * @param featureAnnotation the {@link FeatureAnnotation} to add to this {@link GeneAnnotation}. Must have an id
     */
    public void addFeatureAnnotation(final FeatureAnnotation featureAnnotation) {
        final String id = featureAnnotation.getAttributes().get("ID");
        if (id == null) {
            throw new IllegalArgumentException("The given feature annotation did not contain an id.");
        }

        featureAnnotations.put(id, featureAnnotation);

        final String parentId = featureAnnotation.getAttributes().get("Parent");
        if (parentId != null && featureAnnotations.containsKey(parentId)) {
            featureAnnotations.get(parentId).addChild(featureAnnotation);
        }
    }
}
