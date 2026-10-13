package net.buildtheearth.buildteamtools.modules.map;

import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.map.commands.MapCreateCommand;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.utils.WikiLinks;

public class MapModule extends Module {

    public MapModule() {
        super("Map", WikiLinks.ENTRY, NetworkModule.getInstance());
    }

    @Override
    public void registerCommands() {
        new MapCreateCommand().register(this);
    }

    @Override
    public void registerListeners() {
    }
}
