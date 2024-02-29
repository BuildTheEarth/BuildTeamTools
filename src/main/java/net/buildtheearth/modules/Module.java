package net.buildtheearth.modules;

import lombok.Getter;

/**
 * An interface for BuildTeamTools modules
 *
 * @author Noah Husby
 */
public abstract class Module {

    @Getter
    private boolean enabled = false;

    @Getter
    private String moduleName;


    public Module(String moduleName) {
        this.moduleName = moduleName;
    }


    public void onEnable(){
        registerCommands();
        registerListeners();

        enabled = true;
    }

    public void onDisable(){
        enabled = false;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void registerCommands(){}

    public void registerListeners(){}


}
