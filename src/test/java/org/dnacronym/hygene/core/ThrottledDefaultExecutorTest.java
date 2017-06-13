package org.dnacronym.hygene.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link ThrottledDefaultExecutor}.
 */
class ThrottledDefaultExecutorTest extends ThrottledExecutorTest {
    @Override
    ThrottledExecutor createExecutor(final int timeout) {
        return new ThrottledDefaultExecutor(timeout, () -> {});
    }


    @Test
    void testRunDefaultTwice() {
        final int[] number = {0};

        final ThrottledDefaultExecutor executor = new ThrottledDefaultExecutor(0, () -> number[0]++);

        executor.run();
        executor.run();
        executor.block();

        assertThat(number[0]).isEqualTo(1);
    }

    @Test
    @SuppressWarnings("squid:S2925") // Thread.sleep() is acceptable because it is meant to be interrupted
    void testRunDefaultInterrupt() throws InterruptedException {
        final int[] number = {0};

        final ThrottledDefaultExecutor executor = new ThrottledDefaultExecutor(0, () -> {
            try {
                Thread.sleep(5000);
                number[0]++;
            } catch (final InterruptedException e) {
                return;
            }
        });

        executor.run();
        executor.run(() -> number[0]--);
        executor.block();

        assertThat(number[0]).isEqualTo(-1);
    }
}
