package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

final class RailPreparationProgress implements Runnable {

    private final Player player;
    private final long maxPercentage;
    private final long updateIntervalTicks;
    private volatile BukkitTask task;
    private volatile long stageStartPercentage;
    private volatile long stageEndPercentage;
    private volatile long stageStartedAtMillis = System.currentTimeMillis();
    private volatile long stageEstimatedDurationMillis = 1L;
    private volatile long queuedPercentage = -1L;
    private volatile long lastSentPercentage = -1L;

    RailPreparationProgress(Player player, long maxPercentage, long updateIntervalTicks) {
        this.player = player;
        this.maxPercentage = maxPercentage;
        this.updateIntervalTicks = updateIntervalTicks;
    }

    void start() {
        if (!canContinue())
            return;

        if (task != null)
            return;

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                BuildTeamTools.getInstance(),
                this,
                0L,
                updateIntervalTicks
        );
    }

    void stop() {
        BukkitTask currentTask = task;

        if (currentTask == null)
            return;

        currentTask.cancel();
        task = null;
    }

    void startStage(long startPercentage, long endPercentage, long estimatedDurationMillis) {
        if (!canContinue())
            return;

        stageStartPercentage = clamp(startPercentage);
        stageEndPercentage = clamp(endPercentage);
        stageStartedAtMillis = System.currentTimeMillis();
        stageEstimatedDurationMillis = Math.max(1L, estimatedDurationMillis);
        update(stageStartPercentage);
    }

    void completeStage(long percentage) {
        update(percentage);
    }

    void update(long percentage) {
        if (!canContinue())
            return;

        long clampedPercentage = clamp(percentage);

        if (clampedPercentage <= queuedPercentage)
            return;

        queuedPercentage = clampedPercentage;
        if (clampedPercentage <= lastSentPercentage)
            return;

        lastSentPercentage = clampedPercentage;
        player.sendActionBar(ChatHelper.getStandardComponent(false, "Generator Progress: %s", clampedPercentage + "%"));
    }

    long scale(int completed, int total, long startPercentage, long endPercentage) {
        if (total <= 0)
            return endPercentage;

        double progress = Math.max(0D, Math.min(1D, (double) completed / (double) total));
        return startPercentage + Math.round(progress * (endPercentage - startPercentage));
    }

    @Override
    public void run() {
        if (!canContinue()) {
            stop();
            return;
        }

        long currentStageStart = stageStartPercentage;
        long currentStageEnd = stageEndPercentage;

        if (currentStageEnd <= currentStageStart)
            return;

        long elapsedMillis = Math.max(0L, System.currentTimeMillis() - stageStartedAtMillis);
        double progress = Math.min(0.98D, (double) elapsedMillis / (double) stageEstimatedDurationMillis);
        long estimatedPercentage = currentStageStart + (long) Math.floor(progress * (currentStageEnd - currentStageStart));

        if (estimatedPercentage >= currentStageEnd)
            estimatedPercentage = currentStageEnd - 1L;

        update(estimatedPercentage);
    }

    private long clamp(long percentage) {
        return Math.max(0L, Math.min(maxPercentage, percentage));
    }

    private boolean canContinue() {
        return BuildTeamTools.getInstance().isEnabled()
                && player != null
                && player.isOnline();
    }
}
