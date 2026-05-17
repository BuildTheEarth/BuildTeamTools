package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import org.bukkit.entity.Player;

public class RailSettings extends Settings {

    public RailSettings(Player player) {
        super(player);
    }

    @Override
    public void setDefaultValues() {
        // Rail Generator v1 does not use configurable settings yet.
    }
}