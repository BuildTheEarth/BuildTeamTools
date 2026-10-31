package net.buildtheearth.buildteamtools.modules.navigation.components.navigator;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.WorldEdit;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.ModuleComponent;
import net.buildtheearth.buildteamtools.modules.common.CommonModule;
import net.buildtheearth.buildteamtools.utils.io.NavigationConfig;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.kyori.adventure.audience.Audience;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Field;
import java.util.Objects;

public class NavigatorComponent extends ModuleComponent {

    private final NamespacedKey navigatorKey;

    private final NavigationConfig config;

    public NavigatorComponent(NavigationConfig config) {
        super("Navigator");
        this.config = config;
        this.navigatorKey = new NamespacedKey(BuildTeamTools.getInstance(), "navigator");
    }

    @Override
    public void enable() {
        disableWorldEditNavigationWand();

        super.enable();
    }


    private void disableWorldEditNavigationWand() {
        // Check if WorldEdit is enabled
        if (!CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled()
                && !CommonModule.getInstance().getDependencyComponent().isFastAsyncWorldEditEnabled())
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
            BuildTeamTools.getInstance().getComponentLogger().error("Error configuring navigator wand", e);
        }
    }

    /*
    Toggles the navigator on or off based on the current state
    */
    public void toggle(@NonNull Player player) {
        Inventory inventory = player.getInventory();

        if (!inventory.contains(getItem())) {
            inventory.setItem(getSlot(), getItem());
            player.sendMessage(Messages.component(
                    MessageKeys.Navigator.TOGGLED,
                    Messages.component(MessageKeys.Navigator.STATE_ON)));
        } else {
            inventory.remove(getItem());
            player.sendMessage(Messages.component(
                    MessageKeys.Navigator.TOGGLED,
                    Messages.component(MessageKeys.Navigator.STATE_OFF)));
        }
    }

    public ItemStack getItem() {
        ItemStack item = ItemComponents.displayName(
                Objects.requireNonNull(XMaterial.COMPASS.parseItem()),
                Messages.render((Audience) null, Messages.component(MessageKeys.Navigator.ITEM_NAME)));
        item.editPersistentDataContainer(pdc -> pdc.set(navigatorKey, PersistentDataType.BOOLEAN, true));
        return item;
    }

    public boolean isItem(@NonNull ItemStack item) {
        return item.getPersistentDataContainer().has(navigatorKey, PersistentDataType.BOOLEAN);
    }

    public short getSlot() {
        return (short) config.navigatorHotbarItem().navSlot();
    }


}
