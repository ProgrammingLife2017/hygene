package org.dnacronym.hygene.ui.graph;

import org.dnacronym.hygene.coordinatesystem.GenomeIndex;
import org.dnacronym.hygene.coordinatesystem.GenomePoint;
import org.dnacronym.hygene.models.FeatureAnnotation;
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
    private GenomeIndex genomeIndex;

    private Map<FeatureAnnotation, List<GenomePoint>> genomeIndexMap = new HashMap<>();


    /**
     * Creates an instance of {@link GraphAnnotation}.
     *
     * @param genomeNavigation the {@link GenomeNavigation} used to retrieve
     *                         {@link org.dnacronym.hygene.coordinatesystem.GenomeIndex}es.
     */
    public GraphAnnotation(final GenomeNavigation genomeNavigation) {
        genomeNavigation.getGenomeIndexProperty()
                .addListener((observable, oldValue, newValue) -> {
                    genomeIndex = newValue;
                    genomeIndexMap.clear();
                });
    }

    /**
     * @param featureAnnotation
     * @throws SQLException
     */
    public void addFeatureAnnotation(final FeatureAnnotation featureAnnotation) throws SQLException {
        genomeIndexMap.put(featureAnnotation, new ArrayList<>(2));

        final String genomeName = featureAnnotation.getSequenceId();
        final int startOffset = featureAnnotation.getSubFeatureAnnotations().get(0).getStart();
        final int endOffset = featureAnnotation.getSubFeatureAnnotations().get(0).getEnd();

        genomeIndex.getGenomePoint(genomeName, startOffset)
                .ifPresent(genomePoint -> genomeIndexMap.get(featureAnnotation).add(genomePoint));
        genomeIndex.getGenomePoint(genomeName, endOffset)
                .ifPresent(genomePoint -> genomeIndexMap.get(featureAnnotation).add(genomePoint));
    }
}
