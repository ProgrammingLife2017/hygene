package org.dnacronym.hygene.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


/**
 * Unit tests for {@link DiscardNewestPolicy}.
 */
class DiscardNewestPolicyTest {
    private DiscardNewestPolicy policy;
    private ThreadPoolExecutor executor;
    private Runnable newRunnable;


    @BeforeEach
    void beforeEach() {
        policy = new DiscardNewestPolicy();
        executor = mock(ThreadPoolExecutor.class);
        newRunnable = mock(Runnable.class);
    }


    /**
     * Tests that an exception is thrown if there are no elements in the queue.
     */
    @Test
    void testEmptyQueue() {
        when(executor.getQueue()).thenReturn(new ArrayBlockingQueue<>(1, true));

        assertThrows(IllegalStateException.class, () -> policy.rejectedExecution(newRunnable, executor));
    }

    /**
     * Tests that nothing happens if the executor is shut down.
     */
    @Test
    void testExecutorIsShutdown() {
        when(executor.isShutdown()).thenReturn(true);

        policy.rejectedExecution(newRunnable, executor);

        verify(executor).isShutdown();
        verifyNoMoreInteractions(executor);
    }

    /**
     * Tests that a queue with one element and a capacity of one has its only element replaced.
     */
    @Test
    void testReplaceOnlyElementSmallCapacity() {
        final Runnable lastRunnable = mock(Runnable.class);
        final BlockingQueue<Runnable> queue
                = new ArrayBlockingQueue<>(1, true, Arrays.asList(lastRunnable));
        when(executor.getQueue()).thenReturn(queue);

        policy.rejectedExecution(newRunnable, executor);

        verify(executor).remove(lastRunnable);
        verify(executor).submit(newRunnable);
    }

    /**
     * Tests that a queue with one element and a capacity of more than one has its only element replaced.
     */
    @Test
    void testReplaceOnlyElementLargeCapacity() {
        final Runnable lastRunnable = mock(Runnable.class);
        final BlockingQueue<Runnable> queue
                = new ArrayBlockingQueue<>(34, true, Arrays.asList(lastRunnable));
        when(executor.getQueue()).thenReturn(queue);

        policy.rejectedExecution(newRunnable, executor);

        verify(executor).remove(lastRunnable);
        verify(executor).submit(newRunnable);
    }

    /**
     * Tests that a queue with two elements has its last element replaced.
     */
    @Test
    void testReplaceSecondElement() {
        final Runnable firstRunnable = mock(Runnable.class);
        final Runnable lastRunnable = mock(Runnable.class);
        final BlockingQueue<Runnable> queue
                = new ArrayBlockingQueue<>(5, true, Arrays.asList(firstRunnable, lastRunnable));
        when(executor.getQueue()).thenReturn(queue);

        policy.rejectedExecution(newRunnable, executor);

        verify(executor).remove(lastRunnable);
        verify(executor).submit(newRunnable);
    }
}
