package org.dnacronym.hygene.models;

import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * sub features of features.
 * <p>
 * These features are retrieved from GFF files. Each line of the file corresponds with a single sub feature. Together, a
 * collection of {@link SubFeatureAnnotation}s form a {@link FeatureAnnotation}.
 *
 * @see <a href="https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md">GFF v3 specification</a>
 * @see org.dnacronym.hygene.parser.GffParser
 * @see FeatureAnnotation
 */
public final class SubFeatureAnnotation {
    /**
     * Set of valid attributes of a single feature annotation.
     */
    public static final ImmutableSet<String> VALID_ATTRIBUTES = ImmutableSet.copyOf(Arrays.asList(
            "ID", "Name", "Alias", "Parent", "Target", "Gap", "Derives_from", "Note", "Dbxref", "Ontology_term",
            "Is_circular"));

    private final Map<String, String[]> attributes;
    private final List<SubFeatureAnnotation> children;

    private final String source;
    private final String type;
    private final int start;
    private final int end;
    private final double score;
    private final String strand;
    private final int phase;


    /**
     * Creates an instance of a {@link SubFeatureAnnotation}.
     *
     * @param source the source of the {@link SubFeatureAnnotation}
     * @param type   the type of the {@link SubFeatureAnnotation}
     * @param start  the start of the {@link SubFeatureAnnotation}
     * @param end    the end of the {@link SubFeatureAnnotation}
     * @param score  the score of the {@link SubFeatureAnnotation}. If it has a value, it must be a valid floating point
     *               number, else {@link IllegalArgumentException} is thrown
     * @param strand the strand of the {@link SubFeatureAnnotation}. Must be one of '.', '-' or '+', else
     *               {@link IllegalArgumentException} is thrown
     * @param phase  the phase of the {@link SubFeatureAnnotation}. If it has a value, it must be one of 0, 1 or 2, else
     *               {@link IllegalArgumentException} is thrown
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "squid:S3776"}) // There is little use in splitting this constructor
    public SubFeatureAnnotation(final String source, final String type, final String start, final String end,
                                final String score, final String strand, final String phase) {
        attributes = new HashMap<>();
        children = new ArrayList<>();

        this.source = source;
        this.type = type;
        try {
            this.start = Integer.parseInt(start);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("The start was not a valid natural number, it was '" + start + "'.", e);
        }
        try {
            this.end = Integer.parseInt(end);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("The end was not a valid natural number, it was '" + end + "'.", e);
        }

        if (".".equals(score)) {
            this.score = -1;
        } else {
            try {
                this.score = Double.parseDouble(score);
            } catch (final NumberFormatException e) {
                throw new IllegalArgumentException("The score was not a valid floating point number, it was: '"
                        + score + "'.", e);
            }
        }

        if ("+".equals(strand) || "-".equals(strand) || ".".equals(strand)) {
            this.strand = strand;
        } else {
            throw new IllegalArgumentException("Strand was not '+', '-' or '.', it was: '" + strand + "'.");
        }

        if (".".equals(phase)) {
            this.phase = -1;
        } else {
            try {
                this.phase = Integer.parseInt(phase);
            } catch (final NumberFormatException e) {
                throw new IllegalArgumentException("The phase was not a valid natural number, it was: '" + score
                        + "'.", e);
            }
            if (this.phase < 0 || this.phase > 2) {
                throw new IllegalArgumentException("Phase was not 0, 1, or 2, it was: '" + phase + "'.");
            }
        }
    }


    /**
     * Returns the source of the {@link SubFeatureAnnotation}.
     * <p>
     * The source is a free text qualifier intended to describe the algorithm or operating procedure that generated this
     * feature. Typically this is the name of a piece of software, such as "Genescan" or a database name, such as
     * "Genbank." In effect, the source is used to extend the feature ontology by adding a qualifier to the type
     * creating a new composite type that is a subclass of the type in the type column.
     *
     * @return the source of the {@link SubFeatureAnnotation}
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns the type of the {@link SubFeatureAnnotation}.
     * <p>
     * The type of the feature (previously called the "method"). This is constrained to be either a term from the
     * Sequence Ontology or an SO accession number. The latter alternative is distinguished using the syntax SO:000000.
     * In either case, it must be sequence_feature (SO:0000110) or an is_a child of it.
     *
     * @return the type of the {@link SubFeatureAnnotation}
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the start of the {@link SubFeatureAnnotation}.
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
     * @return the start of the {@link SubFeatureAnnotation}
     * @see FeatureAnnotation#getSeqId()
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the end of the {@link SubFeatureAnnotation}.
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
     * @return the end of the {@link SubFeatureAnnotation}
     * @see FeatureAnnotation#getSeqId()
     */
    public int getEnd() {
        return end;
    }

    /**
     * Returns the score of the {@link SubFeatureAnnotation}.
     * <p>
     * The score of the feature, a floating point number. As in earlier versions of the format, the semantics of the
     * score are ill-defined. It is strongly recommended that E-values be used for sequence similarity features, and
     * that P-values be used for ab initio gene prediction features.
     *
     * @return the score of the {@link SubFeatureAnnotation}
     */
    public double getScore() {
        return score;
    }

    /**
     * Returns the strand of the {@link SubFeatureAnnotation}.
     * <p>
     * The strand of the feature. + for positive strand (relative to the landmark), - for minus strand, and . for
     * features that are not stranded. In addition, ? can be used for features whose strandedness is relevant, but
     * unknown.
     *
     * @return the strand of the {@link SubFeatureAnnotation}
     */
    public String getStrand() {
        return strand;
    }

    /**
     * Returns the phase of the {@link SubFeatureAnnotation}.
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
     * @return the phase of the {@link SubFeatureAnnotation}
     */
    public int getPhase() {
        return phase;
    }

    /**
     * Sets an attribute of the annotation.
     * <p>
     * Values must be separated by a comma.
     *
     * @param name  the name of the attribute
     * @param value the value of the attribute
     */
    public void setAttribute(final String name, final String value) {
        if (!VALID_ATTRIBUTES.contains(name)) {
            throw new IllegalArgumentException("The tag: " + name + " is not a valid attribute tag. Must be one of: "
                    + VALID_ATTRIBUTES.toString() + ".");
        }

        final String[] values = value.split(",");
        if ("ID".equals(name) && values.length > 1) {
            throw new IllegalArgumentException("The ID tag had more than one id, it had: " + values.length + ".");
        }

        attributes.put(name, values);
    }

    /**
     * Returns the attributes of this {@link SubFeatureAnnotation}.
     *
     * @return the attributes of the {@link SubFeatureAnnotation}
     */
    public Map<String, String[]> getAttributes() {
        return attributes;
    }

    /**
     * Add a child to this {@link SubFeatureAnnotation}.
     *
     * @param subFeatureAnnotation the {@link SubFeatureAnnotation} to add to the children of this
     *                             {@link SubFeatureAnnotation}
     */
    public void addChild(final SubFeatureAnnotation subFeatureAnnotation) {
        children.add(subFeatureAnnotation);
    }

    /**
     * Returns the list of children of this {@link SubFeatureAnnotation}.
     *
     * @return the list of {@link SubFeatureAnnotation}s that are the children of this {@link SubFeatureAnnotation}
     */
    public List<SubFeatureAnnotation> getChildren() {
        return children;
    }
}
