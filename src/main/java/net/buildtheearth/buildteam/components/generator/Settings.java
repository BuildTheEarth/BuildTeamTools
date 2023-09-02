package net.buildtheearth.buildteam.components.generator;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class Settings {

    @Getter
    private Player player;

    @Getter
    private HashMap<Object, String> values;

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
        getValues().put(flag, value);
    }
}
