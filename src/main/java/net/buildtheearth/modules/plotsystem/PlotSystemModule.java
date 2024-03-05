package net.buildtheearth.modules.plotsystem;

import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.plotsystem.commands.PlotSystemTerraCommand;

public class PlotSystemModule extends Module {


    private static PlotSystemModule instance = null;

    public PlotSystemModule() {
        super("PlotSystem");
    }

    public static PlotSystemModule getInstance() {
        return instance == null ? instance = new PlotSystemModule() : instance;
    }

    @Override
    public void enable() {
        // Check if WorldEdit is enabled
        if (!CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled()) {
            shutdown("§cWorldEdit is not installed.");
            return;
        }

        // Check if HeadDatabase is enabled
        if (!CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled()) {
            shutdown("§cHeadDatabase is not installed.");
            return;
        }

        // TODO add onEnable logic here

        super.enable();
    }

    @Override
    public void registerCommands() {
        registerCommand("plotsystemterra", new PlotSystemTerraCommand());
    }

    @Override
    public void registerListeners() {}
}
