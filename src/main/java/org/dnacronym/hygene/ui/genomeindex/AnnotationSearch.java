package org.dnacronym.hygene.ui.genomeindex;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dnacronym.hygene.graph.annotation.Annotation;
import org.dnacronym.hygene.ui.graph.GraphAnnotation;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Represents annotation search results.
 */
public final class AnnotationSearch {
    private final GraphAnnotation graphAnnotation;

    private final ObservableList<Annotation> searchResults = FXCollections.observableArrayList();
    private final ReadOnlyListWrapper<Annotation> readOnlySearchResults = new ReadOnlyListWrapper<>(searchResults);


    /**
     * Constructs and initializes {@link AnnotationSearch}.
     *
     * @param graphAnnotation the current instance of {@link GraphAnnotation}
     */
    @Inject
    public AnnotationSearch(final GraphAnnotation graphAnnotation) {
        this.graphAnnotation = graphAnnotation;
    }

    /**
     * Searches for a given query in all annotations.
     *
     * @param query search term
     */
    public void search(final String query) {
        final List<Annotation> results = graphAnnotation.getAnnotationCollection().getAnnotations().stream()
                .filter(annotation -> annotation.matchString(query))
                .collect(Collectors.toList());

        searchResults.setAll(results);
    }

    /**
     * Returns the search results.
     *
     * @return the search results
     */
    public ReadOnlyListWrapper<Annotation> getSearchResults() {
        return readOnlySearchResults;
    }
}
