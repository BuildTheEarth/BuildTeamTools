package net.buildtheearth.modules.warp.menu;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.ListUtil;
import net.buildtheearth.modules.utils.MenuItems;
import net.buildtheearth.modules.utils.menus.AbstractMenu;
import net.buildtheearth.modules.utils.menus.BookMenu;
import net.buildtheearth.modules.warp.model.WarpGroup;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.buildtheearth.modules.warp.menu.WarpEditMenu.CONFIRM_SLOT;

public class WarpGroupEditMenu extends AbstractMenu {

    public static String WARP_GROUP_UPDATE_INV_NAME = "Configure the Warp Group";


    public static int WARP_GROUP = 4;
    public static int NAME_SLOT = 20;
    public static int DESCRIPTION_SLOT = 22;

    public static int DELETE_SLOT = 24;


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
                        warpGroup.getDescriptionLore()
                )
        );

        // Set the name item
        ArrayList<String> nameLore = ListUtil.createList("", "§eCurrent Name: ", warpGroup.getName());
        getMenu().getSlot(NAME_SLOT).setItem(Item.create(Material.NAME_TAG, "§6§lChange Name", nameLore));

        // Set the Description item
        getMenu().getSlot(DESCRIPTION_SLOT).setItem(Item.create(Material.BOOK, "§6§lChange Description", warpGroup.getDescriptionLore()));

        // Set the delete item
        getMenu().getSlot(DELETE_SLOT).setItem(Item.create(Material.BARRIER, "§c§lDelete Warp Group", ListUtil.createList("", "§8Click to delete the warp group.")));
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

            List<String[]> description = new ArrayList<>();
            description.add(warpGroup.getDescription().split("<br>"));

            BookMenu bookMenu = new BookMenu(clickPlayer, "§6§lChange Description", clickPlayer.getName(), description, 240);

            bookMenu.onComplete((text) -> {
                if(text == null) {
                    clickPlayer.sendMessage("§cA problem occurred while saving the description.");
                    new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists);
                    return;
                }

                // Combine the first page to a single string
                String finalText = text.get(0)[0];
                for(int i = 1; i < text.get(0).length; i++)
                    finalText += "<br>" + text.get(0)[i];

                warpGroup.setDescription(finalText);
                new WarpGroupEditMenu(clickPlayer, warpGroup, alreadyExists);
            });
        });

        // Set click event for the delete item
        getMenu().getSlot(DELETE_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

            Main.getBuildTeamTools().getProxyManager().getBuildTeam().deleteWarpGroup(clickPlayer, warpGroup);
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
