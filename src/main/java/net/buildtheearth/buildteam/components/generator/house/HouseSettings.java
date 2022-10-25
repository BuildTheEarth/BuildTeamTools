package net.buildtheearth.buildteam.components.generator.house;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class HouseSettings {

    @Getter
    @Setter
    private ArrayList<String> wallColor;

    @Getter
    @Setter
    private RoofType roofType;

    @Getter
    @Setter
    private ArrayList<String> roofColor;


    public HouseSettings(){
        this.wallColor = new ArrayList<>();
    }
}
