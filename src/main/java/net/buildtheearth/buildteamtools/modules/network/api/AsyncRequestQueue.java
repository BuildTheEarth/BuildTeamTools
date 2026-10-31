package net.buildtheearth.buildteamtools.modules.network.api;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Executes asynchronous requests in FIFO order while enforcing a minimum
 * interval between consecutive request starts.
 *
 * <p>Requests do not need to finish before the next request can start.
 * Each submitted operation has its own future, which completes when the
 * underlying asynchronous operation completes.</p>
 *
 * <p>Each instance owns a dedicated daemon scheduler and an independent
 * request queue.</p>
 */
public final class AsyncRequestQueue implements AutoCloseable {

    private final long intervalNanos;
    private final int maxQueueSize;

    private final ScheduledExecutorService scheduler;
    private final Queue<QueuedRequest<?>> queue = new ArrayDeque<>();

    private long nextAllowedStartNanos;

    private boolean dispatchScheduled;
    private boolean closed;

    /**
     * Creates an asynchronous request queue.
     *
     * @param intervalMs minimum delay between request starts, in milliseconds
     * @param threadName name of the scheduler thread
     * @param maxQueueSize maximum number of waiting requests
     * @throws IllegalArgumentException if the interval is negative
     *         or the queue capacity is not positive
     * @throws NullPointerException if the thread name is null
     */
    public AsyncRequestQueue(
            long intervalMs,
            @NotNull String threadName,
            int maxQueueSize
    ) {
        Objects.requireNonNull(threadName, "threadName");

        if (intervalMs < 0) {
            throw new IllegalArgumentException(
                    "Interval must not be negative"
            );
        }

        if (maxQueueSize <= 0) {
            throw new IllegalArgumentException(
                    "Queue capacity must be positive"
            );
        }

        if (threadName.isBlank()) {
            throw new IllegalArgumentException(
                    "Thread name must not be blank"
            );
        }

        this.intervalNanos = TimeUnit.MILLISECONDS.toNanos(intervalMs);
        this.maxQueueSize = maxQueueSize;

        // Allow the first request to start immediately.
        this.nextAllowedStartNanos = System.nanoTime();

        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, threadName);
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Submits an asynchronous operation to the queue.
     *
     * @param operation operation to execute when its turn arrives
     * @param <T> result type of the asynchronous operation
     * @return a future completed with the operation's result or exception
     * @throws NullPointerException if the operation is null
     * @throws RejectedExecutionException if the queue is closed or full,
     *         or if the scheduler cannot accept the dispatch
     */
    public <T> @NotNull CompletableFuture<T> submit(
            @NotNull Supplier<CompletableFuture<T>> operation
    ) {
        Objects.requireNonNull(operation, "operation");

        CompletableFuture<T> result = new CompletableFuture<>();
        QueuedRequest<T> request = new QueuedRequest<>(operation, result);

        RejectedExecutionException schedulingFailure = null;
        List<QueuedRequest<?>> rejectedRequests = List.of();

        synchronized (this) {
            if (closed) {
                throw new RejectedExecutionException(
                        "Request queue is closed"
                );
            }

            if (queue.size() >= maxQueueSize) {
                throw new RejectedExecutionException(
                        "Request queue is full"
                );
            }

            queue.add(request);

            if (!dispatchScheduled) {
                try {
                    scheduleDispatchLocked();
                } catch (RejectedExecutionException e) {
                    schedulingFailure = e;
                    rejectedRequests = drainQueueLocked();
                }
            }
        }

        if (schedulingFailure != null) {
            completeRequestsExceptionally(
                    rejectedRequests,
                    schedulingFailure
            );

            throw schedulingFailure;
        }

        return result;
    }

    /**
     * Schedules the next dispatch using the earliest allowed start time.
     * Must be called while holding this instance's monitor.
     */
    private void scheduleDispatchLocked() {
        long delayNanos = Math.max(
                0L,
                nextAllowedStartNanos - System.nanoTime()
        );

        scheduler.schedule(
                this::dispatchNext,
                delayNanos,
                TimeUnit.NANOSECONDS
        );

        dispatchScheduled = true;
    }

    /**
     * Starts the next queued operation and schedules the following dispatch.
     * Request completion does not block queue progression.
     */
    private void dispatchNext() {
        QueuedRequest<?> request;

        synchronized (this) {
            if (closed) {
                dispatchScheduled = false;
                return;
            }

            request = queue.poll();

            if (request == null) {
                dispatchScheduled = false;
                return;
            }

            // Record the next allowed start time when this request starts.
            nextAllowedStartNanos = System.nanoTime() + intervalNanos;
        }

        execute(request);

        RejectedExecutionException schedulingFailure = null;
        List<QueuedRequest<?>> rejectedRequests = List.of();

        synchronized (this) {
            if (closed) {
                dispatchScheduled = false;
                return;
            }

            if (queue.isEmpty()) {
                dispatchScheduled = false;
            } else {
                try {
                    scheduleDispatchLocked();
                } catch (RejectedExecutionException e) {
                    dispatchScheduled = false;
                    schedulingFailure = e;
                    rejectedRequests = drainQueueLocked();
                }
            }
        }

        if (schedulingFailure != null) {
            completeRequestsExceptionally(
                    rejectedRequests,
                    schedulingFailure
            );
        }
    }

    /**
     * Executes a queued operation and forwards its outcome to its future.
     *
     * @param request queued operation to execute
     * @param <T> operation result type
     */
    private <T> void execute(QueuedRequest<T> request) {
        try {
            CompletableFuture<T> operationFuture =
                    request.operation().get();

            if (operationFuture == null) {
                throw new NullPointerException(
                        "Operation returned a null future"
                );
            }

            operationFuture.whenComplete((value, error) -> {
                if (error != null) {
                    request.result().completeExceptionally(error);
                } else {
                    request.result().complete(value);
                }
            });
        } catch (Exception e) {
            request.result().completeExceptionally(e);
        }
    }

    /**
     * Removes all waiting requests.
     * Must be called while holding this instance's monitor.
     *
     * @return the removed requests
     */
    private List<QueuedRequest<?>> drainQueueLocked() {
        List<QueuedRequest<?>> requests = new ArrayList<>();

        QueuedRequest<?> request;

        while ((request = queue.poll()) != null) {
            requests.add(request);
        }

        return requests;
    }

    /**
     * Completes a collection of requests exceptionally.
     *
     * @param requests requests to fail
     * @param exception failure to propagate
     */
    private void completeRequestsExceptionally(
            List<QueuedRequest<?>> requests,
            RejectedExecutionException exception
    ) {
        for (QueuedRequest<?> request : requests) {
            request.result().completeExceptionally(exception);
        }
    }

    /**
     * Cancels waiting requests and shuts down the scheduler.
     *
     * <p>An operation that has already started may still complete.
     * Its underlying HTTP request is not automatically cancelled.</p>
     */
    @Override
    public void close() {
        List<QueuedRequest<?>> pendingRequests;

        synchronized (this) {
            if (closed) {
                return;
            }

            closed = true;
            dispatchScheduled = false;

            pendingRequests = drainQueueLocked();

            scheduler.shutdownNow();
        }

        RejectedExecutionException exception =
                new RejectedExecutionException(
                        "Request queue was shut down"
                );

        completeRequestsExceptionally(
                pendingRequests,
                exception
        );
    }

    /**
     * Holds a queued operation and its associated result future.
     *
     * @param <T> operation result type
     */
    private record QueuedRequest<T>(
            Supplier<CompletableFuture<T>> operation,
            CompletableFuture<T> result
    ) {
    }
}
