package org.dnacronym.hygene.core;

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
        final int[] number = {0};
        final ThrottledExecutor runnable = new ThrottledExecutor(34);

        runnable.run(() -> number[0]++);
        runnable.block();

        assertThat(number[0]).isEqualTo(1);
    }

    /**
     * Tests that the runnable is executed twice.
     */
    @Test
    void testRunsTwice() {
        final int[] number = {0};
        final ThrottledExecutor runnable = new ThrottledExecutor(55);

        runnable.run(() -> number[0]++);
        runnable.block();
        runnable.run(() -> number[0]++);
        runnable.block();

        assertThat(number[0]).isEqualTo(2);
    }

    /**
     * Tests that the second run has to wait for some time after the first run.
     */
    @Test
    void testRunsTwiceDelay() {
        final int[] number = {0};
        final ThrottledExecutor runnable = new ThrottledExecutor(500);

        runnable.run(() -> number[0]++);
        runnable.block();
        final Instant then = Instant.now();
        runnable.run(() -> number[0]++);
        runnable.block();
        final long time = Duration.between(then, Instant.now()).toMillis();

        assertThat(time).isGreaterThan(250);
    }

    /**
     * Tests that the runnable is executed once, because the first run is interrupted by the second.
     */
    @Test
    void testInterruptOnce() {
        final int[] number = {0};
        final ThrottledExecutor runnable = new ThrottledExecutor(0);

        runnable.run(() -> number[0]++);
        runnable.run(() -> number[0]++);
        runnable.block();

        assertThat(number[0]).isEqualTo(1);
    }

    /**
     * Tests that the runnable is executed once, because the first run is interrupted by the second, and the second
     * run is interrupted by the third.
     */
    @Test
    void testInterruptTwice() {
        final int[] number = {0};
        final ThrottledExecutor runnable = new ThrottledExecutor(0);

        runnable.run(() -> number[0]++);
        runnable.run(() -> number[0]++);
        runnable.run(() -> number[0]++);
        runnable.block();

        assertThat(number[0]).isEqualTo(1);
    }

    /**
     * Tests that the runnable is executed once, because the first run is interrupted by the second and the third run
     * sees that the second run scheduled a run.
     */
    @Test
    @SuppressWarnings("squid:S2925") // Thread.sleep() is acceptable here
    void testScheduled() {
        final int[] number = {0};
        final Runnable action = () -> {
            number[0]++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        final ThrottledExecutor runnable = new ThrottledExecutor(500);

        runnable.run(action);
        runnable.run(action);
        runnable.run(action);
        runnable.block();

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
        final ThrottledExecutor runnable = new ThrottledExecutor(500);

        runnable.stop();
        runnable.block();
    }

    /**
     * Tests that a runnable is cancelled after {@link ThrottledExecutor#stop()} is called.
     */
    @Test
    @SuppressWarnings("squid:S2925") // Thread.sleep() is acceptable here
    void testStop() {
        final int[] number = {0};
        final ThrottledExecutor runnable = new ThrottledExecutor(59);

        runnable.run(() -> {
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

            number[0]++;
        });
        runnable.stop();

        assertThat(number[0]).isEqualTo(0);
    }


    /**
     * Tests that a block returns if {@link ThrottledExecutor#run(Runnable)} is never called.
     * <p>
     * This method does not contain an assert because it tests that a method returns at all.
     */
    @Test
    void testBlockWithoutRun() {
        final ThrottledExecutor runnable = new ThrottledExecutor(500);

        runnable.block();
    }

    /**
     * Tests that block returns if the runnable throws an exception.
     * <p>
     * This method does not contain an assert because it tests that a method returns at all.
     */
    @Test
    void testBlockInterrupted() {
        final ThrottledExecutor runnable = new ThrottledExecutor(500);

        runnable.run(() -> {
            throw new RuntimeException("Dummy exception");
        });
        runnable.block();
    }

    /**
     * Tests that stopping before blocking returns immediately.
     * <p>
     * This method does not contain an assert because it tests that a method returns at all.
     */
    @Test
    void testBlockAfterStop() {
        final ThrottledExecutor runnable = new ThrottledExecutor(12);

        runnable.run(() -> {});
        runnable.stop();
        runnable.block();
    }
}
