package org.dnacronym.hygene.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.MoreExecutors.getExitingExecutorService;


/**
 * A threaded executor of {@code Runnable}s of which the number of calls over time can be throttled.
 * <p>
 * The {@code ThreadedExecutor} is useful if an action must be executed in a thread, this action must be interrupted
 * whenever some event occurs, and these interrupts occur very often. When this is the case, the {@code
 * ThreadedExecutor} makes sure that these interrupts cannot occur too often, and that there is only very little
 * overhead for managing threads.
 * <p>
 * The timeout is measured as the time in milliseconds after the invocation of the action. If the action is
 * interrupted using {@link #stop()}, the timeout is reset.
 * <p>
 * When {@link #run(Runnable)} is called and there is no action currently running, the action is executed as soon as
 * the minimal timeout has passed.
 * When {@link #run(Runnable)} is called while a thread is currently running, that thread is interrupted, and the new
 * action is executed as soon as the minimal timeout has passed.
 */
public class ThrottledExecutor {
    private static final Logger LOGGER = LogManager.getLogger(ThrottledExecutor.class);
    private static final int THREAD_COUNT = 1;

    private final ExecutorService executor;

    private @MonotonicNonNull Runnable currentAction;
    private @MonotonicNonNull Future<?> future;


    /**
     * Constructs a new {@link ThrottledExecutor}.
     *
     * @param timeout the minimal time between each execution in milliseconds
     */
    public ThrottledExecutor(final int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("The timeout must be a positive integer.");
        }

        final ThreadPoolExecutor executor = new ThreadPoolExecutor(
                THREAD_COUNT, THREAD_COUNT, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(2, true),
                new DiscardNewestPolicy());
        this.executor = getExitingExecutorService(executor);
    }


    /**
     * Tries to execute the action in a separate thread, or schedules one if that is not possible.
     *
     * @param action the {@link Runnable} to execute or schedule
     */
    public final synchronized void run(final Runnable action) {
        if (future != null && !future.isDone() && !future.isCancelled()) {
            if (action.equals(currentAction)) {
                return;
            }

            future.cancel(true);
        }

        currentAction = action;
        future = executor.submit(action);
    }

    /**
     * Stops the current action if it is running, unschedules it if it is scheduled, and starts a timeout to prevent
     * excessive interrupts.
     */
    public final synchronized void stop() {
        if (future == null) {
            return;
        }
        if (!future.isDone() && !future.isCancelled()) {
            future.cancel(true);
        }
    }

    /**
     * Blocks the current thread until the action has completed.
     */
    public final synchronized void block() {
        if (future == null) {
            return;
        }
        if (future.isDone() || future.isCancelled()) {
            return;
        }

        try {
            future.get();
        } catch (final InterruptedException | ExecutionException e) {
            LOGGER.warn(e);
        }
    }
}
