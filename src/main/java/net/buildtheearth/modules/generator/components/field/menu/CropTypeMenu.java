package net.buildtheearth.modules.generator.components.field.menu;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.components.field.CropType;
import net.buildtheearth.modules.generator.components.field.FieldFlag;
import net.buildtheearth.modules.generator.components.field.FieldSettings;
import net.buildtheearth.modules.generator.menu.GeneratorMenu;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.utils.ListUtil;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jspecify.annotations.NonNull;

public class CropTypeMenu extends AbstractMenu {

    public static String CROP_TYPE_INV_NAME = "Choose a Crop Type";

    private final byte POTATO_CROP_SLOT = 9;
    private final byte WHEAT_CROP_SLOT = 10;
    private final byte CORN_CROP_SLOT = 11;
    private final byte VINEYARD_CROP_SLOT = 12;
    private final byte PEAR_CROP_SLOT = 13;
    private final byte CATTLE_CROP_SLOT = 14;
    private final byte MEADOW_CROP_SLOT = 15;
    private final byte HARVESTED_CROP_SLOT = 16;
    private final byte OTHER_CROP_SLOT = 17;

    private final int BACK_ITEM_SLOT = 18;

    public CropTypeMenu(Player player, boolean autoLoad) {
        super(3, CROP_TYPE_INV_NAME, player, autoLoad);
    }


    @Override
    protected void setPreviewItems() {

        ItemStack potatoItem = Item.create(XMaterial.POTATO.parseMaterial(), "§bPotato", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack wheatItem = Item.create(XMaterial.WHEAT.parseMaterial(), "§bWheat", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack cornItem = Item.create(XMaterial.PUMPKIN_SEEDS.parseMaterial(), "§bCorn", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack vineyardItem = Item.create(XMaterial.VINE.parseMaterial(), "§bVineyard", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack pearItem = Item.create(XMaterial.SLIME_BALL.parseMaterial(), "§bPear", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack cattleItem = Item.create(XMaterial.SPRUCE_FENCE.parseMaterial(), "§bWheat", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack meadowItem = Item.create(XMaterial.SHORT_GRASS.parseMaterial(), "§bMeadow", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack harvestedItem = Item.create(XMaterial.HAY_BLOCK.parseMaterial(), "§bHarvested", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack otherItem = Item.create(XMaterial.DEAD_BUSH.parseMaterial(), "§bOther", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));

        // Set items
        getMenu().getSlot(POTATO_CROP_SLOT).setItem(potatoItem);
        getMenu().getSlot(WHEAT_CROP_SLOT).setItem(wheatItem);
        getMenu().getSlot(CORN_CROP_SLOT).setItem(cornItem);
        getMenu().getSlot(VINEYARD_CROP_SLOT).setItem(vineyardItem);
        getMenu().getSlot(PEAR_CROP_SLOT).setItem(pearItem);
        getMenu().getSlot(CATTLE_CROP_SLOT).setItem(cattleItem);
        getMenu().getSlot(MEADOW_CROP_SLOT).setItem(meadowItem);
        getMenu().getSlot(HARVESTED_CROP_SLOT).setItem(harvestedItem);
        getMenu().getSlot(OTHER_CROP_SLOT).setItem(otherItem);

        setBackItem(BACK_ITEM_SLOT, new GeneratorMenu(getMenuPlayer(), false));

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click events for the crop type items
        getMenu().getSlot(POTATO_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.POTATO);
                return;
            }
            performClickAction(clickPlayer, CropType.POTATO);
        }));

        getMenu().getSlot(WHEAT_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.WHEAT);
                return;
            }
            performClickAction(clickPlayer, CropType.WHEAT);
        }));

        getMenu().getSlot(CORN_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.CORN);
                return;
            }
            performClickAction(clickPlayer, CropType.CORN);
        }));

        getMenu().getSlot(VINEYARD_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.VINEYARD);
                return;
            }
            performClickAction(clickPlayer, CropType.VINEYARD);
        }));

        getMenu().getSlot(PEAR_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.PEAR);
                return;
            }
            performClickAction(clickPlayer, CropType.PEAR);
        }));

        getMenu().getSlot(CATTLE_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.CATTLE);
                return;
            }
            performClickAction(clickPlayer, CropType.CATTLE);
        }));

        getMenu().getSlot(MEADOW_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.MEADOW);
                return;
            }
            performClickAction(clickPlayer, CropType.MEADOW);
        }));

        getMenu().getSlot(HARVESTED_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.HARVESTED);
                return;
            }
            performClickAction(clickPlayer, CropType.HARVESTED);
        }));

        getMenu().getSlot(OTHER_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                CropStageMenu.sendMoreInformation(clickPlayer, CropType.OTHER);
                return;
            }
            performClickAction(clickPlayer, CropType.OTHER);
        }));


    }

    private void performClickAction(@NonNull Player p, CropType cropType) {
        Settings settings = GeneratorModule.getInstance().getField().getPlayerSettings().get(p.getUniqueId());

        if (!(settings instanceof FieldSettings))
            return;

        FieldSettings fieldSettings = (FieldSettings) settings;
        fieldSettings.setValue(FieldFlag.CROP_TYPE, cropType.getIdentifier());

        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        if (cropType.hasStages()) {
            new CropStageMenu(p, cropType);
            return;
        }
        if (cropType.equals(CropType.CATTLE) || cropType.equals(CropType.MEADOW)) {
            new FenceTypeMenu(p, true);
            return;
        }

        GeneratorModule.getInstance().getField().generate(p);
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern("111111111")
                .pattern("000000000")
                .pattern("011111111")
                .build();
    }
}
