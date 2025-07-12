package net.buildtheearth.modules.backup;

import net.buildtheearth.modules.Module;

public final class BackupModule extends Module {

    private static BackupModule instance = null;

    public BackupModule() {
        super("Backup");
    }

    @Override
    public void registerListeners() {
        // TODO
    }

    @Override
    protected void registerCommands() {
        // TODO
    }

    // Getters & Setters

    public static BackupModule getInstance() {
        return instance == null ? instance = new BackupModule() : instance;
    }
}
