package org.dnacronym.hygene.graph.annotation;

import org.dnacronym.hygene.ui.graph.ColorRoulette;

import java.util.ArrayList;
import java.util.List;


/**
 * An {@link AnnotationCollection} coincides with a complete GFF file.
 * <p>
 * A GFF file consists of {@link Annotation}s.
 *
 * @see <a href="https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md">GFF v3 specification</a>
 * @see org.dnacronym.hygene.parser.GffParser
 */
public final class AnnotationCollection {
    private final String sequenceId;
    private final List<Annotation> annotations;
    private final List<String> metadata;
    private final ColorRoulette colorRoulette;


    /**
     * Creates a new {@link Annotation}.
     *
     * @param sequenceId the id of the genome. Cannot start with an unescaped '>' character
     */
    public AnnotationCollection(final String sequenceId) {
        this.sequenceId = sequenceId;
        this.colorRoulette = new ColorRoulette();

        this.annotations = new ArrayList<>();
        this.metadata = new ArrayList<>();
    }


    /**
     * Returns the sequence id of this annotation.
     * <p>
     * The ID of the landmark used to establish the coordinate system for the current feature.
     *
     * @return the sequence id of this annotation
     */
    public String getSequenceId() {
        return sequenceId;
    }

    /**
     * Adds a list of of metadata to the {@link AnnotationCollection}.
     *
     * @param metadata item of metadata to add
     */
    public void addMetadata(final List<String> metadata) {
        this.metadata.addAll(metadata);
    }

    /**
     * Returns the list of metadata of this {@link AnnotationCollection}.
     *
     * @return the list of metadata of this {@link AnnotationCollection}
     */
    public List<String> getMetadata() {
        return metadata;
    }

    /**
     * Adds a {@link Annotation} to this {@link AnnotationCollection}.
     *
     * @param annotation the {@link Annotation} to add to this {@link AnnotationCollection}
     */
    public void addAnnotation(final Annotation annotation) {
        annotation.setColor(colorRoulette.getNextLightColor());
        annotations.add(annotation);
    }

    /**
     * Returns the {@link Annotation}s of this {@link AnnotationCollection}.
     *
     * @return the {@link Annotation}s of this {@link AnnotationCollection}
     */
    public List<Annotation> getAnnotations() {
        return annotations;
    }
}
