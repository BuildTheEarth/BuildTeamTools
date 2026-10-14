package net.buildtheearth.buildteamtools.modules.map;

import lombok.Getter;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.map.commands.MapCreateCommand;
import net.buildtheearth.buildteamtools.utils.WikiLinks;
import net.buildtheearth.buildteamtools.utils.io.MapConfig;

public class MapModule extends Module {

    @Getter
    private static MapModule instance;

    @Getter
    private final MapConfig config;

    public MapModule(MapConfig config) {
        super("Map", WikiLinks.ENTRY);
        this.config = config;
        instance = this;
    }

    @Override
    public void registerCommands() {
        new MapCreateCommand().register(this);
    }

    @Override
    public void registerListeners() {
    }
}
