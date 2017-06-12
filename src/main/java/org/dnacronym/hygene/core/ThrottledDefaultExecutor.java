package org.dnacronym.hygene.core;


/**
 * A {@link ThrottledExecutor} that has a default {@link Runnable} to execute.
 */
public final class ThrottledDefaultExecutor extends ThrottledExecutor {
    private final Runnable defaultAction;


    /**
     * Constructs a new {@link ThrottledDefaultExecutor} with the given default action.
     *
     * @param timeout       the minimal time between each execution in milliseconds
     * @param defaultAction the default action to execute
     */
    public ThrottledDefaultExecutor(final int timeout, final Runnable defaultAction) {
        super(timeout);

        this.defaultAction = defaultAction;
    }


    /**
     * Tries to execute the default action in a separate thread, or schedules it if that is not possible.
     */
    public synchronized void run() {
        run(defaultAction);
    }
}
