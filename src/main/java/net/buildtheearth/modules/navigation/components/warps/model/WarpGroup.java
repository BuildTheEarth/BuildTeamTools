package net.buildtheearth.modules.navigation.components.warps.model;

import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.utils.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpGroup {

    @Getter
    private UUID id = UUID.randomUUID();

    @Getter
    private BuildTeam buildTeam;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private int slot;

    @Getter @Setter
    private String material;

    @Getter
    private final List<Warp> warps;


    public WarpGroup(BuildTeam buildTeam, String name, String description, int slot, String material) {
        this.buildTeam = buildTeam;
        this.name = name;
        this.description = description;
        this.slot = slot;
        this.material = material;
        this.warps = new ArrayList<>();
    }

    public WarpGroup(UUID id, BuildTeam buildTeam, String name, String description, int slot, String material) {
        this.id = id;
        this.buildTeam = buildTeam;
        this.name = name;
        this.description = description;
        this.slot = slot;
        this.material = material;
        this.warps = new ArrayList<>();
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();

        json.put("id", id.toString());
        json.put("name", name);
        json.put("description", description);
        json.put("slot", slot);
        json.put("material", material);

        return json;
    }

    public ItemStack getMaterialItem() {
        String itemName = "§6§l" + getName();
        ArrayList<String> lore = getDescriptionLore();

        if(material == null)
            return  CustomHeads.getLetterHead(
                    name.substring(0, 1),
                    CustomHeads.LetterType.WOODEN,
                    itemName,
                    lore
            );
        else if(material.startsWith("http://textures.minecraft.net/texture/"))
            return Item.createCustomHeadTextureURL(material, itemName, lore);

        Material material = Material.matchMaterial(this.material.split(":")[0]);

        if(material == null)
            return CustomHeads.getLetterHead(
                    name.substring(0, 1),
                    CustomHeads.LetterType.STONE,
                    itemName,
                    lore
            );
        else if(!this.material.contains(":"))
            return Item.create(material, itemName, lore);
        else
            return Item.create(material, itemName, Short.parseShort(this.material.split(":")[1]), lore);

    }


    public ArrayList<String> getDescriptionLore() {
        return new ArrayList<String>() {{
            add("");
            add("§eDescription:");
            addAll(ListUtil.createList(description.split("<br>")));
        }};
    }
}
