package org.dnacronym.hygene.core;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Unit tests for {@link ThrottledRunnable}.
 */
class ThrottledRunnableTest {
    /**
     * Tests that a timeout of fewer than 0 milliseconds is not accepted.
     */
    @Test
    void testIllegalTimeout() {
        final Throwable throwable = catchThrowable(() -> new ThrottledRunnable(() -> {}, -24));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests that the runnable is executed.
     */
    @Test
    void testRuns() {
        final int[] number = {0};
        final ThrottledRunnable runnable = new ThrottledRunnable(() -> number[0]++, 34);

        runnable.run();
        runnable.block();

        assertThat(number[0]).isEqualTo(1);
    }

    /**
     * Tests that the runnable is executed twice.
     */
    @Test
    void testRunsTwice() {
        final int[] number = {0};
        final ThrottledRunnable runnable = new ThrottledRunnable(() -> number[0]++, 55);

        runnable.run();
        runnable.block();
        runnable.run();
        runnable.block();

        assertThat(number[0]).isEqualTo(2);
    }

    /**
     * Tests that the second run has to wait for some time after the first run.
     */
    @Test
    void testRunsTwiceDelay() {
        final int[] number = {0};
        final ThrottledRunnable runnable = new ThrottledRunnable(() -> number[0]++, 500);

        runnable.run();
        runnable.block();
        final Instant then = Instant.now();
        runnable.run();
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
        final ThrottledRunnable runnable = new ThrottledRunnable(() -> number[0]++, 0);

        runnable.run();
        runnable.run();
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
        final ThrottledRunnable runnable = new ThrottledRunnable(() -> number[0]++, 0);

        runnable.run();
        runnable.run();
        runnable.run();
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
        final ThrottledRunnable runnable = new ThrottledRunnable(() -> {
            number[0]++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 500);

        runnable.run();
        runnable.run();
        runnable.run();
        runnable.block();

        assertThat(number[0]).isEqualTo(1);
    }

    /**
     * Tests that a block returns if {@link ThrottledRunnable#run()} is never called.
     * <p>
     * This method does not contain an assert because it tests that a method returns at all.
     */
    @Test
    void testBlockWithoutRun() {
        final ThrottledRunnable runnable = new ThrottledRunnable(() -> {}, 500);

        runnable.block();
    }

    /**
     * Tests that block returns if the runnable throws an exception.
     * <p>
     * This method does not contain an assert because it tests that a method returns at all.
     */
    @Test
    void testBlockInterrupted() {
        final ThrottledRunnable runnable = new ThrottledRunnable(() -> {
            throw new RuntimeException("Dummy exception");
        }, 500);

        runnable.run();
        runnable.block();
    }
}
