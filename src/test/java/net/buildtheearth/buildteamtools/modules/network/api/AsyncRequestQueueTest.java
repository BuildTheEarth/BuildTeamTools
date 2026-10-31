package net.buildtheearth.buildteamtools.modules.network.api;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class AsyncRequestQueueTest {

    @Test
    void shouldRespectIntervalBetweenRequests() throws Exception {
        long intervalMs = 300;

        try (AsyncRequestQueue queue =
                     new AsyncRequestQueue(intervalMs, "test-queue", 10)) {

            AtomicLong firstStart = new AtomicLong();
            AtomicLong secondStart = new AtomicLong();

            CompletableFuture<Void> first = queue.submit(() -> {
                firstStart.set(System.nanoTime());
                return CompletableFuture.completedFuture(null);
            });

            CompletableFuture<Void> second = queue.submit(() -> {
                secondStart.set(System.nanoTime());
                return CompletableFuture.completedFuture(null);
            });

            CompletableFuture.allOf(first, second).get(3, TimeUnit.SECONDS);

            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(
                    secondStart.get() - firstStart.get()
            );

            assertTrue(elapsedMs >= intervalMs,
                    "Requests were started too close together: " + elapsedMs + "ms");
        }
    }

    @Test
    void shouldRespectIntervalWhenSubmittingAfterQueueBecomesIdle()
            throws Exception {
        long intervalMs = 500;

        try (AsyncRequestQueue queue =
                     new AsyncRequestQueue(intervalMs, "test-queue", 10)) {

            AtomicLong firstStart = new AtomicLong();
            AtomicLong secondStart = new AtomicLong();

            queue.submit(() -> {
                firstStart.set(System.nanoTime());
                return CompletableFuture.completedFuture(null);
            }).get(2, TimeUnit.SECONDS);

            Thread.sleep(50);

            queue.submit(() -> {
                secondStart.set(System.nanoTime());
                return CompletableFuture.completedFuture(null);
            }).get(2, TimeUnit.SECONDS);

            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(
                    secondStart.get() - firstStart.get()
            );

            assertTrue(elapsedMs >= intervalMs,
                    "Interval was not respected after idle: " + elapsedMs + "ms");
        }
    }

    @Test
    void shouldRejectRequestsWhenQueueIsFull() throws Exception {
        try (AsyncRequestQueue queue =
                     new AsyncRequestQueue(10_000, "test-queue", 1)) {

            CountDownLatch started = new CountDownLatch(1);
            CompletableFuture<Void> inFlight = new CompletableFuture<>();

            queue.submit(() -> {
                started.countDown();
                return inFlight;
            });

            assertTrue(started.await(2, TimeUnit.SECONDS));

            queue.submit(() -> CompletableFuture.completedFuture(null));

            assertThrows(RejectedExecutionException.class, () ->
                    queue.submit(() -> CompletableFuture.completedFuture(null))
            );

            inFlight.complete(null);
        }
    }

    @Test
    void shouldFailPendingRequestsWhenClosed() throws Exception {
        AsyncRequestQueue queue =
                new AsyncRequestQueue(10_000, "test-queue", 10);

        CountDownLatch started = new CountDownLatch(1);
        CompletableFuture<Void> inFlight = new CompletableFuture<>();

        queue.submit(() -> {
            started.countDown();
            return inFlight;
        });

        assertTrue(started.await(2, TimeUnit.SECONDS));

        CompletableFuture<Void> pending = queue.submit(
                () -> CompletableFuture.completedFuture(null)
        );

        queue.close();

        assertThrows(ExecutionException.class,
                () -> pending.get(2, TimeUnit.SECONDS));

        inFlight.complete(null);
    }

    @Test
    void shouldHandleSupplierException() throws Exception {
        try (AsyncRequestQueue queue =
                     new AsyncRequestQueue(0, "test-queue", 10)) {

            CompletableFuture<Void> result = queue.submit(() -> {
                throw new IllegalStateException("Test exception");
            });

            ExecutionException exception = assertThrows(
                    ExecutionException.class,
                    () -> result.get(2, TimeUnit.SECONDS)
            );

            assertInstanceOf(
                    IllegalStateException.class,
                    exception.getCause()
            );
        }
    }

    @Test
    void shouldHandleFailedFuture() throws Exception {
        try (AsyncRequestQueue queue =
                     new AsyncRequestQueue(0, "test-queue", 10)) {

            CompletableFuture<Void> result = queue.submit(() ->
                    CompletableFuture.failedFuture(
                            new IllegalStateException("HTTP error")
                    )
            );

            ExecutionException exception = assertThrows(
                    ExecutionException.class,
                    () -> result.get(2, TimeUnit.SECONDS)
            );

            assertInstanceOf(
                    IllegalStateException.class,
                    exception.getCause()
            );
        }
    }
}


