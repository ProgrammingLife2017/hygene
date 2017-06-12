package org.dnacronym.hygene.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * A threaded {@link Runnable} that can only be called a limited number of times.
 * <p>
 * When {@link #run(Runnable)} is called and there is no thread currently running, the action is scheduled until a later
 * moment if executing it now would violate the timeout between executions, or it is executed immediately otherwise.
 * <p>
 * When {@link #run(Runnable)} is called while a thread is currently running, that thread is interrupted. If no action
 * is scheduled, the action is scheduled until a later moment if executing it now would violate the timeout between
 * executions, or it is executed immediately otherwise.
 */
public final class ThrottledExecutor {
    private static final Logger LOGGER = LogManager.getLogger(ThrottledExecutor.class);

    private final ScheduledExecutorService executor;
    private final int timeout;

    private @MonotonicNonNull ScheduledFuture<?> future;
    private Instant waitUntil;


    /**
     * Constructs a new {@link ThrottledExecutor}.
     *
     * @param timeout the minimal time between each execution in milliseconds
     */
    public ThrottledExecutor(final int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("The limit must be a positive integer.");
        }

        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.timeout = timeout;

        this.waitUntil = Instant.now();
    }


    /**
     * Tries to execute the action in a separate thread, or schedules one if that is not possible.
     *
     * @param action the {@link Runnable} to execute or schedule
     */
    public synchronized void run(final Runnable action) {
        if (future != null && !future.isDone() && !future.isCancelled()) {
            if (future.getDelay(TimeUnit.MILLISECONDS) >= 0) {
                return;
            } else {
                future.cancel(true);
            }
        }

        final long delay = Math.max(0, Duration.between(Instant.now(), waitUntil).toMillis());
        future = executor.schedule(action, delay, TimeUnit.MILLISECONDS);
        waitUntil = Instant.now().plus(timeout, ChronoUnit.MILLIS);
    }

    /**
     * Stops the current action if it is running, unschedules it if it is scheduled, and starts a timeout to prevent
     * excessive interrupts.
     */
    public synchronized void stop() {
        if (future == null) {
            return;
        }
        if (!future.isDone() && !future.isCancelled()) {
            future.cancel(true);
        }

        waitUntil = Instant.now().plus(timeout, ChronoUnit.MILLIS);
    }

    /**
     * Blocks the current thread until the action has completed.
     */
    public synchronized void block() {
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
