package net.buildtheearth.modules.miscellaneous.signtextgenerator;

import net.buildtheearth.modules.ModuleComponent;

public class SignTextGenerator extends ModuleComponent {

    private static final String COMPONENT_NAME = "SignTextGenerator";

    public SignTextGenerator() {
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
