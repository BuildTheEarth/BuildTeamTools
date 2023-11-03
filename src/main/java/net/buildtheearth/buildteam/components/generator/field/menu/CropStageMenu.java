package net.buildtheearth.buildteam.components.generator.field.menu;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.Settings;
import net.buildtheearth.buildteam.components.generator.field.Crop;
import net.buildtheearth.buildteam.components.generator.field.CropStage;
import net.buildtheearth.buildteam.components.generator.field.Field;
import net.buildtheearth.buildteam.components.generator.field.FieldFlag;
import net.buildtheearth.buildteam.components.generator.field.FieldSettings;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.Liste;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

public class CropStageMenu extends AbstractMenu {

    public static String CROP_TYPE_INV_NAME = "Choose a Crop Stage";


    private final Crop crop;
    private final byte STAGE_ONE_SLOT = 11;
    private final byte STAGE_TWO_SLOT = 15;

    private final int BACK_ITEM_SLOT = 18;

    public CropStageMenu(Player player, Crop crop) {
        super(3, CROP_TYPE_INV_NAME, player);
        this.crop = crop;
    }


    @Override
    protected void setPreviewItems() {
        Field field = Main.buildTeamTools.getGenerator().getField();
        Crop crop = Crop.getByIdentifier(field.getPlayerSettings().get(getMenuPlayer().getUniqueId()).getValues().get(FieldFlag.CROP));

        ItemStack itemOne = Item.create(Material.BARRIER);
        ItemStack itemTwo = Item.create(Material.BARRIER);

        switch (crop) {
            case POTATO:
                itemOne = Item.create(Material.GRASS, "§bLow", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
                itemTwo = Item.create(Material.LONG_GRASS, "§bTall", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
                break;
            case WHEAT:
                itemOne = Item.create(Material.BIRCH_FENCE, "§bLight", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
                itemTwo = Item.create(Material.DARK_OAK_FENCE, "§bDark", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
                break;
            case CORN:
                itemOne = Item.create(Material.GRASS, "§bHarvested", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
                itemTwo = Item.create(Material.LONG_GRASS, "§bTall", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
                break;
            case HARVESTED:
            case OTHER:
                itemOne = Item.create(Material.DEAD_BUSH, "§bDry", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
                itemTwo = Item.create(Material.WATER_BUCKET, "§bWet", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
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
                sendMoreInformation(clickPlayer, Crop.POTATO);
                return;
            }

            CropStage cropstage = CropStage.FALLBACK;
            switch (crop) {
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
                sendMoreInformation(clickPlayer, Crop.WHEAT);
                return;
            }

            CropStage cropstage = CropStage.FALLBACK;
            switch (crop) {
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
        Settings settings = Main.buildTeamTools.getGenerator().getField().getPlayerSettings().get(p.getUniqueId());

        if (!(settings instanceof FieldSettings))
            return;

        FieldSettings fieldSettings = (FieldSettings) settings;
        fieldSettings.setValue(FieldFlag.TYPE, cropStage.getIdentifier());

        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        Main.getBuildTeam().getGenerator().getField().generate(p);
    }

    private void sendMoreInformation(Player p, Crop crop) {
        switch (crop) {
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
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short) 15, null))
                .pattern("111111111")
                .pattern("110111011")
                .pattern("011111111")
                .build();
    }
}
