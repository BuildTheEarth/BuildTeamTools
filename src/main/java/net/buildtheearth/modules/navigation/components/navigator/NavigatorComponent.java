package net.buildtheearth.modules.navigation.components.navigator;

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
        // Check if WorldEdit is enabled
        if (!CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled())
            return;

        // Set the navigation wand to nether star
        WorldEdit.getInstance().getConfiguration().navigationWand = 399;
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
        return Item.create(Material.COMPASS, "§aNavigator");
    }

    public short getSlot() {
        return (short) BuildTeamTools.getInstance().getConfig().getInt(ConfigPaths.NAVIGATOR_ITEM_SLOT);
    }


}
