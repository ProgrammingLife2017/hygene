package org.dnacronym.hygene.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A gene annotation is an object representing a single GFF file.
 * <p>
 * A gene annotation consists of {@link SubFeatureAnnotation}s, which form a graph representing the gene.
 *
 * @see org.dnacronym.hygene.parser.GffParser
 */
public final class FeatureAnnotation {
    private final String seqId;

    private final Map<String, SubFeatureAnnotation> featureAnnotations;
    private final List<String> metaData;


    /**
     * Creates an instance of {@link SubFeatureAnnotation}.
     *
     * @param seqId the id of the genome. If it starts with an unescaped '>' character an
     *              {@link IllegalArgumentException} is thrown
     */
    public FeatureAnnotation(final String seqId) {
        if (seqId.charAt(0) == '>') {
            throw new IllegalArgumentException("Seqid " + seqId + " started with the unescaped character '>'.");
        }

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
     * Adds a single item of meta-data to the {@link FeatureAnnotation}.
     *
     * @param metaData item of meta-data to add
     */
    public void addMetaData(final String metaData) {
        this.metaData.add(metaData);
    }

    /**
     * Returns the list of meta-data of this {@link FeatureAnnotation}.
     *
     * @return the list of meta-data of this {@link FeatureAnnotation}
     */
    public List<String> getMetaData() {
        return metaData;
    }

    /**
     * Adds a {@link SubFeatureAnnotation} to this {@link FeatureAnnotation}.
     * <p>
     * This adds to the graph structure of the {@link FeatureAnnotation}, as it is appended to the relevant parent. If
     * it has no parent, it is simply stored internally.
     *
     * @param subFeatureAnnotation the {@link SubFeatureAnnotation} to add to this {@link FeatureAnnotation}. Must have an id
     */
    public void addFeatureAnnotation(final SubFeatureAnnotation subFeatureAnnotation) {
        final String[] id = subFeatureAnnotation.getAttributes().get("ID");
        if (id == null) {
            throw new IllegalArgumentException("The given feature annotation did not contain an id.");
        }
        if (id.length > 1) {
            throw new IllegalArgumentException("The given feature annotation contained more than 1 id, it contained: "
                    + id.length);
        }

        featureAnnotations.put(id[0], subFeatureAnnotation);

        final String[] parentIds = subFeatureAnnotation.getAttributes().get("Parent");
        if (parentIds != null) {
            for (final String parentId : parentIds) {
                if (featureAnnotations.containsKey(parentId)) {
                    featureAnnotations.get(parentId).addChild(subFeatureAnnotation);
                }
            }
        }
    }

    /**
     * Returns the {@link SubFeatureAnnotation}s of this {@link FeatureAnnotation}.
     *
     * @return the {@link SubFeatureAnnotation}s of this {@link FeatureAnnotation}
     */
    public List<SubFeatureAnnotation> getFeatureAnnotations() {
        return new ArrayList<>(featureAnnotations.values());
    }
}
