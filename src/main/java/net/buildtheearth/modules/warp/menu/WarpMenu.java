package net.buildtheearth.modules.warp.menu;

import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.ListUtil;
import net.buildtheearth.modules.utils.MenuItems;
import net.buildtheearth.modules.utils.Utils;
import net.buildtheearth.modules.utils.menus.AbstractPaginatedMenu;
import net.buildtheearth.modules.warp.WarpManager;
import net.buildtheearth.modules.warp.model.Warp;
import net.buildtheearth.modules.warp.model.WarpGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WarpMenu extends AbstractPaginatedMenu {

    public final int BACK_ITEM_SLOT = 27;

    private final boolean hasBackItem;
    private final WarpGroup warpGroup;

    public WarpMenu(Player menuPlayer, WarpGroup warpGroup, boolean hasBackItem) {
        super(4, 3, "Warp Menu", menuPlayer);
        this.hasBackItem = hasBackItem;
        this.warpGroup = warpGroup;
    }

    @Override
    protected void setMenuItemsAsync() {
        setBackItem(BACK_ITEM_SLOT, hasBackItem);
    }

    @Override
    protected void setItemClickEventsAsync() {
        if(hasBackItem)
            getMenu().getSlot(BACK_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();
                new WarpGroupMenu(clickPlayer, warpGroup.getBuildTeam(), false);
            });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short) 15, null))
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("011111111")
                .build();
    }

    @Override
    protected List<?> getSource() {
        // Return the warps in the warp group sorted by name
        return warpGroup.getWarps().stream().sorted((warp1, warp2) -> warp1.getName().compareToIgnoreCase(warp2.getName())).collect(Collectors.toList());
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        List<Warp> warps = source.stream().map(l -> (Warp) l).collect(Collectors.toList());

        // Create the country items
        int slot = 0;

        for (Warp warp : warps) {
            ArrayList<String> loreLines = ListUtil.createList("", "§eAddress:");
            loreLines.addAll(Arrays.asList(Utils.splitStringByLineLength(warp.getAddress(), 30)));
            loreLines.addAll(ListUtil.createList("", "§8Left-Click to warp to this location.", "§8Right-Click to edit this warp."));

            ArrayList<String> warpLore = ListUtil.createList(loreLines.toArray(new String[0]));
            getMenu().getSlot(slot).setItem(
                    MenuItems.getLetterHead(
                            warp.getName().substring(0, 1),
                            MenuItems.LetterType.STONE,
                            "§6§l" + warp.getName(),
                            warpLore
                    )
            );
            slot++;
        }
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {

    }

    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> source) {
        List<Warp> warps = source.stream().map(l -> (Warp) l).collect(Collectors.toList());

        int slot = 0;
        for (Warp warp : warps) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                if(clickInformation.getClickType().isRightClick())
                    new WarpUpdateMenu(clickPlayer, warp, true);
                else if(clickInformation.getClickType().isLeftClick())
                    WarpManager.warpPlayer(clickPlayer, warp);
            });
            slot++;
        }
    }
}
