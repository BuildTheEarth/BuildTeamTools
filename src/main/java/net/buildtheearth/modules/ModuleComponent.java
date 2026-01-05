package net.buildtheearth.modules;

import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * An interface for BuildTeamTools components.
 * Components are subcategories of modules.
 *
 * @author MineFact
 */
public abstract class ModuleComponent {

    @Getter
    private boolean enabled = false;

    @Getter
    private final String componentName;


    public ModuleComponent(String componentName) {
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
