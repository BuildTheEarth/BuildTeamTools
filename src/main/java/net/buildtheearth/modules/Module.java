package net.buildtheearth.modules;

/**
 * An interface for BuildTeamTools modules
 *
 * @author Noah Husby
 */
public interface Module {

    boolean isEnabled();

    void onEnable();

    void onDisable();

    String getModuleName();

    static Module getInstance() {
        return null;
    }
}
