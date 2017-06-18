package org.dnacronym.hygene.ui.query;

import javafx.beans.property.SimpleObjectProperty;
import org.dnacronym.hygene.graph.SearchQuery;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.UITestBase;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link Query}.
 */
final class QueryTest extends UITestBase {
    private Query query;
    private SearchQuery searchQuery;


    public void beforeEach() {
        final GraphStore graphStore = mock(GraphStore.class);
        when(graphStore.getGfaFileProperty()).thenReturn(new SimpleObjectProperty<>());
        searchQuery = mock(SearchQuery.class);

        query = new Query(graphStore);
        query.setSearchQuery(searchQuery);
    }


    @Test
    void testSearch() throws ParseException {
        final String query = ".*A.*";

        interact(() -> {
            try {
                this.query.query(query);

                verify(searchQuery, timeout(0)).executeSequenceRegexQuery(query);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }
}
