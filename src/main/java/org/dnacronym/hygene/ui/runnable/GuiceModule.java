package org.dnacronym.hygene.ui.runnable;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.dnacronym.hygene.ui.bookmark.BookmarkStore;
import org.dnacronym.hygene.ui.bookmark.SimpleBookmarkStore;
import org.dnacronym.hygene.ui.genomeindex.GenomeNavigation;
import org.dnacronym.hygene.ui.graph.GraphAnnotation;
import org.dnacronym.hygene.ui.graph.GraphDimensionsCalculator;
import org.dnacronym.hygene.ui.graph.GraphLocation;
import org.dnacronym.hygene.ui.graph.GraphMovementCalculator;
import org.dnacronym.hygene.ui.graph.GraphStore;
import org.dnacronym.hygene.ui.graph.GraphVisualizer;
import org.dnacronym.hygene.ui.node.SequenceVisualizer;
import org.dnacronym.hygene.ui.progressbar.StatusBar;
import org.dnacronym.hygene.ui.query.Query;
import org.dnacronym.hygene.ui.settings.Settings;


/**
 * A module contributes configuration information, typically interface bindings, which will be used to create an
 * {@link com.google.inject.Injector}.
 */
public final class GuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GraphStore.class).in(Singleton.class);
        bind(Settings.class).in(Singleton.class);
        bind(Query.class).in(Singleton.class);
        bind(BookmarkStore.class).to(SimpleBookmarkStore.class).in(Singleton.class);
        bind(GraphLocation.class).in(Singleton.class);

        bind(GraphDimensionsCalculator.class).in(Singleton.class);
        bind(GraphMovementCalculator.class).in(Singleton.class);
        bind(GenomeNavigation.class).in(Singleton.class);
        bind(GraphAnnotation.class).in(Singleton.class);
        bind(GraphVisualizer.class).in(Singleton.class);

        bind(SequenceVisualizer.class).in(Singleton.class);
        bind(StatusBar.class).in(Singleton.class);
    }
}
