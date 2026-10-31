package net.buildtheearth.buildteamtools.modules.generator.components.house.menu;

import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.house.HouseFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.house.HouseSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.house.RoofType;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.Objects;

public class RoofTypeMenu extends AbstractMenu {

    public static final int FLAT_ROOF_ITEM_SLOT = 9;
    public static final int GABLE_ROOF_ITEM_SLOT = 11;
    public static final int STEEP_SLAB_ROOF_ITEM_SLOT = 13;
    public static final int MEDIUM_SLAB_ROOF_ITEM_SLOT = 15;
    public static final int FLATTER_SLAB_ROOF_ITEM_SLOT = 17;

    private static final int BACK_ITEM_SLOT = 18;

    public RoofTypeMenu(Player player, boolean autoLoad) {
        super(3, Messages.component(MessageKeys.Generator.House.ROOF_TYPE_TITLE), player, autoLoad);
    }

    @Override
    protected void setPreviewItems() {

        Player player = getMenuPlayer();
        ItemStack flatRoofItem = ItemComponents.of(Objects.requireNonNull(XMaterial.WHITE_CARPET.get()), Messages.render(player, MessageKeys.Generator.House.ROOF_FLAT_NAME), Messages.loreComponents(player, MessageKeys.Generator.House.ROOF_FLAT_LORE));
        ItemStack gableRoofItem = ItemComponents.of(Objects.requireNonNull(XMaterial.COBBLESTONE_STAIRS.get()), Messages.render(player, MessageKeys.Generator.House.ROOF_GABLE_NAME), Messages.loreComponents(player, MessageKeys.Generator.House.ROOF_GABLE_LORE));
        ItemStack steepSlabRoofItem = ItemComponents.of(Objects.requireNonNull(XMaterial.COBBLESTONE_SLAB.get()), Messages.render(player, MessageKeys.Generator.House.ROOF_STEEP_NAME), Messages.loreComponents(player, MessageKeys.Generator.House.ROOF_STEEP_LORE));
        ItemStack mediumSlabRoofItem = ItemComponents.of(Objects.requireNonNull(XMaterial.COBBLESTONE_SLAB.get()), Messages.render(player, MessageKeys.Generator.House.ROOF_MEDIUM_NAME), Messages.loreComponents(player, MessageKeys.Generator.House.ROOF_MEDIUM_LORE));
        ItemStack flatterSlabRoofItem = ItemComponents.of(Objects.requireNonNull(XMaterial.COBBLESTONE_SLAB.get()), Messages.render(player, MessageKeys.Generator.House.ROOF_FLATTER_NAME), Messages.loreComponents(player, MessageKeys.Generator.House.ROOF_FLATTER_LORE));

        // Set items
        getMenu().getSlot(FLAT_ROOF_ITEM_SLOT).setItem(flatRoofItem);
        getMenu().getSlot(GABLE_ROOF_ITEM_SLOT).setItem(gableRoofItem);
        getMenu().getSlot(STEEP_SLAB_ROOF_ITEM_SLOT).setItem(steepSlabRoofItem);
        getMenu().getSlot(MEDIUM_SLAB_ROOF_ITEM_SLOT).setItem(mediumSlabRoofItem);
        getMenu().getSlot(FLATTER_SLAB_ROOF_ITEM_SLOT).setItem(flatterSlabRoofItem);

        setBackItem(BACK_ITEM_SLOT, new WallColorMenu(getMenuPlayer(), false));

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click events for the roof type items
        getMenu().getSlot(FLAT_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) ->
                performClickAction(clickPlayer, RoofType.FLAT)));
        getMenu().getSlot(GABLE_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) ->
                performClickAction(clickPlayer, RoofType.STAIRS)));
        getMenu().getSlot(STEEP_SLAB_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) ->
                performClickAction(clickPlayer, RoofType.STEEP_SLABS)));
        getMenu().getSlot(MEDIUM_SLAB_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) ->
                performClickAction(clickPlayer, RoofType.MEDIUM_SLABS)));
        getMenu().getSlot(FLATTER_SLAB_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) ->
                performClickAction(clickPlayer, RoofType.FLATTER_SLABS)));
    }

    private void performClickAction(Player p, RoofType roofType) {
        Settings settings = GeneratorModule.getInstance().getHouse().getPlayerSettings().get(p.getUniqueId());

        if (!(settings instanceof HouseSettings houseSettings))
            return;

        houseSettings.setValue(HouseFlag.ROOF_TYPE, roofType.getType());

        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        new RoofColorMenu(p, true);
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern(BinaryMask.FULL_PATTERN)
                .pattern("010101010")
                .pattern("011111111")
                .build();
    }
}
