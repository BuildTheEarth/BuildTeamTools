package net.buildtheearth.modules.generator.modules.house.menu;

import net.buildtheearth.Main;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.modules.generator.modules.house.HouseFlag;
import net.buildtheearth.modules.generator.modules.house.HouseSettings;
import net.buildtheearth.modules.generator.modules.house.RoofType;
import net.buildtheearth.modules.utils.menus.AbstractMenu;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.Liste;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

public class RoofTypeMenu extends AbstractMenu {

    public static String ROOF_COLOR_INV_NAME = "Choose a Roof Type";

    public static int FLAT_ROOF_ITEM_SLOT = 9;
    public static int GABLE_ROOF_ITEM_SLOT = 11;
    public static int STEEP_SLAB_ROOF_ITEM_SLOT = 13;
    public static int MEDIUM_SLAB_ROOF_ITEM_SLOT = 15;
    public static int FLATTER_SLAB_ROOF_ITEM_SLOT = 17;

    public RoofTypeMenu(Player player) {
        super(3, ROOF_COLOR_INV_NAME, player);
    }

    @Override
    protected void setPreviewItems() {

        ItemStack flatRoofItem = Item.create(Material.CARPET, "§bFlat Roof", Liste.createList("", "§eDescription:", "A flat roof for", "skyscrapers using carpets", "or slabs"));
        ItemStack gableRoofItem = Item.create(Material.COBBLESTONE_STAIRS, "§bGable Roof", Liste.createList("", "§eDescription:", "A gable roof for", "residential houses using stairs"));
        ItemStack steepSlabRoofItem = Item.create(Material.STEP, "§bSteep Slab Roof", (short) 3, Liste.createList("", "§eDescription:", "A gable roof for", "residential houses using the", "steepest way you can","create a roof with slabs"));
        ItemStack mediumSlabRoofItem = Item.create(Material.STEP, "§bMedium Steep Slab Roof", (short) 3, Liste.createList("", "§eDescription:", "A gable roof for", "residential houses by creating", "a medium steep roof with slabs"));
        ItemStack flatterSlabRoofItem = Item.create(Material.STEP, "§bFlatter Slab Roof", (short) 3, Liste.createList("", "§eDescription:", "A flat gable roof for", "residential houses by creating", "a very flat roof with slabs"));

        // Set items
        getMenu().getSlot(FLAT_ROOF_ITEM_SLOT).setItem(flatRoofItem);
        getMenu().getSlot(GABLE_ROOF_ITEM_SLOT).setItem(gableRoofItem);
        getMenu().getSlot(STEEP_SLAB_ROOF_ITEM_SLOT).setItem(steepSlabRoofItem);
        getMenu().getSlot(MEDIUM_SLAB_ROOF_ITEM_SLOT).setItem(mediumSlabRoofItem);
        getMenu().getSlot(FLATTER_SLAB_ROOF_ITEM_SLOT).setItem(flatterSlabRoofItem);

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        // Set click events for the roof type items
        getMenu().getSlot(FLAT_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            performClickAction(clickPlayer, RoofType.FLAT);
        }));
        getMenu().getSlot(GABLE_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            performClickAction(clickPlayer, RoofType.STAIRS);
        }));
        getMenu().getSlot(STEEP_SLAB_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            performClickAction(clickPlayer, RoofType.STEEP_SLABS);
        }));
        getMenu().getSlot(MEDIUM_SLAB_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            performClickAction(clickPlayer, RoofType.MEDIUM_SLABS);
        }));
        getMenu().getSlot(FLATTER_SLAB_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            performClickAction(clickPlayer, RoofType.FLATTER_SLABS);
        }));
    }

    private void performClickAction(Player p, RoofType roofType){
        Settings settings = Main.buildTeamTools.getGenerator().getHouse().getPlayerSettings().get(p.getUniqueId());

        if(!(settings instanceof HouseSettings))
            return;

        HouseSettings houseSettings = (HouseSettings) settings;
        houseSettings.setValue(HouseFlag.ROOF_TYPE, roofType.getType());

        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        new RoofColorMenu(p);
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short)15, null))
                .pattern("111111111")
                .pattern("010101010")
                .pattern("111111111")
                .build();
    }
}
