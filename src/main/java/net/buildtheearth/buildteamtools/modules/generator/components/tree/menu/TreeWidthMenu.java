package net.buildtheearth.buildteamtools.modules.generator.components.tree.menu;

import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.kyori.adventure.text.Component;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeWidth;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.utils.menus.NameListMenu;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TreeWidthMenu extends NameListMenu {

    public TreeWidthMenu(Player player, boolean autoLoad) {
        super(player, Messages.plain(player, MessageKeys.Generator.Tree.WIDTH_TITLE), getTreeWidths(), new TreeTypeMenu(player, false), autoLoad);
    }

    /**
     * Get a list of all tree widths
     */
    private static @NonNull List<MutablePair<ItemStack, String>> getTreeWidths() {
        List<MutablePair<ItemStack, String>> treeWidths = new ArrayList<>();

        treeWidths.add(new MutablePair<>(
                ItemComponents.named(Objects.requireNonNull(XMaterial.LIME_CONCRETE.parseItem()), Component.text("Any")),
                "Any"));

        for (TreeWidth treeWidth : TreeWidth.values())
            treeWidths.add(new MutablePair<>(
                    ItemComponents.of(
                            Objects.requireNonNull(XMaterial.JUNGLE_SAPLING.get()),
                            Component.text(StringUtils.capitalize(treeWidth.getName()))),
                    treeWidth.getName()));

        return treeWidths;
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        // Set click event for next item
        if (canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = GeneratorModule.getInstance().getTree().getPlayerSettings().get(clickPlayer.getUniqueId());

                if (!(settings instanceof TreeSettings treeSettings))
                    return;

                treeSettings.setValue(TreeFlag.WIDTH, selectedNames.getFirst());

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

                GeneratorModule.getInstance().getTree().generate(clickPlayer);
            });
    }
}
