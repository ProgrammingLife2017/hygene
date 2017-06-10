package org.dnacronym.hygene.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A gene annotation is an object representing a single GFF file.
 * <p>
 * A gene annotation consists of {@link FeatureAnnotation}s, which form a graph representing the gene.
 *
 * @see org.dnacronym.hygene.parser.GffParser
 */
public final class SequenceAnnotation {
    private final String seqId;

    private final Map<String, FeatureAnnotation> featureAnnotations;
    private final List<String> metaData;


    /**
     * Creates an instance of {@link FeatureAnnotation}.
     *
     * @param seqId the id of the genome
     */
    public SequenceAnnotation(final String seqId) {
        this.seqId = seqId;
        featureAnnotations = new HashMap<>();
        metaData = new ArrayList<>();
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
     * Adds a single item of meta-data to the {@link SequenceAnnotation}
     *
     * @param metaData item of meta-data to add
     */
    public void addMetaData(final String metaData) {
        this.metaData.add(metaData);
    }

    /**
     * Returns the list of meta-data of this {@link SequenceAnnotation}.
     *
     * @return the list of meta-data of this {@link SequenceAnnotation}
     */
    public List<String> getMetaData() {
        return metaData;
    }

    /**
     * Adds a {@link FeatureAnnotation} to this {@link SequenceAnnotation}.
     * <p>
     * This adds to the graph structure of the {@link SequenceAnnotation}, as it is appended to the relevant parent. If it
     * has no parent, it is simply stored internally.
     *
     * @param featureAnnotation the {@link FeatureAnnotation} to add to this {@link SequenceAnnotation}. Must have an id
     */
    public void addFeatureAnnotation(final FeatureAnnotation featureAnnotation) {
        final String[] id = featureAnnotation.getAttributes().get("ID");
        if (id == null) {
            throw new IllegalArgumentException("The given feature annotation did not contain an id.");
        }
        if (id.length > 1) {
            throw new IllegalArgumentException("The given feature annotation contained more than 1 id, it contained: "
                    + id.length);
        }

        featureAnnotations.put(id[0], featureAnnotation);

        final String[] parentIds = featureAnnotation.getAttributes().get("Parent");
        if (parentIds != null) {
            for (final String parentId : parentIds) {
                if (featureAnnotations.containsKey(parentId)) {
                    featureAnnotations.get(parentId).addChild(featureAnnotation);
                }
            }
        }
    }
}
