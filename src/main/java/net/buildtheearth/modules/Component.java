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
    private final String componentName;


    public Component(String componentName) {
        this.componentName = componentName;

        enable();
    }


    public void enable(){
        enabled = true;
    }

    public void disable(){
        enabled = false;
    }
}
