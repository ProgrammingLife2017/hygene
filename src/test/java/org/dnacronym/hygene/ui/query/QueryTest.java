package org.dnacronym.hygene.ui.query;


import javafx.beans.property.SimpleObjectProperty;
import org.dnacronym.hygene.graph.SearchQuery;
import org.dnacronym.hygene.parser.ParseException;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link Query}.
 */
final class QueryTest {
    private GraphStore graphStore;
    private Query query;
    private SearchQuery searchQuery;


    @BeforeEach
    void beforeEach() {
        graphStore = mock(GraphStore.class);
        when(graphStore.getGfaFileProperty()).thenReturn(new SimpleObjectProperty<>());
        searchQuery = mock(SearchQuery.class);

        query = new Query(graphStore);
        query.setSearchQuery(searchQuery);
    }


    @Test
    void testSearch() throws ParseException {
        final String query = ".*A.*";

        this.query.query(query);

        verify(searchQuery).executeSequenceRegexQuery(query);
    }
}
