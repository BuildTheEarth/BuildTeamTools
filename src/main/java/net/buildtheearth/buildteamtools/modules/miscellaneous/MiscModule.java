package net.buildtheearth.buildteamtools.modules.miscellaneous;

import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui.BlockPaletteCommand;
import net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui.BlockPaletteGUI;
import net.buildtheearth.buildteamtools.utils.WikiLinks;

public class MiscModule extends Module {

    private BlockPaletteGUI blockPaletteGUI;
    private static MiscModule instance = null;

    public MiscModule() {
        super("Misc", WikiLinks.MISC);
    }

    public static MiscModule getInstance() {
        return instance == null ? instance = new MiscModule() : instance;
    }

    @Override
    public void enable() {
        super.enable();

        blockPaletteGUI = new BlockPaletteGUI(BuildTeamTools.getInstance());
        blockPaletteGUI.enable();
    }

    @Override
    public void disable() {
        if (!isEnabled()) return;

        if (blockPaletteGUI != null) {
            blockPaletteGUI.disable();
            blockPaletteGUI = null;
        }

        super.disable();
    }

    @Override
    public void registerListeners() {
        // No Listeners
    }

    @Override
    public void registerCommands() {
        new BlockPaletteCommand(() -> blockPaletteGUI.getManager(), BuildTeamTools.getInstance()).register(this);
    }
}
