package net.buildtheearth.modules.navigation.components.navigator;

import com.cryptomorin.xseries.XMaterial;
import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.WorldEdit;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Component;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.io.ConfigPaths;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class NavigatorComponent extends Component {


    public NavigatorComponent() {
        super("Navigator");
    }

    @Override
    public void enable() {
        disableWorldEditNavigationWand();

        super.enable();
    }


    private void disableWorldEditNavigationWand(){
        try {
            Object config = WorldEdit.getInstance().getConfiguration();

            // Check which version of WorldEdit or FastAsyncWorldEdit is being used
            if (CommonModule.getInstance().getDependencyComponent().isFastAsyncWorldEditEnabled()) {

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
            player.sendMessage(ChatHelper.successful("You turned the navigator %s.", "on"));
        } else {
            inventory.remove(getItem());
            player.sendMessage(ChatHelper.successful("You turned the navigator %s.", "off"));
        }
    }

    public ItemStack getItem() {
        return Item.create(XMaterial.COMPASS.parseMaterial(), "§aNavigator");
    }

    public short getSlot() {
        return (short) BuildTeamTools.getInstance().getConfig().getInt(ConfigPaths.NAVIGATOR_ITEM_SLOT);
    }


}
