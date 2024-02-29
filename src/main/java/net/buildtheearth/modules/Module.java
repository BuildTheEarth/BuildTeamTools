package net.buildtheearth.modules;

/**
 * An interface for BuildTeamTools modules
 *
 * @author Noah Husby
 */
public interface Module {
    void onEnable();

    void onDisable();

    String getModuleName();
}
