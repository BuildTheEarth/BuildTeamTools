package net.buildtheearth.buildteamtools.modules.generator.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.common.CommonModule;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.house.House;
import net.buildtheearth.buildteamtools.modules.generator.components.house.HouseSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.house.RoofType;
import net.buildtheearth.buildteamtools.modules.generator.components.house.menu.WallColorMenu;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.Rail;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.menu.RailTypeMenu;
import net.buildtheearth.buildteamtools.modules.generator.components.road.Road;
import net.buildtheearth.buildteamtools.modules.generator.components.road.RoadSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.road.menu.RoadColorMenu;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.Tree;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.menu.TreeTypeMenu;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorCollections;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorComponent;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorType;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class GeneratorMenu extends AbstractMenu {

    public static final int HOUSE_ITEM_SLOT = 9;
    public static final int ROAD_ITEM_SLOT = 11;
    public static final int RAIL_ITEM_SLOT = 13;
    public static final int TREE_ITEM_SLOT = 15;
    public static final int FIELD_ITEM_SLOT = 17;

    public GeneratorMenu(Player player, boolean autoLoad) {
        super(3, Messages.component(MessageKeys.Generator.Menu.TITLE), player, autoLoad);
    }

    @Override
    protected void setPreviewItems() {
        Player player = getMenuPlayer();

        ItemStack houseItem = ItemComponents.of(
                Objects.requireNonNull(XMaterial.BIRCH_DOOR.get()),
                Messages.render(player, MessageKeys.Generator.Menu.HOUSE_NAME),
                Messages.loreComponents(
                        player,
                        MessageKeys.Generator.Menu.HOUSE_LORE,
                        Component.text(String.valueOf(RoofType.values().length)))
        );
        getMenu().getSlot(HOUSE_ITEM_SLOT).setItem(houseItem);

        ItemStack roadItem = ItemComponents.of(
                Objects.requireNonNull(XMaterial.SMOOTH_STONE_SLAB.get()),
                Messages.render(player, MessageKeys.Generator.Menu.ROAD_NAME),
                Messages.loreComponents(player, MessageKeys.Generator.Menu.ROAD_LORE)
        );
        getMenu().getSlot(ROAD_ITEM_SLOT).setItem(roadItem);

        ItemStack railwayItem = ItemComponents.of(
                Objects.requireNonNull(XMaterial.RAIL.get()),
                Messages.render(player, MessageKeys.Generator.Menu.RAIL_NAME),
                Messages.loreComponents(player, MessageKeys.Generator.Menu.RAIL_LORE)
        );
        getMenu().getSlot(RAIL_ITEM_SLOT).setItem(railwayItem);

        if (!CommonModule.getInstance().getDependencyComponent().isSchematicBrushEnabled()) {
            ItemStack treeItem = createTreeItem(
                    Messages.render(player, MessageKeys.Generator.Menu.TREE_DISABLED_SUFFIX),
                    Messages.loreComponents(player, MessageKeys.Generator.Menu.TREE_DISABLED_SCHEMATIC_LORE));
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        } else if (!GeneratorCollections.hasUpdatedGeneratorCollections(getMenuPlayer())) {
            ItemStack treeItem = createTreeItem(
                    Messages.render(player, MessageKeys.Generator.Menu.TREE_DISABLED_SUFFIX),
                    Messages.loreComponents(
                            player,
                            MessageKeys.Generator.Menu.TREE_DISABLED_PACK_LORE,
                            Component.text(Tree.TREE_PACK_VERSION)));
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        } else {
            ItemStack treeItem = createTreeItem(
                    null,
                    Messages.loreComponents(player, MessageKeys.Generator.Menu.TREE_LORE));
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        }

        ItemStack fieldItem = ItemComponents.of(
                Objects.requireNonNull(XMaterial.WHEAT.get()),
                Messages.render(player, MessageKeys.Generator.Menu.FIELD_NAME),
                Messages.loreComponents(player, MessageKeys.Generator.Menu.FIELD_LORE)
        );
        getMenu().getSlot(FIELD_ITEM_SLOT).setItem(fieldItem);

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
        // No async or database items.
    }

    @Override
    protected void setItemClickEventsAsync() {
        getMenu().getSlot(HOUSE_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) ->
                handleHouseClick(clickPlayer, clickInformation.getClickType()));
        getMenu().getSlot(ROAD_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) ->
                handleRoadClick(clickPlayer, clickInformation.getClickType()));
        getMenu().getSlot(RAIL_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) ->
                handleRailClick(clickPlayer, clickInformation.getClickType()));
        getMenu().getSlot(TREE_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) ->
                handleTreeClick(clickPlayer, clickInformation.getClickType()));
        getMenu().getSlot(FIELD_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) ->
                sendMoreInformation(clickPlayer, GeneratorType.FIELD));
    }

    private void handleHouseClick(Player player, ClickType clickType) {
        if (showTutorialForRightClick(player, clickType, GeneratorType.HOUSE))
            return;

        House house = GeneratorModule.getInstance().getHouse();
        house.getPlayerSettings().put(player.getUniqueId(), new HouseSettings(player));

        if (!house.checkForPlayer(player))
            return;

        closeMenuWithClickSound(player);
        new WallColorMenu(player, true);
    }

    private void handleRoadClick(Player player, ClickType clickType) {
        if (showTutorialForRightClick(player, clickType, GeneratorType.ROAD))
            return;

        Road road = GeneratorModule.getInstance().getRoad();
        road.getPlayerSettings().put(player.getUniqueId(), new RoadSettings(player));

        if (!road.checkForPlayer(player))
            return;

        closeMenuWithClickSound(player);
        new RoadColorMenu(player, true);
    }

    private void handleRailClick(Player player, ClickType clickType) {
        if (showTutorialForRightClick(player, clickType, GeneratorType.RAIL)
                || !Permissions.checkPermission(player, Permissions.RAIL_TYPE_MENU))
            return;

        Rail rail = GeneratorModule.getInstance().getRail();
        rail.getPlayerSettings().put(player.getUniqueId(), new RailSettings(player));

        if (!rail.checkForPlayer(player))
            return;

        closeMenuWithClickSound(player);
        new RailTypeMenu(player, true);
    }

    private void handleTreeClick(Player player, ClickType clickType) {
        if (showTutorialForRightClick(player, clickType, GeneratorType.TREE))
            return;

        Tree tree = GeneratorModule.getInstance().getTree();
        tree.getPlayerSettings().put(player.getUniqueId(), new TreeSettings(player));

        if (!tree.checkForPlayer(player))
            return;

        closeMenuWithClickSound(player);
        new TreeTypeMenu(player, true);
    }

    private boolean showTutorialForRightClick(Player player, ClickType clickType, GeneratorType generatorType) {
        if (clickType != ClickType.RIGHT)
            return false;

        sendMoreInformation(player, generatorType);
        return true;
    }

    private void closeMenuWithClickSound(Player player) {
        player.closeInventory();
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
    }

    private ItemStack createTreeItem(@Nullable Component nameSuffix, List<Component> lore) {
        Component name = Messages.render(getMenuPlayer(), MessageKeys.Generator.Menu.TREE_NAME);
        if (nameSuffix != null)
            name = name.append(nameSuffix);
        return ItemComponents.of(
                Objects.requireNonNull(XMaterial.OAK_SAPLING.get()),
                name,
                lore
        );
    }

    private void sendMoreInformation(@NonNull Player clickPlayer, @NonNull GeneratorType generator) {
        GeneratorComponent.sendMoreInformation(clickPlayer, generator);
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu()).item(MenuItems.ITEM_BACKGROUND).pattern(BinaryMask.FULL_PATTERN).pattern("010101010").pattern(BinaryMask.FULL_PATTERN).build();
    }
}
