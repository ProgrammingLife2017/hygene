package org.dnacronym.hygene.core;

import com.google.common.eventbus.Subscribe;
import org.dnacronym.hygene.events.CenterPointQueryChangeEvent;
import org.dnacronym.hygene.graph.CenterPointQuery;
import org.dnacronym.hygene.graph.Graph;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Integrations tests for the event bus workflow.
 */
public final class EventBusIntegrationTest {
    private static final int[][] DUMMY_GRAPH = new int[][] {Graph.createEmptyNodeArray()};


    @Test
    void testThatEventsCanBePostedAndListenedTo() {
        final CenterPointQuery centerPointQuery = new CenterPointQuery(new Graph(DUMMY_GRAPH, null));

        final CenterPointQueryChangeEventListener listener = new CenterPointQueryChangeEventListener();
        HygeneEventBus.getInstance().register(listener);

        centerPointQuery.query(0, 1);

        assertThat(centerPointQuery).isEqualTo(listener.getCenterPointQuery());
    }


    private static class CenterPointQueryChangeEventListener {
        private CenterPointQuery centerPointQuery;

        @Subscribe
        public void centerPointQueryChanged(final CenterPointQueryChangeEvent event) {
            centerPointQuery = event.getCenterPointQuery();
        }

        public CenterPointQuery getCenterPointQuery() {
            return centerPointQuery;
        }
    }
}
