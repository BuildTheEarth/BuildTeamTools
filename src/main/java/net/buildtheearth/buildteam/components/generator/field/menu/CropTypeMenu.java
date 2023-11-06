package net.buildtheearth.buildteam.components.generator.field.menu;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.GeneratorMenu;
import net.buildtheearth.buildteam.components.generator.Settings;
import net.buildtheearth.buildteam.components.generator.field.Crop;
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

        ItemStack potatoItem = Item.create(Material.POTATO_ITEM, "§bPotato", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack wheatItem = Item.create(Material.WHEAT, "§bWheat", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack cornItem = Item.create(Material.PUMPKIN_SEEDS, "§bCorn", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack vineyardItem = Item.create(Material.VINE, "§bVineyard", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack pearItem = Item.create(Material.SLIME_BALL, "§bPear", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack cattleItem = Item.create(Material.SPRUCE_FENCE, "§bWheat", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack meadowItem = Item.create(Material.GRASS, "§bMeadow", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack harvestedItem = Item.create(Material.HAY_BLOCK, "§bHarvested", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));
        ItemStack otherItem = Item.create(Material.DEAD_BUSH, "§bOther", Liste.createList("", "§8Left-click to select", "§8Right-click for more information"));

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
                sendMoreInformation(clickPlayer, Crop.POTATO);
                return;
            }
            performClickAction(clickPlayer, Crop.POTATO);
        }));

        getMenu().getSlot(WHEAT_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, Crop.WHEAT);
                return;
            }
            performClickAction(clickPlayer, Crop.WHEAT);
        }));

        getMenu().getSlot(CORN_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, Crop.CORN);
                return;
            }
            performClickAction(clickPlayer, Crop.CORN);
        }));

        getMenu().getSlot(VINEYARD_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, Crop.VINEYARD);
                return;
            }
            performClickAction(clickPlayer, Crop.VINEYARD);
        }));

        getMenu().getSlot(PEAR_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, Crop.PEAR);
                return;
            }
            performClickAction(clickPlayer, Crop.PEAR);
        }));

        getMenu().getSlot(CATTLE_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, Crop.CATTLE);
                return;
            }
            performClickAction(clickPlayer, Crop.CATTLE);
        }));

        getMenu().getSlot(MEADOW_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, Crop.MEADOW);
                return;
            }
            performClickAction(clickPlayer, Crop.MEADOW);
        }));

        getMenu().getSlot(HARVESTED_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, Crop.HARVESTED);
                return;
            }
            performClickAction(clickPlayer, Crop.HARVESTED);
        }));

        getMenu().getSlot(OTHER_CROP_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, Crop.OTHER);
                return;
            }
            performClickAction(clickPlayer, Crop.OTHER);
        }));


    }

    private void performClickAction(Player p, Crop crop) {
        Settings settings = Main.buildTeamTools.getGenerator().getField().getPlayerSettings().get(p.getUniqueId());

        if (!(settings instanceof FieldSettings))
            return;

        FieldSettings fieldSettings = (FieldSettings) settings;
        fieldSettings.setValue(FieldFlag.CROP_TYPE, crop.getIdentifier());

        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        if (crop.hasStages()) {
            new CropStageMenu(p, crop);
            return;
        }
        if (crop.equals(Crop.CATTLE) || crop.equals(Crop.MEADOW)) {
            new FenceTypeMenu(p, true);
            return;
        }

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
            case VINEYARD:
                p.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Crop-Types#vineyard-requires-lines");
                break;
            case PEAR:
                p.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Crop-Types#pear-requires-lines");
                break;
            case CATTLE:
                p.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Crop-Types#cattle");
                break;
            case MEADOW:
                p.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Crop-Types#meadow");
                break;
        }
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short) 15, null))
                .pattern("111111111")
                .pattern("000000000")
                .pattern("011111111")
                .build();
    }
}
