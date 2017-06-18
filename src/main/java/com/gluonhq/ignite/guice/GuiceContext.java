package com.gluonhq.ignite.guice;

import com.gluonhq.ignite.DIContext;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import javafx.fxml.FXMLLoader;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Supplier;


/**
 * Implementation of dependency injection context for Guice.
 * <p>
 * This class is taken into our own code in order to remove the {@code @Singleton} annotation of the
 * {@code provideFxmlLoader} (line 75). This change has already been included in the code base of
 * the authors of the library, but unfortunately it has not been released yet.
 */
@SuppressWarnings({"PMD", "nullness", "squid:S1186", "squid:S1161"}) // No need to fix third party code
public class GuiceContext implements DIContext {
    private final Object contextRoot;
    protected Injector injector;

    private final Supplier<Collection<Module>> modules;


    /**
     * Create the Guice context
     *
     * @param contextRoot root object to inject
     * @param modules     custom Guice modules
     */
    public GuiceContext(Object contextRoot, Supplier<Collection<Module>> modules) {
        this.contextRoot = Objects.requireNonNull(contextRoot);
        this.modules = Objects.requireNonNull(modules);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void injectMembers(Object obj) {
        injector.injectMembers(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getInstance(Class<T> cls) {
        return injector.getInstance(cls);
    }

    /**
     * {@inheritDoc}
     */
    public final void init() {
        Collection<Object> allModules = new HashSet<>();
        allModules.addAll(this.modules.get());
        allModules.add(new com.gluonhq.ignite.guice.GuiceContext.FXModule());
        injector = Guice.createInjector(allModules.toArray(new Module[0]));
        injectMembers(contextRoot);
    }

    private class FXModule extends AbstractModule {
        @Override
        protected void configure() {
        }

        @Provides
        FXMLLoader provideFxmlLoader() {
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(GuiceContext.this::getInstance);
            return loader;
        }
    }
}
