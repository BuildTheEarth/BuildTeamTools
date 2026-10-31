package net.buildtheearth.buildteamtools.modules.navigation.components.warps.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.OutOfProjectionBoundsException;
import net.buildtheearth.Projection;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.heads.HeadFactory;
import net.buildtheearth.buildteamtools.utils.heads.HeadTexture;
import net.buildtheearth.buildteamtools.utils.heads.LetterType;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.buildtheearth.model.GeographicalCoordinate;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class WarpEditMenu extends AbstractMenu {

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

    /**
     * In this menu the player can update a warp.
     * This can be used for example to change the name of a warp in the {@link WarpMenu}.
     *
     * @param player The player that is viewing the menu.
     * @param warp   The warp that is being updated.
     */
    public WarpEditMenu(Player player, Warp warp, boolean alreadyExists, boolean autoLoad) {
        super(4, Messages.component(MessageKeys.Warp.Edit.TITLE), player, autoLoad);

        this.warp = warp;
        this.alreadyExists = alreadyExists;

        if (!this.alreadyExists) {
            NAME_SLOT = 18;
            GROUP_SLOT = 20;
            HIGHLIGHT_SLOT = 22;
            ADDRESS_TYPE_SLOT = 24;
            MATERIAL_SLOT = 26;
        }
    }


    @Override
    protected void setMenuItemsAsync() {
        Player player = getMenuPlayer();

        // Set the confirmation item
        getMenu().getSlot(CONFIRM_SLOT).setItem(HeadFactory.head(
                HeadTexture.CHECKMARK,
                Messages.render(player, alreadyExists ? MessageKeys.Warp.Edit.UPDATE : MessageKeys.Warp.Edit.CREATE)));

        // Set the warp item
        getMenu().getSlot(WARP_SLOT).setItem(warp.getMaterialItem(player));

        // Set the location item if the warp already exists. Otherwise, the location is set automatically on creation.
        if (alreadyExists) {
            List<Component> locationLore = new ArrayList<>(Messages.loreComponents(
                    player,
                    MessageKeys.Warp.Edit.LOCATION_LORE,
                    Component.text(String.valueOf(warp.getWorldName())),
                    Component.text(String.valueOf(warp.getCoordinate().latitude())),
                    Component.text(String.valueOf(warp.getCoordinate().longitude())),
                    Component.text(String.valueOf(warp.getY()))));
            getMenu().getSlot(LOCATION_SLOT).setItem(ItemComponents.of(
                    Objects.requireNonNull(XMaterial.COMPASS.get()),
                    Messages.render(player, MessageKeys.Warp.Edit.CHANGE_LOCATION),
                    locationLore));

        }

        // Set the name item
        List<Component> nameLore = new ArrayList<>(Messages.loreComponents(
                player, MessageKeys.Warp.Edit.NAME_LORE, Component.text(warp.getName())));
        getMenu().getSlot(NAME_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                Messages.render(player, MessageKeys.Warp.Edit.CHANGE_NAME),
                nameLore));

        // Set the group item
        List<Component> groupLore = new ArrayList<>(Messages.loreComponents(
                player, MessageKeys.Warp.Edit.GROUP_LORE, Component.text(warp.getWarpGroup().getName())));
        getMenu().getSlot(GROUP_SLOT).setItem(HeadFactory.letter(
                LetterType.WOODEN,
                warp.getWarpGroup().getName().charAt(0),
                Messages.render(player, MessageKeys.Warp.Edit.CHANGE_GROUP),
                groupLore
        ));

        // Set the address type item
        String addressTypeName = warp.getAddressType() == null
                ? Messages.plain(player, MessageKeys.Warp.Edit.ADDRESS_TYPE_CITY_DEFAULT)
                : warp.getAddressType().getName();
        List<Component> addressTypeLore = new ArrayList<>(Messages.loreComponents(
                player, MessageKeys.Warp.Edit.ADDRESS_TYPE_LORE, Component.text(addressTypeName)));
        getMenu().getSlot(ADDRESS_TYPE_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.PAPER.get()),
                Messages.render(player, MessageKeys.Warp.Edit.CHANGE_ADDRESS_TYPE),
                addressTypeLore));

        // Set the material item
        String materialName = warp.getMaterial() == null
                ? Messages.plain(player, MessageKeys.Warp.Edit.MATERIAL_DEFAULT)
                : warp.getMaterial();
        List<Component> materialLore = new ArrayList<>(Messages.loreComponents(
                player, MessageKeys.Warp.Edit.MATERIAL_LORE, Component.text(materialName)));
        getMenu().getSlot(MATERIAL_SLOT).setItem(ItemComponents.named(
                warp.getMaterialItem(player),
                Messages.render(player, MessageKeys.Warp.Edit.CHANGE_MATERIAL),
                materialLore));

        // Set the highlight item
        List<Component> highlightLore = new ArrayList<>(Messages.loreComponents(
                player, MessageKeys.Warp.Edit.HIGHLIGHT_LORE, Component.text(String.valueOf(warp.isHighlight()))));
        getMenu().getSlot(HIGHLIGHT_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.NETHER_STAR.get()),
                Messages.render(player, warp.isHighlight()
                        ? MessageKeys.Warp.Edit.MAKE_NORMAL
                        : MessageKeys.Warp.Edit.MAKE_HIGHLIGHT),
                highlightLore));

        // Set the delete item
        if (alreadyExists)
            getMenu().getSlot(DELETE_SLOT).setItem(ItemComponents.of(
                    Objects.requireNonNull(XMaterial.BARRIER.get()),
                    Messages.render(player, MessageKeys.Warp.Edit.DELETE),
                    Messages.loreComponents(player, MessageKeys.Warp.Edit.DELETE_LORE)));
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click event for the confirmation item
        getMenu().getSlot(CONFIRM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

            BuildTeam buildTeam = NetworkModule.getInstance().getBuildTeam();
            if (buildTeam != null) {
                if (alreadyExists)
                    buildTeam.updateWarp(clickPlayer, warp);
                else
                    buildTeam.createWarp(clickPlayer, warp);
            }
        });

        // Set click event for the location item
        getMenu().getSlot(LOCATION_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

            Location location = clickPlayer.getLocation();

            try {
                GeographicalCoordinate coordinate = Projection.toGeo(location.getX(), location.getZ());

                WarpsComponent.getOwnedRegionFromLocation(coordinate, clickPlayer)
                        .thenAccept(result -> {
                            warp.setCountryCode(result.countryCodeCCA2());
                            warp.setWorldName(location.getWorld().getName());
                            warp.setY(location.getY());
                            warp.setCoordinate(coordinate);
                            warp.setYaw(location.getYaw());
                            warp.setPitch(location.getPitch());

                            clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

                            new WarpEditMenu(clickPlayer, warp, alreadyExists, true);
                        })
                        .exceptionally(e -> {
                            Throwable cause = e.getCause() != null ? e.getCause() : e;

                            clickPlayer.sendMessage(Messages.component(
                                    MessageKeys.Warp.Edit.LOCATION_CHANGE_FAILED,
                                    Component.text(String.valueOf(cause.getMessage()))
                            ));

                            BuildTeamTools.getInstance().getComponentLogger().error(
                                    "An error occurred while changing the location of the warp!",
                                    e
                            );

                            return null;
                        });

            } catch (OutOfProjectionBoundsException e) {
                clickPlayer.sendMessage(Messages.component(
                        MessageKeys.Warp.Edit.LOCATION_SET_HERE_FAILED,
                        Component.text(String.valueOf(e.getMessage()))));
            }
        });


        // Set click event for the name item
        getMenu().getSlot(NAME_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AnvilGUI.Builder()
                    .onClose(player -> {
                        player.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

                        new WarpEditMenu(clickPlayer, warp, alreadyExists, true);
                    })
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != AnvilGUI.Slot.OUTPUT)
                            return Collections.emptyList();

                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        return Arrays.asList(
                                AnvilGUI.ResponseAction.close(),
                                AnvilGUI.ResponseAction.run(() -> {
                                    warp.setName(stateSnapshot.getText());
                                    new WarpEditMenu(clickPlayer, warp, alreadyExists, true);
                                })
                        );
                    })
                    .text(Messages.plain(clickPlayer, MessageKeys.Warp.Edit.ANVIL_TEXT))
                    .itemLeft(ItemComponents.of(
                            Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                            Messages.render(clickPlayer, MessageKeys.Warp.Edit.CHANGE_NAME)))
                    .title(Messages.legacy(clickPlayer, MessageKeys.Warp.Edit.ANVIL_TITLE))
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

            new WarpGroupSelectionMenu(clickPlayer, warp.getWarpGroup().getBuildTeam(), warp, alreadyExists, true);
        });

        // Set click event for the highlight item
        getMenu().getSlot(HIGHLIGHT_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            warp.setHighlight(!warp.isHighlight());
            new WarpEditMenu(clickPlayer, warp, alreadyExists, true);
        });

        // Set click event for the delete item
        if (alreadyExists)
            getMenu().getSlot(DELETE_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

                if (clickPlayer.hasPermission(Permissions.WARP_DELETE)) {
                    BuildTeam buildTeam = NetworkModule.getInstance().getBuildTeam();
                    if (buildTeam != null) {
                        buildTeam.deleteWarp(clickPlayer, warp);
                    }
                } else {
                    clickPlayer.sendMessage(Messages.component(MessageKeys.Warp.Edit.DELETE_NO_PERMISSION));
                }
            });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern("111111110")
                .build();
    }

}
