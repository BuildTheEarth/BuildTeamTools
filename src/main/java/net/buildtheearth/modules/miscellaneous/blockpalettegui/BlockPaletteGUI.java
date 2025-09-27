package net.buildtheearth.modules.miscellaneous.blockpalettegui;

import net.buildtheearth.modules.ModuleComponent;

public class BlockPaletteGUI extends ModuleComponent {

    private static final String COMPONENT_NAME = "BlockPaletteGUI";

    public BlockPaletteGUI() {
        super(COMPONENT_NAME);
    }

    @Override
    public void enable() {
        super.enable();
    }

    @Override
    public void disable() {
        if(!isEnabled())
            return;

        super.disable();
    }
}
