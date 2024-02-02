package net.buildtheearth.modules.warp.menu;

import net.buildtheearth.Main;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.ListUtil;
import net.buildtheearth.modules.utils.MenuItems;
import net.buildtheearth.modules.utils.menus.AbstractMenu;
import net.buildtheearth.modules.warp.model.WarpGroup;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static net.buildtheearth.modules.warp.menu.WarpEditMenu.CONFIRM_SLOT;

public class WarpGroupEditMenu extends AbstractMenu {

    public static String WARP_GROUP_UPDATE_INV_NAME = "Configure the Warp Group";


    public static int WARP_GROUP = 4;
    public static int NAME_SLOT = 21;
    public static int DESCRIPTION_SLOT = 23;


    private final WarpGroup warpGroup;
    private final boolean alreadyExists;

    /** In this menu the player can update a warp.
     * This can be used for example to change the name of a warp in the {@link WarpMenu}.
     *
     * @param player  The player that is viewing the menu.
     * @param warpGroup The warp that is being updated.
     */
    public WarpGroupEditMenu(Player player, WarpGroup warpGroup, boolean alreadyExists) {
        super(4, WARP_GROUP_UPDATE_INV_NAME, player);

        this.warpGroup = warpGroup;
        this.alreadyExists = alreadyExists;
    }

    @Override
    protected void setMenuItemsAsync() {
        // Set the confirmation item
        getMenu().getSlot(CONFIRM_SLOT).setItem(MenuItems.getCheckmarkItem(alreadyExists ? "§aUpdate" : "§aCreate"));

        // Set the warp group item
        getMenu().getSlot(WARP_GROUP).setItem(
                MenuItems.getLetterHead(
                        warpGroup.getName().substring(0, 1),
                        MenuItems.LetterType.WOODEN,
                        "§6§l" + warpGroup.getName(),
                        ListUtil.createList("", "§eDescription:", warpGroup.getDescription())
                )
        );

        // Set the name item
        ArrayList<String> nameLore = ListUtil.createList("", "§eCurrent Name: ", warpGroup.getName());
        getMenu().getSlot(NAME_SLOT).setItem(Item.create(Material.NAME_TAG, "§6§lChange Name", nameLore));

        // Set the Description item
        getMenu().getSlot(DESCRIPTION_SLOT).setItem(Item.create(Material.BOOK, "§6§lChange Description", warpGroup.getDescriptionLore()));
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click event for the confirmation item
        getMenu().getSlot(CONFIRM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

            if(alreadyExists)
                Main.getBuildTeamTools().getProxyManager().getBuildTeam().updateWarpGroup(clickPlayer, warpGroup);
            else
                Main.getBuildTeamTools().getProxyManager().getBuildTeam().createWarpGroup(clickPlayer, warpGroup);
        });

        // Set click event for the name item
        getMenu().getSlot(NAME_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AnvilGUI.Builder()
                    .onClose(player -> {
                        player.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

                        new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists);
                    })
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != AnvilGUI.Slot.OUTPUT)
                            return Collections.emptyList();

                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        return Arrays.asList(
                                AnvilGUI.ResponseAction.close(),
                                AnvilGUI.ResponseAction.run(() -> {
                                    warpGroup.setName(stateSnapshot.getText());
                                    new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists);
                                })
                        );
                    })
                    .text("Name")
                    .itemLeft(Item.create(Material.NAME_TAG, "§6§lChange Name"))
                    .title("§8Change the warp name")
                    .plugin(Main.instance)
                    .open(clickPlayer);
        });

        // Set click event for the description item
        getMenu().getSlot(DESCRIPTION_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AnvilGUI.Builder()
                    .onClose(player -> {
                        player.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

                        new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists);
                    })
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != AnvilGUI.Slot.OUTPUT)
                            return Collections.emptyList();

                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        return Arrays.asList(
                                AnvilGUI.ResponseAction.close(),
                                AnvilGUI.ResponseAction.run(() -> {
                                    warpGroup.setDescription(stateSnapshot.getText());
                                    new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists);
                                })
                        );
                    })
                    .text("Description")
                    .itemLeft(Item.create(Material.NAME_TAG, "§6§lChange Description"))
                    .title("§8Change the description")
                    .plugin(Main.instance)
                    .open(clickPlayer);
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
