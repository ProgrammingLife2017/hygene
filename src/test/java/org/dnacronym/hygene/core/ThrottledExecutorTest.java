package org.dnacronym.hygene.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for {@link ThrottledExecutor}.
 */
class ThrottledExecutorTest {
    /**
     * An integer that can be used in lambdas.
     */
    private final int[] number = {0};

    /**
     * Increments the {@code number} field, and then sleeps for 500 milliseconds.
     */
    @SuppressWarnings("squid:S2925") // Thread.sleep() is acceptable because it is meant to be interrupted
    private final Runnable incrementBeforeDelay = () -> {
        try {
            number[0]++;
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            // Do nothing because this is intentional
        }
    };
    /**
     * Increments the {@code number} field after sleeping for 500 milliseconds.
     */
    @SuppressWarnings("squid:S2925") // Thread.sleep() is acceptable because it is meant to be interrupted
    private final Runnable incrementAfterDelay = () -> {
        try {
            Thread.sleep(500);
            number[0]++;
        } catch (final InterruptedException e) {
            // Do nothing because this is intentional
        }
    };

    private ThrottledExecutor executor;


    @BeforeEach
    void beforeEach() {
        number[0] = 0;
        executor = createExecutor(0);
    }

    /**
     * Sets the {@link ThrottledExecutor} to be used in this class' tests.
     * <p>
     * This method should be overridden in subclasses that wish to make use of these tests.
     *
     * @param timeout the timeout
     */
    ThrottledExecutor createExecutor(final int timeout) {
        return new ThrottledExecutor(timeout);
    }


    /**
     * Tests that a timeout of fewer than 0 milliseconds is not accepted.
     */
    @Test
    void testIllegalTimeout() {
        final Throwable throwable = catchThrowable(() -> new ThrottledExecutor(-24));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests that the runnable is executed.
     */
    @Test
    void testRuns() {
        executor.run(() -> number[0]++);
        executor.block();

        assertThat(number[0]).isEqualTo(1);
    }

    /**
     * Tests that the runnable is executed twice.
     */
    @Test
    void testRunsTwice() {
        executor.run(() -> number[0]++);
        executor.block();
        executor.run(() -> number[0]++);
        executor.block();

        assertThat(number[0]).isEqualTo(2);
    }

    /**
     * Tests that the second run has to wait for some time after the first run.
     */
    @Test
    void testRunsTwiceDelay() {
        executor = createExecutor(500);

        executor.run(() -> number[0]++);
        executor.block();
        final Instant then = Instant.now();
        executor.run(() -> number[0]++);
        executor.block();
        final long time = Duration.between(then, Instant.now()).toMillis();

        assertThat(time).isGreaterThan(250);
    }

    /**
     * Tests that the runnable is executed once, because the first run is interrupted by the second.
     */
    @Test
    void testInterruptOnce() {
        executor.run(incrementAfterDelay);
        executor.run(incrementAfterDelay);
        executor.block();

        assertThat(number[0]).isEqualTo(1);
    }

    /**
     * Tests that the runnable is executed once, because the first run is interrupted by the second, and the second
     * run is interrupted by the third.
     */
    @Test
    void testInterruptTwice() {
        executor.run(incrementAfterDelay);
        executor.run(incrementAfterDelay);
        executor.run(incrementAfterDelay);
        executor.block();

        assertThat(number[0]).isEqualTo(1);
    }

    /**
     * Tests that the runnable is executed once, because the first run is interrupted by the second and the third run
     * sees that the second run scheduled a run.
     */
    @Test
    void testScheduled() {
        executor.run(incrementBeforeDelay);
        executor.run(incrementBeforeDelay);
        executor.run(incrementBeforeDelay);
        executor.block();

        assertThat(number[0]).isEqualTo(1);
    }


    /**
     * Tests that nothing happens when {@link ThrottledExecutor#stop()} is called but {@link
     * ThrottledExecutor#run(Runnable)} is never called.
     * <p>
     * This method does not contain an assert because it tests that a method returns at all.
     */
    @Test
    void testStopWithoutRun() {
        executor.stop();
        executor.block();
    }

    /**
     * Tests that a runnable is cancelled after {@link ThrottledExecutor#stop()} is called.
     */
    @Test
    void testStop() {
        executor.run(incrementAfterDelay);
        executor.stop();

        assertThat(number[0]).isEqualTo(0);
    }


    /**
     * Tests that a block returns if {@link ThrottledExecutor#run(Runnable)} is never called.
     * <p>
     * This method does not contain an assert because it tests that a method returns at all.
     */
    @Test
    void testBlockWithoutRun() {
        executor.block();
    }

    /**
     * Tests that block returns if the runnable throws an exception.
     * <p>
     * This method does not contain an assert because it tests that a method returns at all.
     */
    @Test
    void testBlockInterrupted() {
        executor.run(() -> {
            throw new RuntimeException("Dummy exception");
        });
        executor.block();
    }

    /**
     * Tests that stopping before blocking returns immediately.
     * <p>
     * This method does not contain an assert because it tests that a method returns at all.
     */
    @Test
    void testBlockAfterStop() {
        executor.run(() -> {});
        executor.stop();
        executor.block();
    }
}
