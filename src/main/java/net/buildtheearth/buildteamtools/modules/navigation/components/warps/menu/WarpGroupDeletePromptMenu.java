package net.buildtheearth.buildteamtools.modules.navigation.components.warps.menu;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.utils.ListUtil;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WarpGroupDeletePromptMenu extends AbstractMenu {

    public static final String INV_NAME = "Delete Warp Group?";

    public static final int MOVE_WARPS_SLOT = 11;
    public static final int DELETE_WARPS_SLOT = 15;
    public static final int INFO_SLOT = 13;
    public static final int BACK_ITEM_SLOT = 27;

    private final WarpGroup warpGroup;
    private final AbstractMenu backMenu;

    /**
     * Confirmation menu shown before deleting a warp group.
     * <p>
     * If the group contains warps, the player can either move them to a fallback group
     * or delete them together with the group.
     *
     * @param menuPlayer The player viewing the menu..
     * @param warpGroup  The warp group being deleted.
     * @param backMenu   The menu to go back to.
     */
    public WarpGroupDeletePromptMenu(Player menuPlayer, WarpGroup warpGroup, AbstractMenu backMenu) {
        super(4, INV_NAME, menuPlayer);
        this.warpGroup = warpGroup;
        this.backMenu = backMenu;
    }

    @Override
    protected void setMenuItemsAsync() {
        int warpAmount = getWarpsInGroup().size();

        ArrayList<String> infoLore = ListUtil.createList(
                "",
                "§7You are about to delete:",
                "§e" + warpGroup.getName(),
                "",
                "§7This group contains §e" + warpAmount + " §7warp" + (warpAmount == 1 ? "." : "s.")
        );

        getMenu().getSlot(INFO_SLOT).setItem(
                Item.create(
                        Objects.requireNonNull(XMaterial.PAPER.get()),
                        "§6§lDelete Warp Group",
                        infoLore
                )
        );
        getMenu().getSlot(MOVE_WARPS_SLOT).setItem(
                Item.create(
                        Objects.requireNonNull(XMaterial.LIME_DYE.get()),
                        "§a§lMove Warps",
                        ListUtil.createList(
                                "",
                                "§7Delete this group, but keep",
                                "§7all warps inside it.",
                                "",
                                "§7The warps will be moved to:",
                                "§e Other ",
                                "",
                                "§eClick to continue."
                        )
                )
        );


        getMenu().getSlot(DELETE_WARPS_SLOT).setItem(
                Item.create(
                        Objects.requireNonNull(XMaterial.RED_DYE.get()),
                        "§c§lDelete Everything",
                        ListUtil.createList(
                                "",
                                "§7Delete this group and all",
                                "§7warps inside it.",
                                "",
                                "§cThis cannot be undone.",
                                "",
                                "§eClick to continue."
                        )
                )
        );

        if (backMenu != null) {
            setBackItem(BACK_ITEM_SLOT, new WarpGroupEditMenu(getMenuPlayer(), warpGroup, true, false));
        }
    }

    @Override
    protected void setItemClickEventsAsync() {
        getMenu().getSlot(MOVE_WARPS_SLOT).setClickHandler((clickPlayer, clickInformation) -> {

            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            clickPlayer.closeInventory();

            warpGroup.getBuildTeam().deleteWarpGroup(clickPlayer, warpGroup, false);
        });

        getMenu().getSlot(DELETE_WARPS_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            clickPlayer.closeInventory();

            warpGroup.getBuildTeam().deleteWarpGroup(clickPlayer, warpGroup, true);
        });
    }

    private List<Warp> getWarpsInGroup() {
        return warpGroup.getWarps();
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
