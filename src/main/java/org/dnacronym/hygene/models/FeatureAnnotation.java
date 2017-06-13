package org.dnacronym.hygene.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A {@link FeatureAnnotation} coincides with a single annotation in the graph.
 * <p>
 * A gene annotation consists of {@link SubFeatureAnnotation}s. These {@link SubFeatureAnnotation}s combined represent
 * the annotation.
 *
 * @see <a href="https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md">GFF v3 specification</a>
 * @see org.dnacronym.hygene.parser.GffParser
 */
public final class FeatureAnnotation {
    private final String seqId;

    private final List<SubFeatureAnnotation> subFeatureAnnotations;
    /**
     * Map used for quick access of annotations to build up a hierarchy and quickly retrieve parent nodes.
     */
    private final Map<String, List<SubFeatureAnnotation>> subFeatureAnnotationsMap;
    private final List<String> metadata;


    /**
     * Creates an instance of {@link SubFeatureAnnotation}.
     *
     * @param seqId the id of the genome. Cannot start with an unescaped '>' character
     */
    public FeatureAnnotation(final String seqId) {
        if (seqId.charAt(0) == '>') {
            throw new IllegalArgumentException("Seqid '" + seqId + "' started with the unescaped character '>'.");
        }

        this.seqId = seqId;
        subFeatureAnnotations = new ArrayList<>();
        subFeatureAnnotationsMap = new HashMap<>();
        metadata = new ArrayList<>();
    }


    /**
     * Returns the sequence id of this annotation.
     * <p>
     * The ID of the landmark used to establish the coordinate system for the current feature.
     *
     * @return the sequence id of this annotation
     */
    public String getSequenceId() {
        return seqId;
    }

    /**
     * Adds a list of of meta-data to the {@link FeatureAnnotation}.
     *
     * @param metadata item of meta-data to add
     */
    public void addMetadata(final List<String> metadata) {
        this.metadata.addAll(metadata);
    }

    /**
     * Returns the list of meta-data of this {@link FeatureAnnotation}.
     *
     * @return the list of meta-data of this {@link FeatureAnnotation}
     */
    public List<String> getMetadata() {
        return metadata;
    }

    /**
     * Adds a {@link SubFeatureAnnotation} to this {@link FeatureAnnotation}.
     * <p>
     * This adds to the graph structure of the {@link FeatureAnnotation}, as it is added to the relevant parent. If it
     * has no parent, it is simply stored internally.<br>
     * All {@link SubFeatureAnnotation}s with the same ids are appended to the same list. Each of these list in turn
     * form a transcript.
     * <p>
     * Note that a {@link SubFeatureAnnotation} may have no "ID" attribute. This is acceptable, as long as no subsequent
     * {@link SubFeatureAnnotation}s wish for this {@link SubFeatureAnnotation} to be their parent.
     *
     * @param subFeatureAnnotation the {@link SubFeatureAnnotation} to add to this {@link FeatureAnnotation}
     */
    public void addSubFeatureAnnotation(final SubFeatureAnnotation subFeatureAnnotation) {
        final String[] ids = subFeatureAnnotation.getAttributes().get("ID");
        if (ids != null && ids.length != 1) {
            throw new IllegalArgumentException("The given feature annotation contained more than 1 id, it contained: "
                    + ids.length + ".");
        }

        if (ids != null) {
            if (!subFeatureAnnotationsMap.containsKey(ids[0])) {
                subFeatureAnnotationsMap.put(ids[0], new ArrayList<>());
            }
            subFeatureAnnotationsMap.get(ids[0]).add(subFeatureAnnotation);
        }

        final String[] parentIds = subFeatureAnnotation.getAttributes().get("Parent");
        if (parentIds != null) {
            for (final String parentId : parentIds) {
                if (!subFeatureAnnotationsMap.containsKey(parentId)) {
                    throw new IllegalArgumentException("Reference made to non-existent parent: '" + parentId + "'.");
                }

                final List<SubFeatureAnnotation> annotations = subFeatureAnnotationsMap.get(parentId);
                for (final SubFeatureAnnotation annotation : annotations) {
                    annotation.addChild(subFeatureAnnotation);
                }
            }
        }

        subFeatureAnnotations.add(subFeatureAnnotation);
    }

    /**
     * Returns the {@link SubFeatureAnnotation}s of this {@link FeatureAnnotation}.
     *
     * @return the {@link SubFeatureAnnotation}s of this {@link FeatureAnnotation}
     */
    public List<SubFeatureAnnotation> getSubFeatureAnnotations() {
        return subFeatureAnnotations;
    }
}
