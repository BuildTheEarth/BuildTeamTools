package net.buildtheearth.buildteamtools.modules.generator.components.tree;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class TreeScripts {

    public static void treescript_v_1_0(@NonNull Player p, @NonNull Tree tree) {
        Settings settings = tree.getPlayerSettings().get(p.getUniqueId());
        TreeType treeType = (TreeType) settings.getValues().get(TreeFlag.TYPE);
        if (treeType == null) treeType = TreeType.ANY;
        String height = String.valueOf(settings.getValues().get(TreeFlag.HEIGHT));
        TreeWidth treeWidth = (TreeWidth) settings.getValues().get(TreeFlag.WIDTH);
        if (treeWidth == null) treeWidth = TreeWidth.ANY;

        // In case the player is holding no item, give him a diamond sword
        if (p.getInventory().getItemInMainHand().getType() == Material.AIR)
            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Item.create(XMaterial.DIAMOND_SWORD.get()));

        if (treeType.getName().equalsIgnoreCase("any")) {
            p.chat("/schbr $GeneratorCollections/treepack/* -place:bottom -yoff:2");
        } else if (height.equalsIgnoreCase("null") || height.equalsIgnoreCase("any")) {
            p.chat("/schbr $GeneratorCollections/treepack/" + treeType.getName() + "/* -place:bottom -yoff:2");
        } else if (treeWidth.getName().equalsIgnoreCase("any")) {
            p.chat("/schbr $GeneratorCollections/treepack/" + treeType.getName() + "/" + height + "/* -place:bottom -yoff:2");
        } else {
            p.chat("/schbr $GeneratorCollections/treepack/" + treeType.getName() + "/" + height + "/"
                    + treeWidth.getName() + "/* -place:bottom -yoff:2");
        }
    }
}
