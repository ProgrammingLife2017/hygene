package org.dnacronym.hygene.ui.graph;

import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.graph.annotation.FeatureAnnotation;
import org.dnacronym.hygene.parser.GffFile;
import org.dnacronym.hygene.ui.genomeindex.GenomeNavigation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Stores all {@link FeatureAnnotation} of the {@link GffFile}s in memory for quick retrieval.
 */
@SuppressWarnings("PMD.ImmutableField") // The values are set via event listeners, so they should not be immutable
public final class GraphAnnotation {
    private static final Logger LOGGER = LogManager.getLogger(GraphAnnotation.class);

    private GenomeIndex genomeIndex;

    private Map<FeatureAnnotation, List<GenomePoint>> genomeIndexMap = new HashMap<>();
    /**
     * List that allows faster iteration over all {@link FeatureAnnotation}s.
     */
    private List<FeatureAnnotation> featureAnnotations = new ArrayList<>();

    /**
     * Creates an instance of {@link GraphAnnotation}.
     *
     * @param genomeNavigation the {@link GenomeNavigation} used to retrieve
     *                         {@link org.dnacronym.hygene.coordinatesystem.GenomeIndex}es.
     * @param graphStore       the {@link GraphStore} who's {@link GffFile}s are used to update the
     *                         {@link FeatureAnnotation}s
     */
    @Inject
    public GraphAnnotation(final GenomeNavigation genomeNavigation, final GraphStore graphStore) {
        genomeIndex = genomeNavigation.getGenomeIndexProperty().get();
        genomeNavigation.getGenomeIndexProperty().addListener((observable, oldValue, newValue) -> {
            genomeIndex = newValue;
            genomeIndexMap.clear();
        });

        graphStore.getGffFiles().addListener((observable, oldValue, newValue) -> {
            for (final GffFile gffFile : newValue) {
                final FeatureAnnotation featureAnnotation = gffFile.getFeatureAnnotation();
                if (genomeIndexMap.containsKey(featureAnnotation)) {
                    continue;
                }

                addFeatureAnnotation(featureAnnotation);
            }
        });
    }


    /**
     * Adds a {@link FeatureAnnotation}, and add {@link GenomePoint}s which denote the start and end points of this
     * annotation in the graph.
     *
     * @param featureAnnotation the {@link FeatureAnnotation} to add
     * @throws SQLException if an error occurred whilst creating {@link GenomePoint}s
     */
    public void addFeatureAnnotation(final FeatureAnnotation featureAnnotation) {
        final String genomeName = featureAnnotation.getSequenceId();
        final int startOffset = featureAnnotation.getSubFeatureAnnotations().get(0).getStart();
        final int endOffset = featureAnnotation.getSubFeatureAnnotations().get(0).getEnd();

        final List<GenomePoint> genomePoints = new ArrayList<>(2);

        try {
            genomeIndex.getGenomePoint(genomeName, startOffset).ifPresent(genomePoints::add);
            genomeIndex.getGenomePoint(genomeName, endOffset).ifPresent(genomePoints::add);

            genomeIndexMap.put(featureAnnotation, genomePoints);
            featureAnnotations.add(featureAnnotation);
        } catch (final SQLException e) {
            LOGGER.error("Unable to add an annotation.", e);
        }
    }

    /**
     * Returns the {@link Map} of {@link FeatureAnnotation}s and {@link GenomePoint}s denoting the start and end points
     * of each {@link FeatureAnnotation}.
     *
     * @return the {@link Map} of {@link FeatureAnnotation}s and {@link GenomePoint}s denoting the start and end points
     * of each {@link FeatureAnnotation}.
     */
    public Map<FeatureAnnotation, List<GenomePoint>> getGenomeIndexMap() {
        return genomeIndexMap;
    }

    /**
     * Returns the {@link List} of {@link FeatureAnnotation}s stored internally.
     *
     * @return the {@link List} of {@link FeatureAnnotation}s stored internally
     */
    public List<FeatureAnnotation> getFeatureAnnotations() {
        return featureAnnotations;
    }
}
