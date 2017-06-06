package org.dnacronym.hygene.core;

import com.google.common.eventbus.Subscribe;
import org.dnacronym.hygene.events.GraphQueryChangeEvent;
import org.dnacronym.hygene.models.Graph;
import org.dnacronym.hygene.models.GraphQuery;
import org.dnacronym.hygene.models.Node;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Integrations tests for the event bus workflow.
 */
public final class EventBusIntegrationTest {
    private static final int[][] DUMMY_GRAPH = new int[][] {Node.createEmptyNodeArray()};


    @Test
    void testThatEventsCanBePostedAndListenedTo() {
        final GraphQuery graphQuery = new GraphQuery(new Graph(DUMMY_GRAPH, null));

        final CenterPointQueryChangeEventListener listener = new CenterPointQueryChangeEventListener();
        HygeneEventBus.getInstance().register(listener);

        graphQuery.query(0, 1);

        assertThat(graphQuery).isEqualTo(listener.getGraphQuery());
    }


    private static class CenterPointQueryChangeEventListener {
        private GraphQuery graphQuery;

        @Subscribe
        public void centerPointQueryChanged(final GraphQueryChangeEvent event) {
            graphQuery = event.getGraphQuery();
        }

        public GraphQuery getGraphQuery() {
            return graphQuery;
        }
    }
}
