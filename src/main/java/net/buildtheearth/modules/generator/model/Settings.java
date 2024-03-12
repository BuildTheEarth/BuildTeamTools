package net.buildtheearth.modules.generator.model;

import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.modules.generator.components.FlagType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class Settings {

    @Getter
    private final Player player;

    @Getter
    private final HashMap<Flag, String> values;

    @Getter
    @Setter
    private Block[][][] blocks;

    public Settings(Player player){
        this.player = player;
        this.values = new HashMap<>();

        setDefaultValues();
    }

    public abstract void setDefaultValues();

    public void setValue(Flag flag, String value){
        String errorMessage = FlagType.validateFlagType(flag, value);
        if(errorMessage != null){
            player.sendMessage(errorMessage);
            return;
        }

        getValues().put(flag, value);
    }
}
