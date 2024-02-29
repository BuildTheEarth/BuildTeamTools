package net.buildtheearth.modules.navigation.components.warps.menu;

import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.api.OpenStreetMapAPI;
import net.buildtheearth.modules.utils.*;
import net.buildtheearth.modules.utils.geo.CoordinateConversion;
import net.buildtheearth.modules.utils.menus.AbstractMenu;
import net.buildtheearth.modules.navigation.components.warps.model.Warp;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class WarpEditMenu extends AbstractMenu {

    public static String WARP_UPDATE_INV_NAME = "Configure the Warp";


    public static int WARP_SLOT = 4;
    public static int LOCATION_SLOT = 19;
    public static int NAME_SLOT = 20;
    public static int GROUP_SLOT = 21;
    public static int ADDRESS_TYPE_SLOT = 22;
    public static int MATERIAL_SLOT = 23;
    public static int HIGHLIGHT_SLOT = 24;
    public static int DELETE_SLOT = 25;
    public static int CONFIRM_SLOT = 35;

    private final Warp warp;
    private final boolean alreadyExists;

    /** In this menu the player can update a warp.
     * This can be used for example to change the name of a warp in the {@link WarpMenu}.
     *
     * @param player  The player that is viewing the menu.
     * @param warp The warp that is being updated.
     */
    public WarpEditMenu(Player player, Warp warp, boolean alreadyExists) {
        super(4, WARP_UPDATE_INV_NAME, player);

        this.warp = warp;
        this.alreadyExists = alreadyExists;

        if(!this.alreadyExists){
            NAME_SLOT = 18;
            GROUP_SLOT = 20;
            HIGHLIGHT_SLOT = 22;
            ADDRESS_TYPE_SLOT = 24;
            MATERIAL_SLOT = 26;
        }
    }


    @Override
    protected void setMenuItemsAsync() {
        // Set the confirmation item
        getMenu().getSlot(CONFIRM_SLOT).setItem(MenuItems.getCheckmarkItem(alreadyExists ? "§aUpdate" : "§aCreate"));

        // Set the warp item
        getMenu().getSlot(WARP_SLOT).setItem(warp.getMaterialItem());

        // Set the location item if the warp already exists. Otherwise, the location is set automatically on creation.
        if(alreadyExists){
            ArrayList<String> locationLore = ListUtil.createList("", "§eWorld: §7" + warp.getWorldName(), "§eLatitude: §7" + warp.getLat(), "§eLongitude: §7" + warp.getLon(), "§eElevation: §7" + warp.getY());
            getMenu().getSlot(LOCATION_SLOT).setItem(Item.create(Material.COMPASS, "§6§lChange Location", locationLore));

        }

        // Set the name item
        ArrayList<String> nameLore = ListUtil.createList("", "§eCurrent Name: ", warp.getName());
        getMenu().getSlot(NAME_SLOT).setItem(Item.create(Material.NAME_TAG, "§6§lChange Name", nameLore));

        // Set the group item
        ArrayList<String> groupLore = ListUtil.createList("", "§eCurrent Group: ", warp.getWarpGroup().getName());
        getMenu().getSlot(GROUP_SLOT).setItem(MenuItems.getLetterHead(
                warp.getWarpGroup().getName().substring(0, 1),
                MenuItems.LetterType.WOODEN,
                "§6§lChange Warp Group",
                groupLore
        ));

        // Set the address type item
        ArrayList<String> addressTypeLore = ListUtil.createList("", "§eAddress Type: ", warp.getAddressType() == null ? "§7City" : warp.getAddressType().getName());
        getMenu().getSlot(ADDRESS_TYPE_SLOT).setItem(Item.create(Material.PAPER, "§6§lChange Address Type", addressTypeLore));

        // Set the material item
        ArrayList<String> materialLore = ListUtil.createList("", "§eMaterial: ", warp.getMaterial() == null ? "§7Default" : warp.getMaterial());
        getMenu().getSlot(MATERIAL_SLOT).setItem(new Item(warp.getMaterialItem()).setDisplayName("§6§lChange Material").setLore(materialLore).build());

        // Set the highlight item
        ArrayList<String> highlightLore = ListUtil.createList("", "§eIs Highlight: ", "" + warp.isHighlight());
        getMenu().getSlot(HIGHLIGHT_SLOT).setItem(Item.create(Material.NETHER_STAR, warp.isHighlight() ? "§6§lMake Normal" : "§6§lMake Highlight", highlightLore));

        // Set the delete item
        if(alreadyExists)
            getMenu().getSlot(DELETE_SLOT).setItem(Item.create(Material.BARRIER, "§c§lDelete Warp", ListUtil.createList("", "§8Click to delete the warp.")));
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click event for the confirmation item
        getMenu().getSlot(CONFIRM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

            if(alreadyExists)
                NetworkModule.getInstance().getBuildTeam().updateWarp(clickPlayer, warp);
            else
                NetworkModule.getInstance().getBuildTeam().createWarp(clickPlayer, warp);
        });

        // Set click event for the location item
        getMenu().getSlot(LOCATION_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

            // Get the geographic coordinates of the player's location.
            Location location = clickPlayer.getLocation();
            double[] coordinates = CoordinateConversion.convertToGeo(location.getX(), location.getZ());

            //Get the country belonging to the coordinates
            CompletableFuture<String[]> future = OpenStreetMapAPI.getCountryFromLocationAsync(coordinates);

            future.thenAccept(result -> {
                String regionName = result[0];
                String countryCodeCCA2 = result[1].toUpperCase();

                //Check if the team owns this region/country
                boolean ownsRegion = NetworkModule.getInstance().ownsRegion(regionName, countryCodeCCA2);

                if(!ownsRegion) {
                    clickPlayer.sendMessage(ChatHelper.error("This team does not own the country %s!", result[0]));
                    return;
                }

                warp.setCountryCode(countryCodeCCA2);
                warp.setWorldName(location.getWorld().getName());
                warp.setY(location.getY());
                warp.setLat(coordinates[0]);
                warp.setLon(coordinates[1]);
                warp.setYaw(location.getYaw());
                warp.setPitch(location.getPitch());

                clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

                new WarpEditMenu(clickPlayer, warp, alreadyExists);
            }).exceptionally(e -> {
                clickPlayer.sendMessage(ChatHelper.error("An error occurred while changing the location of the warp!"));
                e.printStackTrace();
                return null;
            });
        });


        // Set click event for the name item
        getMenu().getSlot(NAME_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AnvilGUI.Builder()
                    .onClose(player -> {
                        player.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

                        new WarpEditMenu(clickPlayer, warp, alreadyExists);
                    })
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != AnvilGUI.Slot.OUTPUT)
                            return Collections.emptyList();

                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        return Arrays.asList(
                                AnvilGUI.ResponseAction.close(),
                                AnvilGUI.ResponseAction.run(() -> {
                                    warp.setName(stateSnapshot.getText());
                                    new WarpEditMenu(clickPlayer, warp, alreadyExists);
                                })
                        );
                    })
                    .text("Name")
                    .itemLeft(Item.create(Material.NAME_TAG, "§6§lChange Name"))
                    .title("§8Change the warp name")
                    .plugin(BuildTeamTools.getInstance())
                    .open(clickPlayer);
        });

        // Set click event for the material item
        getMenu().getSlot(MATERIAL_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new MaterialSelectionMenu(clickPlayer, warp, alreadyExists);
        });

        // Set click event for the address type item
        getMenu().getSlot(ADDRESS_TYPE_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AddressTypeSelectionMenu(clickPlayer, warp, alreadyExists);
        });

        // Set click event for the group item
        getMenu().getSlot(GROUP_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new WarpGroupSelectionMenu(clickPlayer, warp.getWarpGroup().getBuildTeam(), warp, alreadyExists);
        });

        // Set click event for the highlight item
        getMenu().getSlot(HIGHLIGHT_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            warp.setHighlight(!warp.isHighlight());
            new WarpEditMenu(clickPlayer, warp, alreadyExists);
        });

        // Set click event for the delete item
        if(alreadyExists)
            getMenu().getSlot(DELETE_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

                NetworkModule.getInstance().getBuildTeam().deleteWarp(clickPlayer, warp);
            });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short) 15, null))
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("111111110")
                .build();
    }
}
