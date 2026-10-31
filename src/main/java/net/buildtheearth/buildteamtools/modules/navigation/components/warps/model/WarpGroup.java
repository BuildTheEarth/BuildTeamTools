package net.buildtheearth.buildteamtools.modules.navigation.components.warps.model;

import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.utils.ListUtil;
import net.buildtheearth.buildteamtools.utils.heads.LetterType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpGroup {

    @Getter
    private UUID id = UUID.randomUUID();

    @Getter
    private final BuildTeam buildTeam;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private int slot;

    @Getter
    @Setter
    private String material;

    @Getter
    private final List<Warp> warps;

    // Slot which is used internally by the navigation module when auto slot is enabled
    @Getter
    @Setter
    private int internalSlot = -1;

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

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("id", id.toString());
        json.put("name", name);
        json.put("description", description);
        json.put("slot", slot);
        json.put("material", material);

        return json;
    }

    public ItemStack getMaterialItem() {
        return getMaterialItem(null);
    }

    public ItemStack getMaterialItem(Player player) {
        Component itemName = player == null
                ? Messages.render((Audience) null, Messages.component(MessageKeys.Warp.Menu.ITEM_NAME, Component.text(getName())))
                : Messages.render(player, MessageKeys.Warp.Menu.ITEM_NAME, Component.text(getName()));
        List<Component> lore = getDescriptionLore(player);

        return WarpMaterialItems.create(material, name, itemName, lore, LetterType.WOODEN);
    }


    public List<Component> getDescriptionLore() {
        return getDescriptionLore(null);
    }

    public List<Component> getDescriptionLore(Player player) {
        List<Component> lore = new ArrayList<>(player == null
                ? Messages.loreComponents(MessageKeys.Warp.Menu.DESCRIPTION_LABEL)
                : Messages.loreComponents(player, MessageKeys.Warp.Menu.DESCRIPTION_LABEL));
        if (description != null) {
            lore.addAll(ListUtil.grayComponents(description.split("<br>")));
        }
        return lore;
    }
}
