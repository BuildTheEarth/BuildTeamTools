package net.buildtheearth.buildteamtools.modules.network.api;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
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

    private final long intervalMs;
    private final int maxQueueSize;

    private final ScheduledExecutorService scheduler;
    private final Queue<QueuedRequest<?>> queue = new ArrayDeque<>();

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

        this.intervalMs = intervalMs;
        this.maxQueueSize = maxQueueSize;

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
     * @throws RejectedExecutionException if the queue is closed or full
     */
    public <T> @NotNull CompletableFuture<T> submit(
            @NotNull Supplier<CompletableFuture<T>> operation
    ) {
        CompletableFuture<T> result = new CompletableFuture<>();

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

            queue.add(new QueuedRequest<>(operation, result));

            if (!dispatchScheduled) {
                dispatchScheduled = true;
                scheduler.schedule(
                        this::dispatchNext,
                        0,
                        TimeUnit.MILLISECONDS
                );
            }
        }

        return result;
    }

    /**
     * Starts the next queued operation and schedules the following dispatch.
     * Request completion does not block queue progression.
     */
    private void dispatchNext() {
        QueuedRequest<?> request;

        synchronized (this) {
            request = queue.poll();

            if (request == null) {
                dispatchScheduled = false;
                return;
            }
        }

        execute(request);

        synchronized (this) {
            if (queue.isEmpty()) {
                dispatchScheduled = false;
            } else {
                scheduler.schedule(
                        this::dispatchNext,
                        intervalMs,
                        TimeUnit.MILLISECONDS
                );
            }
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
                    request.operation.get();

            if (operationFuture == null) {
                throw new NullPointerException(
                        "Operation returned a null future"
                );
            }

            operationFuture.whenComplete((value, error) -> {
                if (error != null) {
                    request.result.completeExceptionally(error);
                } else {
                    request.result.complete(value);
                }
            });
        } catch (Exception e) {
            request.result.completeExceptionally(e);
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
        synchronized (this) {
            if (closed) {
                return;
            }

            closed = true;

            RejectedExecutionException exception =
                    new RejectedExecutionException(
                            "Request queue was shut down"
                    );

            QueuedRequest<?> request;

            while ((request = queue.poll()) != null) {
                request.result.completeExceptionally(exception);
            }

            scheduler.shutdownNow();
        }
    }

    /**
     * Holds a queued operation and its associated result future.
     *
     * @param <T> operation result type
     */
    private static final class QueuedRequest<T> {
        private final Supplier<CompletableFuture<T>> operation;
        private final CompletableFuture<T> result;

        private QueuedRequest(
                Supplier<CompletableFuture<T>> operation,
                CompletableFuture<T> result
        ) {
            this.operation = operation;
            this.result = result;
        }
    }
}
