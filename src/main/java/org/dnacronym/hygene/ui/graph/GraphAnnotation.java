package org.dnacronym.hygene.ui.graph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.models.FeatureAnnotation;
import org.dnacronym.hygene.parser.GffFile;
import org.dnacronym.hygene.ui.genomeindex.GenomeNavigation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class GraphAnnotation {
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
     */
    public GraphAnnotation(final GenomeNavigation genomeNavigation, final GraphStore graphStore) {
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
     * @param featureAnnotation
     * @throws SQLException
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
        } catch (final SQLException e) {
            LOGGER.error("Unable to add an annotation.", e);
        }
    }

    public Map<FeatureAnnotation, List<GenomePoint>> getGenomeIndexMap() {
        return genomeIndexMap;
    }

    public List<FeatureAnnotation> getFeatureAnnotations() {
        return featureAnnotations;
    }
}
