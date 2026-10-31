package net.buildtheearth.buildteamtools.modules.navigation.components.warps.model;

import com.alpsbte.alpslib.utils.item.Item;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.heads.HeadFactory;
import net.buildtheearth.buildteamtools.utils.heads.LetterType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Shared material/icon resolution for {@link Warp} and {@link WarpGroup} menu items.
 */
final class WarpMaterialItems {

    private WarpMaterialItems() {
    }

    static ItemStack create(
            String material,
            String name,
            Component itemName,
            List<Component> lore,
            LetterType fallbackLetterType
    ) {
        if (material == null) {
            return HeadFactory.letter(fallbackLetterType, name.charAt(0), itemName, lore);
        }
        if (Warp.isMinecraftTextureUrl(material)) {
            return ItemComponents.named(
                    Item.createCustomHeadTextureURL(material, " ", null),
                    itemName,
                    lore);
        }

        Material matchedMaterial = Material.matchMaterial(material.split(":")[0]);
        if (matchedMaterial == null) {
            return HeadFactory.letter(LetterType.STONE, name.charAt(0), itemName, lore);
        }
        if (!material.contains(":")) {
            return ItemComponents.of(matchedMaterial, itemName, lore);
        }
        return ItemComponents.named(
                Item.create(matchedMaterial, " ", Short.parseShort(material.split(":")[1]), null),
                itemName,
                lore);
    }
}
