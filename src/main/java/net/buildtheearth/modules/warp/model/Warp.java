package net.buildtheearth.modules.warp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.modules.utils.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@AllArgsConstructor
public class Warp {

    @Getter
    private final UUID id;

    @Getter @Setter
    private WarpGroup warpGroup;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String countryCode;

    @Getter
    private String countryCodeType;

    @Getter @Setter
    private String address;

    @Getter @Setter
    private AddressType addressType;

    @Getter @Setter
    private String material;

    @Getter @Setter
    private String worldName;

    @Getter @Setter
    private double lat;

    @Getter @Setter
    private double lon;

    @Getter @Setter
    private double y;

    @Getter @Setter
    private float yaw;

    @Getter @Setter
    private float pitch;

    @Getter @Setter
    private boolean isHighlight;

    /** Create a warp with a random warp ID. */
    public Warp(WarpGroup warpGroup, String name, String countryCode, String countryCodeType, String address, AddressType addressType, String material, String worldName, double lat, double lon, double y, float yaw, float pitch, boolean isHighlight) {
        this(UUID.randomUUID(), warpGroup, name, countryCode, countryCodeType, address, addressType, material, worldName, lat, lon, y, yaw, pitch, isHighlight);
    }

    public ItemStack getMaterialItem() {
        String itemName = "§6§l" + getName();
        ArrayList<String> lore = null;

        if(address != null){
            lore = new ArrayList<>(Arrays.asList("", "§eAddress:"));
            lore.addAll(ListUtil.createList(Utils.splitStringByLineLength(address, 30, ", ")));
        }

        if(material == null)
            return  MenuItems.getLetterHead(
                    name.substring(0, 1),
                    MenuItems.LetterType.STONE,
                    itemName,
                    lore
            );
        else if(material.startsWith("http://textures.minecraft.net/texture/"))
            return Item.createCustomHeadBase64(material, itemName, lore);

        Material material = Material.matchMaterial(this.material.split(":")[0]);

        if(material == null)
            return MenuItems.getLetterHead(
                    name.substring(0, 1),
                    MenuItems.LetterType.STONE,
                    itemName,
                    lore
            );
        else if(!this.material.contains(":"))
            return Item.create(material, itemName, lore);
        else
            return Item.create(material, itemName, Short.parseShort(this.material.split(":")[1]), lore);

    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();

        json.put("id", id.toString());
        json.put("warpGroupID", warpGroup.getId().toString());
        json.put("name", name);
        json.put("countryCode", countryCode);
        json.put("countryCodeType", countryCodeType);
        json.put("worldName", worldName);
        json.put("lat", lat);
        json.put("lon", lon);
        json.put("y", y);
        json.put("yaw", yaw);
        json.put("pitch", pitch);
        json.put("isHighlight", isHighlight);

        if(address != null)
            json.put("address", address);

        if(addressType != null)
            json.put("addressType", addressType.toString());

        if(material != null)
            json.put("material", material);

        return json;
    }

    public enum AddressType {
        BUILDING("Building", "Empire State Building, 350, 5th Avenue, Midtown South, Manhattan, New York County, City of New York, New York, 10118, United States"),
        STREET("Street", "West 33rd Street, Midtown South, Manhattan, New York County, City of New York, New York, 10001, United States"),
        CITY("City", "Manhattan, New York County, City of New York, New York, United States"),
        STATE("State", "New York, United States"),
        COUNTRY("Country", "United States"),
        CUSTOM("Custom", "Custom Address");

        @Getter
        private final String name;
        @Getter
        private final String example;

        AddressType(String name, String example) {
            this.name = name;
            this.example = example;
        }

        public static AddressType fromValue(String value) {
            for (AddressType type : values())
                if (type.toString().equalsIgnoreCase(value))
                    return type;

            return AddressType.CITY;
        }
    }
}


