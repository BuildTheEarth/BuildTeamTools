package net.buildtheearth.modules;

import lombok.Getter;

/**
 * An interface for BuildTeamTools components.
 * Components are subcategories of modules.
 *
 * @author MineFact
 */
public abstract class Component {

    @Getter
    private boolean enabled = false;

    @Getter
    private String componentName;


    public Component(String componentName) {
        this.componentName = componentName;

        onEnable();
    }


    public void onEnable(){
        enabled = true;
    }

    public void onDisable(){
        enabled = false;
    }

    public boolean isEnabled(){
        return enabled;
    }
}
