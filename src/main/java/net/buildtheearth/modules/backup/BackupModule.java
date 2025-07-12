package net.buildtheearth.modules.backup;

import lombok.Getter;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.backup.components.FileTrackerComponent;

public final class BackupModule extends Module {

    private static BackupModule instance = null;

    @Getter
    private FileTrackerComponent fileTrackerComponent;

    public BackupModule() {
        super("Backup");
    }

    @Override
    public void enable() {
        super.enable();

        fileTrackerComponent = new FileTrackerComponent();
    }

    @Override
    public void registerListeners() {
        // TODO
    }

    @Override
    protected void registerCommands() {
        // TODO
    }

    public static BackupModule getInstance() {
        return instance == null ? instance = new BackupModule() : instance;
    }
}
