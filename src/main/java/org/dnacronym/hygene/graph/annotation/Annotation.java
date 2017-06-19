package org.dnacronym.hygene.graph.annotation;

import java.util.HashMap;
import java.util.Map;


/**
 * Sub features of features.
 * <p>
 * These features are retrieved from GFF files. Each line of the file corresponds with a single sub feature. Together, a
 * collection of {@link Annotation}s form a {@link AnnotationCollection}.
 *
 * @see <a href="https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md">GFF v3 specification</a>
 * @see org.dnacronym.hygene.parser.GffParser
 * @see AnnotationCollection
 */
public final class Annotation {
    private final Map<String, String[]> attributes;

    private final String source;
    private final String type;
    private final int start;
    private final int end;
    private final double score;
    private final String strand;
    private final int phase;


    /**
     * Creates an instance of a {@link Annotation}.
     *
     * @param source the source node
     * @param type   the type
     * @param start  the start of the base offset
     * @param end    the end of the base offset
     * @param score  the score of the feature
     * @param strand the strand of the {@link Annotation}. Must be one of '.', '-' or '+'
     * @param phase  the phase of the {@link Annotation}. 0, 1 or 2, or -1 to indicate it has no value
     */
    public Annotation(final String source, final String type, final int start, final int end, final double score,
                      final String strand, final int phase) {
        this.source = source;
        this.type = type;
        this.start = start;
        this.end = end;
        this.score = score;
        this.strand = strand;
        this.phase = phase;

        attributes = new HashMap<>();
    }


    /**
     * Returns the source of the {@link Annotation}.
     * <p>
     * The source is a free text qualifier intended to describe the algorithm or operating procedure that generated this
     * feature. Typically this is the name of a piece of software, such as "Genescan" or a database name, such as
     * "Genbank." In effect, the source is used to extend the feature ontology by adding a qualifier to the type
     * creating a new composite type that is a subclass of the type in the type column.
     *
     * @return the source of the {@link Annotation}
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns the type of the {@link Annotation}.
     * <p>
     * The type of the feature (previously called the "method"). This is constrained to be either a term from the
     * Sequence Ontology or an SO accession number. The latter alternative is distinguished using the syntax SO:000000.
     * In either case, it must be sequence_feature (SO:0000110) or an is_a child of it.
     *
     * @return the type of the {@link Annotation}
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the start of the {@link Annotation}.
     * <p>
     * The start coordinate of the feature are given in positive 1-based integer coordinates, relative to the
     * landmark given in column one. Start is always less than or equal to end. For features that cross the origin of a
     * circular feature (e.g. most bacterial genomes, plasmids, and some viral genomes), the requirement for start to be
     * less than or equal to end is satisfied by making end = the position of the end + the length of the landmark
     * feature.
     * <p>
     * For zero-length features, such as insertion sites, start equals end and the implied site is to the right of the
     * indicated base in the direction of the landmark.
     *
     * @return the start of the {@link Annotation}
     * @see AnnotationCollection#getSequenceId()
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the end of the {@link Annotation}.
     * <p>
     * The end coordinate of the feature are given in positive 1-based integer coordinates, relative to the
     * landmark given in column one. Start is always less than or equal to end. For features that cross the origin of a
     * circular feature (e.g. most bacterial genomes, plasmids, and some viral genomes), the requirement for start to be
     * less than or equal to end is satisfied by making end = the position of the end + the length of the landmark
     * feature.
     * <p>
     * For zero-length features, such as insertion sites, start equals end and the implied site is to the right of the
     * indicated base in the direction of the landmark.
     *
     * @return the end of the {@link Annotation}
     * @see AnnotationCollection#getSequenceId()
     */
    public int getEnd() {
        return end;
    }

    /**
     * Returns the score of the {@link Annotation}.
     * <p>
     * The score of the feature, a floating point number. As in earlier versions of the format, the semantics of the
     * score are ill-defined. It is strongly recommended that E-values be used for sequence similarity features, and
     * that P-values be used for ab initio gene prediction features.
     *
     * @return the score of the {@link Annotation}
     */
    public double getScore() {
        return score;
    }

    /**
     * Returns the strand of the {@link Annotation}.
     * <p>
     * The strand of the feature. + for positive strand (relative to the landmark), - for minus strand, and . for
     * features that are not stranded. In addition, ? can be used for features whose strandedness is relevant, but
     * unknown.
     *
     * @return the strand of the {@link Annotation}
     */
    public String getStrand() {
        return strand;
    }

    /**
     * Returns the phase of the {@link Annotation}.
     * <p>
     * For features of type "CDS", the phase indicates where the feature begins with reference to the reading frame. The
     * phase is one of the integers 0, 1, or 2, indicating the number of bases that should be removed from the beginning
     * of this feature to reach the first base of the next codon. In other words, a phase of "0" indicates that the next
     * codon begins at the first base of the region described by the current line, a phase of "1" indicates that the
     * next codon begins at the second base of this region, and a phase of "2" indicates that the codon begins at the
     * third base of this region. This is NOT to be confused with the frame, which is simply start modulo 3.
     * <p>
     * For forward strand features, phase is counted from the start field. For reverse strand features, phase is counted
     * from the end field.
     * <p>
     * The phase is REQUIRED for all CDS features.
     *
     * @return the phase of the {@link Annotation}
     */
    public int getPhase() {
        return phase;
    }

    /**
     * Sets an attribute of the annotation.
     * <p>
     * Values must be separated by a comma.
     *
     * @param name   the name of the attribute
     * @param values the value of the attribute
     */
    public void setAttribute(final String name, final String[] values) {
        attributes.put(name, values);
    }

    /**
     * Returns the attributes of this {@link Annotation}.
     *
     * @return the attributes of the {@link Annotation}
     */
    public Map<String, String[]> getAttributes() {
        return attributes;
    }
}
