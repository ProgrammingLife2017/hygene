package org.dnacronym.hygene.core;

/**
 * This class is a singleton implementation of the Guava HygeneEventBus.
 */
public final class HygeneEventBus extends com.google.common.eventbus.EventBus {
    private static HygeneEventBus instance = new HygeneEventBus();


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
        return instance;
    }
}
