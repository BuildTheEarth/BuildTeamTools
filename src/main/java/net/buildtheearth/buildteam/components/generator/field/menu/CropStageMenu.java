package net.buildtheearth.buildteam.components.generator.field.menu;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.Settings;
import net.buildtheearth.buildteam.components.generator.field.Crop;
import net.buildtheearth.buildteam.components.generator.field.CropStage;
import net.buildtheearth.buildteam.components.generator.field.FieldFlag;
import net.buildtheearth.buildteam.components.generator.field.FieldSettings;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.Liste;
import net.buildtheearth.utils.menus.AbstractMenu;
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
    private final byte STAGE_ONE_SLOT = 9;
    private final byte STAGE_TWO_SLOT = 10;

    public CropStageMenu(Player player, Crop crop) {
        super(3, CROP_TYPE_INV_NAME, player);
        player.sendMessage("TEST " + crop.toString());
        this.crop = crop;
    }


    @Override
    protected void setPreviewItems() {

        ItemStack itemOne = Item.create(Material.BARRIER);
        ItemStack itemTwo = Item.create(Material.BARRIER);;

        switch (crop) {
            case POTATO:
                itemOne = Item.create(Material.STONE_PLATE, "&bLow", Liste.createList("", "§8Leftclick to select", "§8Rightclick for more information"));
                itemTwo = Item.create(Material.STONE, "&bTall", Liste.createList("", "§8Leftclick to select", "§8Rightclick for more information"));
                break;
            case WHEAT:
                itemOne = Item.create(Material.BIRCH_FENCE, "&bLight", Liste.createList("", "§8Leftclick to select", "§8Rightclick for more information"));
                itemTwo = Item.create(Material.DARK_OAK_FENCE, "&bDark", Liste.createList("", "§8Leftclick to select", "§8Rightclick for more information"));
                break;
            case CORN:
                itemOne = Item.create(Material.STONE_PLATE, "&bHarvested", Liste.createList("", "§8Leftclick to select", "§8Rightclick for more information"));
                itemTwo = Item.create(Material.STONE, "&bTall", Liste.createList("", "§8Leftclick to select", "§8Rightclick for more information"));
                break;
            case HARVESTED:
            case OTHER:
                itemOne = Item.create(Material.DEAD_BUSH, "&bDry", Liste.createList("", "§8Leftclick to select", "§8Rightclick for more information"));
                itemTwo = Item.create(Material.WATER_BUCKET, "&bWet", Liste.createList("", "§8Leftclick to select", "§8Rightclick for more information"));
                break;
        }


        // Set items
        getMenu().getSlot(STAGE_ONE_SLOT).setItem(itemOne);
        getMenu().getSlot(STAGE_TWO_SLOT).setItem(itemTwo);


        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        // Set click events for the crop type items
        getMenu().getSlot(STAGE_ONE_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if(clickInformation.getClickType().equals(ClickType.RIGHT)) {
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
            if(clickInformation.getClickType().equals(ClickType.RIGHT)) {
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

    private void performClickAction(Player p, CropStage cropStage){
        Settings settings = Main.buildTeamTools.getGenerator().getField().getPlayerSettings().get(p.getUniqueId());

        if(!(settings instanceof FieldSettings))
            return;

        FieldSettings fieldSettings = (FieldSettings) settings;
        fieldSettings.setValue(FieldFlag.TYPE, cropStage.getIdentifier());

        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        Main.getBuildTeam().getGenerator().getField().generate(p);
    }

    private void sendMoreInformation(Player p, Crop crop) {

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
