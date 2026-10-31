package net.buildtheearth.buildteamtools.modules.navigation.components.warps.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WarpGroupDeletePromptMenu extends AbstractMenu {

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
        super(4, Messages.component(MessageKeys.Warp.DeleteGroup.TITLE), menuPlayer);
        this.warpGroup = warpGroup;
        this.backMenu = backMenu;
    }

    @Override
    protected void setMenuItemsAsync() {
        Player player = getMenuPlayer();
        int warpAmount = getWarpsInGroup().size();

        String infoLoreKey = warpAmount == 1
                ? MessageKeys.Warp.DeleteGroup.INFO_LORE_SINGULAR
                : MessageKeys.Warp.DeleteGroup.INFO_LORE_PLURAL;
        List<Component> infoLore = new ArrayList<>(Messages.loreComponents(
                player,
                infoLoreKey,
                Component.text(warpGroup.getName()),
                Component.text(String.valueOf(warpAmount))));

        getMenu().getSlot(INFO_SLOT).setItem(
                ItemComponents.of(
                        Objects.requireNonNull(XMaterial.PAPER.get()),
                        Messages.render(player, MessageKeys.Warp.DeleteGroup.INFO_NAME),
                        infoLore
                )
        );
        getMenu().getSlot(MOVE_WARPS_SLOT).setItem(
                ItemComponents.of(
                        Objects.requireNonNull(XMaterial.LIME_DYE.get()),
                        Messages.render(player, MessageKeys.Warp.DeleteGroup.MOVE_NAME),
                        Messages.loreComponents(player, MessageKeys.Warp.DeleteGroup.MOVE_LORE)
                )
        );


        getMenu().getSlot(DELETE_WARPS_SLOT).setItem(
                ItemComponents.of(
                        Objects.requireNonNull(XMaterial.RED_DYE.get()),
                        Messages.render(player, MessageKeys.Warp.DeleteGroup.DELETE_ALL_NAME),
                        Messages.loreComponents(player, MessageKeys.Warp.DeleteGroup.DELETE_ALL_LORE)
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
