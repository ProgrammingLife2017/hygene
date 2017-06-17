package org.dnacronym.hygene.graph.layout;

import org.dnacronym.hygene.graph.node.Node;
import org.dnacronym.hygene.graph.Subgraph;


/**
 * Lays out a {@link Subgraph} according to the Sugiyama style, also known as layered graph drawing or hierarchical
 * graph drawing.
 * <p>
 * The algorithm starts by placing the nodes in layers, and then proceeds to reduce the number of crossings.
 */
public final class SugiyamaLayout implements Layout {
    private final SugiyamaLayerer layerer;
    private final SugiyamaCrossingsReducer crossingsReducer;


    /**
     * Constructs a new {@link SugiyamaLayout}.
     */
    public SugiyamaLayout() {
        this.layerer = new FafospLayerer();
        this.crossingsReducer = new BarycentricCrossingsReducer();
    }


    @Override
    public void layOut(final Subgraph subgraph) {
        final Node[][] layers = layerer.layer(subgraph);
        crossingsReducer.reduceCrossings(layers);
        if (Thread.interrupted()) {
            return;
        }

        for (final Node[] layer : layers) {
            for (int i = 0; i < layer.length; i++) {
                if (layer[i] == null) {
                    continue;
                }

                layer[i].setYPosition(i);
            }
        }
    }
}
