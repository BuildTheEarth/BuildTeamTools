package net.buildtheearth.buildteamtools.modules.navigation.components.warps.model;

import com.alpsbte.alpslib.utils.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.ListUtil;
import net.buildtheearth.buildteamtools.utils.Utils;
import net.buildtheearth.buildteamtools.utils.heads.HeadFactory;
import net.buildtheearth.buildteamtools.utils.heads.LetterType;
import net.buildtheearth.model.GeographicalCoordinate;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class Warp {

    @Getter
    private final UUID id;

    @Getter
    @Setter
    private WarpGroup warpGroup;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String countryCode;

    @Getter
    private String countryCodeType;

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private AddressType addressType;

    @Getter
    @Setter
    private String material;

    @Getter
    @Setter
    private String worldName;

    @Getter
    @Setter
    private GeographicalCoordinate coordinate;

    @Getter
    @Setter
    private double y;

    @Getter
    @Setter
    private float yaw;

    @Getter
    @Setter
    private float pitch;

    @Getter
    @Setter
    private boolean isHighlight;

    /**
     * Create a warp with a random warp ID.
     */
    public Warp(WarpGroup warpGroup, String name, String countryCode, String countryCodeType, String address,
                AddressType addressType, String material, String worldName, GeographicalCoordinate coordinate, double y,
                float yaw,
                float pitch, boolean isHighlight) {
        this(UUID.randomUUID(), warpGroup, name, countryCode, countryCodeType, address, addressType, material, worldName,
                coordinate,
                y, yaw, pitch, isHighlight);
    }

    public ItemStack getMaterialItem() {
        return getMaterialItem(null);
    }

    public ItemStack getMaterialItem(Player player) {
        Component itemName = player == null
                ? Messages.render((Audience) null, Messages.component(MessageKeys.Warp.Menu.ITEM_NAME, Component.text(getName())))
                : Messages.render(player, MessageKeys.Warp.Menu.ITEM_NAME, Component.text(getName()));
        List<Component> lore = null;

        if (address != null) {
            lore = new ArrayList<>(player == null
                    ? Messages.loreComponents(MessageKeys.Warp.Menu.ADDRESS_LABEL)
                    : Messages.loreComponents(player, MessageKeys.Warp.Menu.ADDRESS_LABEL));
            lore.addAll(ListUtil.grayComponents(Utils.splitStringByLineLength(address, 30, ", ")));
        }

        if (material == null)
            return HeadFactory.letter(
                    LetterType.STONE,
                    name.charAt(0),
                    itemName,
                    lore
            );
        else if (material.startsWith("http://textures.minecraft.net/texture/"))
            return ItemComponents.named(
                    Item.createCustomHeadTextureURL(material, " ", null),
                    itemName,
                    lore);

        Material matchedMaterial = Material.matchMaterial(this.material.split(":")[0]);

        if (matchedMaterial == null)
            return HeadFactory.letter(
                    LetterType.STONE,
                    name.charAt(0),
                    itemName,
                    lore
            );
        else if (!this.material.contains(":"))
            return ItemComponents.of(matchedMaterial, itemName, lore);
        else
            return ItemComponents.named(
                    Item.create(matchedMaterial, " ", Short.parseShort(this.material.split(":")[1]), null),
                    itemName,
                    lore);

    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("id", id.toString());
        json.put("warpGroupID", warpGroup.getId().toString());
        json.put("name", name);
        json.put("countryCode", countryCode);
        json.put("countryCodeType", countryCodeType);
        json.put("worldName", worldName);
        json.put("lat", coordinate.latitude());
        json.put("lon", coordinate.longitude());
        json.put("y", y);
        json.put("yaw", yaw);
        json.put("pitch", pitch);
        json.put("isHighlight", isHighlight);

        if (address != null)
            json.put("address", address);

        if (addressType != null)
            json.put("addressType", addressType.toString());

        if (material != null)
            json.put("material", material);

        return json;
    }

    public enum AddressType {
        BUILDING("Building", "Empire State Building, 350, 5th Avenue, Midtown South, Manhattan, New York County, City of New " +
                "York, New York, 10118, United States"),
        STREET("Street", "West 33rd Street, Midtown South, Manhattan, New York County, City of New York, New York, 10001, " +
                "United States"),
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
