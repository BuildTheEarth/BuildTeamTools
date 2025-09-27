package net.buildtheearth.modules.miscellaneous;

import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.miscellaneous.blockpalettegui.BlockPaletteGUI;
import net.buildtheearth.modules.miscellaneous.signtextgenerator.SignTextGenerator;

public class MiscModule extends Module {

    private BlockPaletteGUI blockPaletteGUI;
    private SignTextGenerator signTextGenerator;

    private static MiscModule instance = null;

    public MiscModule() {
        super("Misc");
    }

    public static MiscModule getInstance() {
        return instance == null ? instance = new MiscModule() : instance;
    }



    @Override
    public void enable() {
        super.enable();

        blockPaletteGUI = new BlockPaletteGUI();
        signTextGenerator = new SignTextGenerator();
    }

    @Override
    public void disable() {
        if(!isEnabled())
            return;

        blockPaletteGUI.disable();
        signTextGenerator.disable();

        super.disable();
    }

    @Override
    public void registerListeners() {
        //super.registerListeners(new Listener());
    }
}
