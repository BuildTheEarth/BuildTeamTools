package net.buildtheearth.modules.generator.components.field.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.components.field.*;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.ListUtil;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.WikiLinks;
import net.buildtheearth.utils.menus.AbstractMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;

public class CropStageMenu extends AbstractMenu {

    private static final String CROP_TYPE_INV_NAME = "Choose a Crop Stage";

    private final CropType cropType;
    private static final byte STAGE_ONE_SLOT = 11;
    private static final byte STAGE_TWO_SLOT = 15;
    private static final int BACK_ITEM_SLOT = 18;

    public CropStageMenu(Player player, CropType cropType) {
        super(3, CROP_TYPE_INV_NAME, player);
        this.cropType = cropType;
    }


    @Override
    protected void setPreviewItems() {
        Field field = GeneratorModule.getInstance().getField();
        CropType cropType = (CropType) field.getPlayerSettings().get(getMenuPlayer().getUniqueId()).getValues().get(FieldFlag.CROP_TYPE);

        ItemStack itemOne = Item.create(XMaterial.BARRIER.parseMaterial());
        ItemStack itemTwo = Item.create(XMaterial.BARRIER.parseMaterial());

        ArrayList<String> typeLore = ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information");
        switch (cropType) {
            case POTATO:
                itemOne = new Item(XMaterial.SHORT_GRASS.parseItem()).setDisplayName("§bLow").setLore(typeLore).build();
                itemTwo = new Item(XMaterial.TALL_GRASS.parseItem()).setDisplayName("§bTall").setLore(typeLore).build();
                break;
            case WHEAT:
                itemOne = Item.create(XMaterial.BIRCH_FENCE.parseMaterial(), "§bLight", typeLore);
                itemTwo = Item.create(XMaterial.DARK_OAK_FENCE.parseMaterial(), "§bDark", typeLore);
                break;
            case CORN:
                itemOne = new Item(XMaterial.SHORT_GRASS.parseItem()).setDisplayName("§bHarvested").setLore(typeLore).build();
                itemTwo = new Item(XMaterial.TALL_GRASS.parseItem()).setDisplayName("§bTall").setLore(typeLore).build();
                break;
            case OTHER:
                itemOne = Item.create(XMaterial.DEAD_BUSH.parseMaterial(), "§bDry", typeLore);
                itemTwo = Item.create(XMaterial.WATER_BUCKET.parseMaterial(), "§bWet", typeLore);
                break;
        }


        // Set items
        getMenu().getSlot(STAGE_ONE_SLOT).setItem(itemOne);
        getMenu().getSlot(STAGE_TWO_SLOT).setItem(itemTwo);

        setBackItem(BACK_ITEM_SLOT, new CropTypeMenu(getMenuPlayer(), false));


        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click events for the crop type items
        getMenu().getSlot(STAGE_ONE_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, CropType.POTATO);
                return;
            }

            CropStage cropstage = CropStage.FALLBACK;
            switch (cropType) {
                case POTATO:
                    cropstage = CropStage.LOW;
                    break;
                case WHEAT:
                    cropstage = CropStage.LIGHT;
                    break;
                case CORN:
                    cropstage = CropStage.HARVESTED;
                    break;
                case OTHER:
                    cropstage = CropStage.DRY;
                    break;
            }

            performClickAction(clickPlayer, cropstage);
        }));

        getMenu().getSlot(STAGE_TWO_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, CropType.WHEAT);
                return;
            }

            CropStage cropstage = CropStage.FALLBACK;
            cropstage = switch (cropType) {
                case POTATO, CORN -> CropStage.TALL;
                case WHEAT -> CropStage.DARK;
                case HARVESTED, OTHER -> CropStage.WET;
                default -> cropstage;
            };

            performClickAction(clickPlayer, cropstage);
        }));
    }

    private void performClickAction(Player p, CropStage cropStage) {
        Settings settings = GeneratorModule.getInstance().getField().getPlayerSettings().get(p.getUniqueId());

        if (!(settings instanceof FieldSettings))
            return;

        FieldSettings fieldSettings = (FieldSettings) settings;
        fieldSettings.setValue(FieldFlag.CROP_STAGE, cropStage);

        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        GeneratorModule.getInstance().getField().generate(p);
    }

    public static void sendMoreInformation(Player p, @NonNull CropType cropType) {
        switch (cropType) {
            case POTATO:
                p.sendMessage(Component.text(WikiLinks.Gen.Field.CROP_POTATO, NamedTextColor.RED));
                break;
            case CORN:
                p.sendMessage(Component.text(WikiLinks.Gen.Field.CROP_CORN, NamedTextColor.RED));
                break;
            case WHEAT:
                p.sendMessage(Component.text(WikiLinks.Gen.Field.CROP_WHEAT, NamedTextColor.RED));
                break;
            case HARVESTED:
                p.sendMessage(Component.text(WikiLinks.Gen.Field.CROP_HARVESTED, NamedTextColor.RED));
                break;
            case OTHER:
                p.sendMessage(Component.text(WikiLinks.Gen.Field.CROP_OTHER, NamedTextColor.RED));
                break;
            case VINEYARD:
                p.sendMessage(Component.text(WikiLinks.Gen.Field.CROP_VINEYARD, NamedTextColor.RED));
                break;
            case PEAR:
                p.sendMessage(Component.text(WikiLinks.Gen.Field.CROP_PEAR, NamedTextColor.RED));
                break;
            case CATTLE:
                p.sendMessage(Component.text(WikiLinks.Gen.Field.CROP_CATTLE, NamedTextColor.RED));
                break;
            case MEADOW:
                p.sendMessage(Component.text(WikiLinks.Gen.Field.CROP_MEADOW, NamedTextColor.RED));
                break;
        }

        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern("111111111")
                .pattern("110111011")
                .pattern("011111111")
                .build();
    }
}
