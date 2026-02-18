package net.buildtheearth.buildteamtools.modules.navigation.components.navigator;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.WorldEdit;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.ModuleComponent;
import net.buildtheearth.buildteamtools.modules.common.CommonModule;
import net.buildtheearth.buildteamtools.utils.io.ConfigPaths;
import net.buildtheearth.buildteamtools.utils.io.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Objects;

public class NavigatorComponent extends ModuleComponent {


    public NavigatorComponent() {
        super("Navigator");
    }

    @Override
    public void enable() {
        disableWorldEditNavigationWand();

        super.enable();
    }


    private void disableWorldEditNavigationWand(){
        // Check if WorldEdit is enabled
        if(!CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled()
        &&! CommonModule.getInstance().getDependencyComponent().isFastAsyncWorldEditEnabled())
            return;

        try {
            Object config = WorldEdit.getInstance().getConfiguration();

            // Check which version of WorldEdit or FastAsyncWorldEdit is being used
            if (CommonModule.getInstance().getDependencyComponent().isFastAsyncWorldEditEnabled()
            || !CommonModule.getInstance().getDependencyComponent().isLegacyWorldEdit()) {

                // For FastAsyncWorldEdit, set the navigation wand to a String
                Field navigationWandField = config.getClass().getField("navigationWand");
                navigationWandField.set(config, "minecraft:nether_star");

            } else if (CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled()) {

                // For WorldEdit, set the navigation wand to an integer
                Field navigationWandField = config.getClass().getField("navigationWand");
                navigationWandField.set(config, 399);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace(); // Handle the error properly
        }
    }

    /*
    Toggles the navigator on or off based on the current state
    */
    public void toggle(Player player) {
        Inventory inventory = player.getInventory();

        if(!inventory.contains(getItem())) {
            inventory.setItem(getSlot(), getItem());
            ChatHelper.sendSuccessfulMessage(player, "You turned the navigator %s.", "on");
        } else {
            inventory.remove(getItem());
            ChatHelper.sendSuccessfulMessage(player, "You turned the navigator %s.", "off");
        }
    }

    public ItemStack getItem() {
        return Item.edit(Objects.requireNonNull(XMaterial.COMPASS.parseItem()), "§aNavigator");
    }

    public short getSlot() {
        return (short) BuildTeamTools.getInstance().getConfig(ConfigUtil.NAVIGATION).getInt(ConfigPaths.Navigation.NAVIGATOR_ITEM_SLOT);
    }


}
