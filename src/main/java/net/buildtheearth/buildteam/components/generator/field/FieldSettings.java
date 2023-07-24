package net.buildtheearth.buildteam.components.generator.field;

import net.buildtheearth.buildteam.components.generator.Settings;
import org.bukkit.entity.Player;

public class FieldSettings extends Settings {

    public FieldSettings(Player player) {
        super(player);
    }

    @Override
    public void setDefaultValues() {
        getValues().put(FieldFlag.BLOCK, "35:4");
    }
}
