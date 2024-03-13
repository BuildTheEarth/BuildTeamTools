package net.buildtheearth.modules.generator.components.field.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.components.field.*;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.ListUtil;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

public class CropStageMenu extends AbstractMenu {

    public static String CROP_TYPE_INV_NAME = "Choose a Crop Stage";


    private final CropType cropType;
    private final byte STAGE_ONE_SLOT = 11;
    private final byte STAGE_TWO_SLOT = 15;

    private final int BACK_ITEM_SLOT = 18;

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

        switch (cropType) {
            case POTATO:
                itemOne = new Item(XMaterial.SHORT_GRASS.parseItem()).setDisplayName("§bLow").setLore(ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information")).build();
                itemTwo = new Item(XMaterial.TALL_GRASS.parseItem()).setDisplayName("§bTall").setLore(ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information")).build();
                break;
            case WHEAT:
                itemOne = Item.create(XMaterial.BIRCH_FENCE.parseMaterial(), "§bLight", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));
                itemTwo = Item.create(XMaterial.DARK_OAK_FENCE.parseMaterial(), "§bDark", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));
                break;
            case CORN:
                itemOne = new Item(XMaterial.SHORT_GRASS.parseItem()).setDisplayName("§bHarvested").setLore(ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information")).build();
                itemTwo = new Item(XMaterial.TALL_GRASS.parseItem()).setDisplayName("§bTall").setLore(ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information")).build();
                break;
            case HARVESTED:
            case OTHER:
                itemOne = Item.create(XMaterial.DEAD_BUSH.parseMaterial(), "§bDry", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));
                itemTwo = Item.create(XMaterial.WATER_BUCKET.parseMaterial(), "§bWet", ListUtil.createList("", "§8Left-click to select", "§8Right-click for more information"));
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
                case HARVESTED:
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
            switch (cropType) {
                case POTATO:
                case CORN:
                    cropstage = CropStage.TALL;
                    break;
                case WHEAT:
                    cropstage = CropStage.DARK;
                    break;
                case HARVESTED:
                case OTHER:
                    cropstage = CropStage.WET;
                    break;
            }

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

    private void sendMoreInformation(Player p, CropType cropType) {
        switch (cropType) {
            case POTATO:
                p.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Crop-Types#potato-requires-lines");
                break;
            case CORN:
                p.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Crop-Types#corn");
                break;
            case WHEAT:
                p.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Crop-Types#wheat");
                break;
            case HARVESTED:
                p.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Crop-Types#harvested-requires-lines");
                break;
            case OTHER:
                p.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Crop-Types#other-requires-lines");
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
