package net.buildtheearth.modules.navigation.components.warps.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.ListUtil;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.Utils;
import net.buildtheearth.utils.menus.AbstractMenu;
import net.buildtheearth.modules.navigation.components.warps.model.Warp;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AddressTypeSelectionMenu extends AbstractMenu {

    public static String ADDRESS_TYPE_INV_NAME = "Select an Address Type";
    public final int BUILDING_SLOT = 10;
    public final int STREET_SLOT = 11;
    public final int CITY_SLOT = 12;
    public final int STATE_SLOT = 13;
    public final int COUNTRY_SLOT = 14;
    public final int CUSTOM_SLOT = 15;
    public final int BACK_ITEM_SLOT = 27;

    private final Warp warp;
    private final boolean alreadyExists;

    /** In this menu the player can select an address type for a warp.
     * This can be used for example to change the AddressType of a warp in the {@link WarpMenu}.
     *
     * @param menuPlayer  The player that is viewing the menu.
     * @param warp The warp that is being updated with the selected warp group.
     * @param alreadyExists Whether the warp already exists.
     */
    public AddressTypeSelectionMenu(Player menuPlayer, Warp warp, boolean alreadyExists) {
        super(4, ADDRESS_TYPE_INV_NAME, menuPlayer);
        this.warp = warp;
        this.alreadyExists = alreadyExists;
    }

    @Override
    protected void setMenuItemsAsync() {
        ArrayList<String> buildingLore = ListUtil.createList("", "§eExample:");
        ArrayList<String> streetLore = ListUtil.createList("", "§eExample:");
        ArrayList<String> cityLore = ListUtil.createList("", "§eExample:");
        ArrayList<String> stateLore = ListUtil.createList("", "§eExample:");
        ArrayList<String> countryLore = ListUtil.createList("", "§eExample:");

        buildingLore.addAll(ListUtil.createList(Utils.splitStringByLineLength(Warp.AddressType.BUILDING.getExample(), 30, ", ")));
        streetLore.addAll(ListUtil.createList(Utils.splitStringByLineLength(Warp.AddressType.STREET.getExample(), 30, ", ")));
        cityLore.addAll(ListUtil.createList(Utils.splitStringByLineLength(Warp.AddressType.CITY.getExample(), 30, ", ")));
        stateLore.addAll(ListUtil.createList(Utils.splitStringByLineLength(Warp.AddressType.STATE.getExample(), 30, ", ")));
        countryLore.addAll(ListUtil.createList(Utils.splitStringByLineLength(Warp.AddressType.COUNTRY.getExample(), 30, ", ")));

        getMenu().getSlot(BUILDING_SLOT).setItem(new Item(XMaterial.LIGHT_BLUE_DYE.parseItem()).setDisplayName("§6§lBuilding").setLore(buildingLore).build());
        getMenu().getSlot(STREET_SLOT).setItem(new Item(XMaterial.LIGHT_BLUE_DYE.parseItem()).setDisplayName("§6§lStreet").setLore(streetLore).build());
        getMenu().getSlot(CITY_SLOT).setItem(new Item(XMaterial.LIGHT_BLUE_DYE.parseItem()).setDisplayName("§6§lCity").setLore(cityLore).build());
        getMenu().getSlot(STATE_SLOT).setItem(new Item(XMaterial.LIGHT_BLUE_DYE.parseItem()).setDisplayName("§6§lState").setLore(stateLore).build());
        getMenu().getSlot(COUNTRY_SLOT).setItem(new Item(XMaterial.LIGHT_BLUE_DYE.parseItem()).setDisplayName("§6§lCountry").setLore(countryLore).build());
        getMenu().getSlot(CUSTOM_SLOT).setItem(Item.create(XMaterial.NAME_TAG.parseMaterial(), "§6§lCustom Address", ListUtil.createList("", "§7If no other address type fits,", "§7enter a custom address.")));

        setBackItem(BACK_ITEM_SLOT, new WarpEditMenu(getMenuPlayer(), warp, alreadyExists, false));
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
                    .text("Address")
                    .itemLeft(Item.create(XMaterial.NAME_TAG.parseMaterial(), "§6§lChange Address"))
                    .title("§8Enter the address")
                    .plugin(BuildTeamTools.getInstance())
                    .open(clickPlayer);
        });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("011111111")
                .build();
    }
}
