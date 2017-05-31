package org.dnacronym.hygene.core;

import com.google.common.eventbus.EventBus;


/**
 * This class is a singleton implementation of the Guava HygeneEventBus.
 */
public final class HygeneEventBus extends EventBus {
    private static final HygeneEventBus INSTANCE = new HygeneEventBus();


    /**
     * Makes class non instantiable.
     */
    private HygeneEventBus() {
    }


    /**
     * Gets the instance of {@link HygeneEventBus}.
     *
     * @return the instance of {@link HygeneEventBus}
     */
    public static HygeneEventBus getInstance() {
        return INSTANCE;
    }
}
