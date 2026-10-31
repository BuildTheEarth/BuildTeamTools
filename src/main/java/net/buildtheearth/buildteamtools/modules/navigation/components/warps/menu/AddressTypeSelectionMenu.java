package net.buildtheearth.buildteamtools.modules.navigation.components.warps.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.ListUtil;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.Utils;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AddressTypeSelectionMenu extends AbstractMenu {

    public static final int BUILDING_SLOT = 10;
    public static final int STREET_SLOT = 11;
    public static final int CITY_SLOT = 12;
    public static final int STATE_SLOT = 13;
    public static final int COUNTRY_SLOT = 14;
    public static final int CUSTOM_SLOT = 15;
    public static final int BACK_ITEM_SLOT = 27;

    private final Warp warp;
    private final boolean alreadyExists;

    /**
     * In this menu the player can select an address type for a warp.
     * This can be used for example to change the AddressType of a warp in the {@link WarpMenu}.
     *
     * @param menuPlayer    The player that is viewing the menu.
     * @param warp          The warp that is being updated with the selected warp group.
     * @param alreadyExists Whether the warp already exists.
     */
    public AddressTypeSelectionMenu(Player menuPlayer, Warp warp, boolean alreadyExists) {
        super(4, Messages.component(MessageKeys.Warp.AddressType.TITLE), menuPlayer);
        this.warp = warp;
        this.alreadyExists = alreadyExists;
    }

    @Override
    protected void setMenuItemsAsync() {
        Player player = getMenuPlayer();

        getMenu().getSlot(BUILDING_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.LIGHT_BLUE_DYE.get()),
                Messages.render(player, MessageKeys.Warp.AddressType.BUILDING),
                exampleLore(player, Warp.AddressType.BUILDING)));
        getMenu().getSlot(STREET_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.LIGHT_BLUE_DYE.get()),
                Messages.render(player, MessageKeys.Warp.AddressType.STREET),
                exampleLore(player, Warp.AddressType.STREET)));
        getMenu().getSlot(CITY_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.LIGHT_BLUE_DYE.get()),
                Messages.render(player, MessageKeys.Warp.AddressType.CITY),
                exampleLore(player, Warp.AddressType.CITY)));
        getMenu().getSlot(STATE_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.LIGHT_BLUE_DYE.get()),
                Messages.render(player, MessageKeys.Warp.AddressType.STATE),
                exampleLore(player, Warp.AddressType.STATE)));
        getMenu().getSlot(COUNTRY_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.LIGHT_BLUE_DYE.get()),
                Messages.render(player, MessageKeys.Warp.AddressType.COUNTRY),
                exampleLore(player, Warp.AddressType.COUNTRY)));
        getMenu().getSlot(CUSTOM_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                Messages.render(player, MessageKeys.Warp.AddressType.CUSTOM),
                Messages.loreComponents(player, MessageKeys.Warp.AddressType.CUSTOM_LORE)));

        setBackItem(BACK_ITEM_SLOT, new WarpEditMenu(getMenuPlayer(), warp, alreadyExists, false));
    }

    private List<Component> exampleLore(Player player, Warp.AddressType type) {
        List<Component> lore = new ArrayList<>(Messages.loreComponents(player, MessageKeys.Warp.AddressType.EXAMPLE_LABEL));
        lore.addAll(ListUtil.grayComponents(Utils.splitStringByLineLength(type.getExample(), 30, ", ")));
        return lore;
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click event for the address type items
        getMenu().getSlot(BUILDING_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            warp.setAddressType(Warp.AddressType.BUILDING);
            clickPlayer.closeInventory();
            new WarpEditMenu(clickPlayer, warp, alreadyExists, true);
        });
        getMenu().getSlot(STREET_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            warp.setAddressType(Warp.AddressType.STREET);
            clickPlayer.closeInventory();
            new WarpEditMenu(clickPlayer, warp, alreadyExists, true);
        });
        getMenu().getSlot(CITY_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            warp.setAddressType(Warp.AddressType.CITY);
            clickPlayer.closeInventory();
            new WarpEditMenu(clickPlayer, warp, alreadyExists, true);
        });
        getMenu().getSlot(STATE_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            warp.setAddressType(Warp.AddressType.STATE);
            clickPlayer.closeInventory();
            new WarpEditMenu(clickPlayer, warp, alreadyExists, true);
        });
        getMenu().getSlot(COUNTRY_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            warp.setAddressType(Warp.AddressType.COUNTRY);
            clickPlayer.closeInventory();
            new WarpEditMenu(clickPlayer, warp, alreadyExists, true);
        });


        // Set click event for the custom address item
        getMenu().getSlot(CUSTOM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
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
                                    warp.setAddressType(Warp.AddressType.CUSTOM);
                                    warp.setAddress(stateSnapshot.getText());
                                    new WarpEditMenu(clickPlayer, warp, alreadyExists, true);
                                })
                        );
                    })
                    .text(Messages.plain(clickPlayer, MessageKeys.Warp.AddressType.ANVIL_TEXT))
                    .itemLeft(ItemComponents.of(
                            Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                            Messages.render(clickPlayer, MessageKeys.Warp.AddressType.ANVIL_ITEM)))
                    .title(Messages.legacy(clickPlayer, MessageKeys.Warp.AddressType.ANVIL_TITLE))
                    .plugin(BuildTeamTools.getInstance())
                    .open(clickPlayer);
        });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern("011111111")
                .build();
    }
}
