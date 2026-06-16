package net.buildtheearth.buildteamtools.modules.navigation.components.warps.model;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record MigrationResult(int migratedCount, int failedCount, boolean success, @Nullable String errorMessage) {

    @Contract("_, _ -> new")
    public static @NonNull MigrationResult success(int migratedCount, int failedCount) {
        return new MigrationResult(migratedCount, failedCount, true, null);
    }

    @Contract("_ -> new")
    public static @NonNull MigrationResult failure(String errorMessage) {
        return new MigrationResult(0, 0, false, errorMessage);
    }
}
