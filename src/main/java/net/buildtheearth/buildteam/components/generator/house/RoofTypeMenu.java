package net.buildtheearth.buildteam.components.generator.house;

import net.buildtheearth.utils.AbstractMenu;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.Liste;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

public class RoofTypeMenu extends AbstractMenu {

    public static String ROOF_COLOR_INV_NAME = "Choose a Roof Color";

    public static int FLAT_ROOF_ITEM_SLOT = 11;
    public static int GABLE_ROOF_ITEM_SLOT = 13;
    public static int FLAT_GABLE_ROOF_ITEM_SLOT = 15;


    public RoofTypeMenu(Player player) {
        super(3, ROOF_COLOR_INV_NAME, player);
    }

    @Override
    protected void setPreviewItems() {

        ItemStack flatRoofItem = Item.create(Material.CARPET, "§bFlat Roof", Liste.createList("", "§eDescription:", "A flat roof for", "skyscrapers using carpets", "or slabs"));
        ItemStack gableRoofItem = Item.create(Material.COBBLESTONE_STAIRS, "§bGable Roof", Liste.createList("", "§eDescription:", "A gable roof for", "residential houses using stairs"));
        ItemStack flatGableRoofItem = Item.create(Material.STEP, "§bFlat Gable Roof", (short) 3, Liste.createList("", "§eDescription:", "A flat gable roof for", "residential houses using slabs"));

        // Set items
        getMenu().getSlot(FLAT_ROOF_ITEM_SLOT).setItem(flatRoofItem);
        getMenu().getSlot(GABLE_ROOF_ITEM_SLOT).setItem(gableRoofItem);
        getMenu().getSlot(FLAT_GABLE_ROOF_ITEM_SLOT).setItem(flatGableRoofItem);

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        // Set click events items
        getMenu().getSlot(FLAT_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            House.playerHouseSettings.get(clickPlayer.getUniqueId()).setRoofType(RoofType.FLAT);

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new RoofColorMenu(clickPlayer);
        }));

        getMenu().getSlot(GABLE_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            House.playerHouseSettings.get(clickPlayer.getUniqueId()).setRoofType(RoofType.STAIRS);

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new RoofColorMenu(clickPlayer);
        }));

        getMenu().getSlot(FLAT_GABLE_ROOF_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            House.playerHouseSettings.get(clickPlayer.getUniqueId()).setRoofType(RoofType.SLABS);

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new RoofColorMenu(clickPlayer);
        }));
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short)15, null))
                .pattern("111111111")
                .pattern("110101011")
                .pattern("111111111")
                .build();
    }
}
