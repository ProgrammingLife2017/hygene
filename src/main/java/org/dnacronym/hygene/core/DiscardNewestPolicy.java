package org.dnacronym.hygene.core;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * A handler for rejected tasks that discards the newest unhandled request and then retries adding it,
 * unless the executor is shut down, in which case the task is discarded.
 */
public final class DiscardNewestPolicy implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(final Runnable runnable, final ThreadPoolExecutor executor) {
        if (executor.isShutdown()) {
            return;
        }

        final @Nullable Runnable lastTask = getLastElement(executor.getQueue());
        if (lastTask == null) {
            throw new IllegalStateException("ThreadPoolExecutor should not be full while queue is empty.");
        }
        executor.remove(lastTask);
        executor.submit(runnable);
    }


    /**
     * Returns the last element in a {@link Queue}.
     *
     * @param queue a {@link Queue}
     * @param <T>   the type of the elements in the {@link Queue}
     * @return the last element in a {@link Queue}
     */
    private <T> @Nullable T getLastElement(final Queue<T> queue) {
        final Iterator<T> iterator = queue.iterator();

        T last = null;
        while (iterator.hasNext()) {
            last = iterator.next();
        }

        return last;
    }
}
