package net.buildtheearth.buildteamtools.modules.generator.components.rail.generation;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

final class RailPreparationProgress implements Runnable {

    private final Player player;
    private final long maxPercentage;
    private final long updateIntervalTicks;
    private final AtomicReference<BukkitTask> task = new AtomicReference<>();
    private final AtomicLong stageStartPercentage = new AtomicLong();
    private final AtomicLong stageEndPercentage = new AtomicLong();
    private final AtomicLong stageStartedAtMillis = new AtomicLong(System.currentTimeMillis());
    private final AtomicLong stageEstimatedDurationMillis = new AtomicLong(1L);
    private final AtomicLong queuedPercentage = new AtomicLong(-1L);
    private final AtomicLong lastSentPercentage = new AtomicLong(-1L);

    RailPreparationProgress(Player player, long maxPercentage, long updateIntervalTicks) {
        this.player = player;
        this.maxPercentage = maxPercentage;
        this.updateIntervalTicks = updateIntervalTicks;
    }

    void start() {
        if (!canContinue())
            return;

        if (task.get() != null)
            return;

        BukkitTask newTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                BuildTeamTools.getInstance(),
                this,
                0L,
                updateIntervalTicks
        );

        if (!task.compareAndSet(null, newTask))
            newTask.cancel();
    }

    void stop() {
        BukkitTask currentTask = task.getAndSet(null);

        if (currentTask == null)
            return;

        currentTask.cancel();
    }

    void startStage(long startPercentage, long endPercentage, long estimatedDurationMillis) {
        if (!canContinue())
            return;

        long clampedStartPercentage = clamp(startPercentage);
        stageStartPercentage.set(clampedStartPercentage);
        stageEndPercentage.set(clamp(endPercentage));
        stageStartedAtMillis.set(System.currentTimeMillis());
        stageEstimatedDurationMillis.set(Math.max(1L, estimatedDurationMillis));
        update(clampedStartPercentage);
    }

    void completeStage(long percentage) {
        update(percentage);
    }

    void update(long percentage) {
        if (!canContinue())
            return;

        long clampedPercentage = clamp(percentage);

        if (clampedPercentage <= queuedPercentage.get())
            return;

        queuedPercentage.set(clampedPercentage);
        if (clampedPercentage <= lastSentPercentage.get())
            return;

        lastSentPercentage.set(clampedPercentage);
        player.sendActionBar(ChatHelper.getStandardComponent(false, "Generator Progress: %s", clampedPercentage + "%"));
    }

    long scale(int completed, int total, long startPercentage, long endPercentage) {
        if (total <= 0)
            return endPercentage;

        double progress = Math.clamp((double) completed / (double) total, 0D, 1D);
        return startPercentage + Math.round(progress * (endPercentage - startPercentage));
    }

    @Override
    public void run() {
        if (!canContinue()) {
            stop();
            return;
        }

        long currentStageStart = stageStartPercentage.get();
        long currentStageEnd = stageEndPercentage.get();

        if (currentStageEnd <= currentStageStart)
            return;

        long elapsedMillis = Math.max(0L, System.currentTimeMillis() - stageStartedAtMillis.get());
        double progress = Math.clamp((double) elapsedMillis / (double) stageEstimatedDurationMillis.get(), 0D, 0.98D);
        long estimatedPercentage = currentStageStart + (long) Math.floor(progress * (currentStageEnd - currentStageStart));

        if (estimatedPercentage >= currentStageEnd)
            estimatedPercentage = currentStageEnd - 1L;

        update(estimatedPercentage);
    }

    private long clamp(long percentage) {
        return Math.clamp(percentage, 0L, maxPercentage);
    }

    private boolean canContinue() {
        return BuildTeamTools.getInstance().isEnabled()
                && player != null
                && player.isOnline();
    }
}
