package net.buildtheearth.modules.navigation.components.warps.menu;

import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.modules.navigation.menu.CountrySelectorMenu;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Permissions;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.CustomHeads;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.ListUtil;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.io.ConfigPaths;
import net.buildtheearth.utils.io.ConfigUtil;
import net.buildtheearth.utils.menus.AbstractPaginatedMenu;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WarpGroupMenu extends AbstractPaginatedMenu {

    public static final int BACK_ITEM_SLOT = 27;

    public static final int ALTERNATE_PLUS_SLOT = 35;

    private final boolean hasBackItem;
    private final BuildTeam buildTeam;

    /** In this menu the player can select a warp group to view the warps in each warp group.
     *
     * @param menuPlayer The player that is viewing the menu
     * @param buildTeam The build team that the menu is for
     * @param hasBackItem Whether the menu has a back item
     */
    public WarpGroupMenu(Player menuPlayer, BuildTeam buildTeam, boolean hasBackItem, boolean autoLoad) {
        super(4, 3, "Warp Menu", menuPlayer, autoLoad);
        this.hasBackItem = hasBackItem;
        this.buildTeam = buildTeam;
    }

    @Override
    protected void setMenuItemsAsync() {
        if (hasBackItem) {
            if (ChatHelper.DEBUG)
                getMenuPlayer().sendMessage("Finally triggered the misterious back item in the warp group menu");
            setBackItem(BACK_ITEM_SLOT, new CountrySelectorMenu(getMenuPlayer(), buildTeam.getRegions().getFirst().getContinent(), false));
            // TODO fix it for multiple continents
            if (ChatHelper.DEBUG) Thread.dumpStack();
            ChatHelper.logDebug("Setting back item for warp group menu");
        }
    }

    @Override
    protected void setItemClickEventsAsync() {
        if(hasBackItem)
            getMenu().getSlot(BACK_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> clickPlayer.closeInventory());
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        List<WarpGroup> warpGroups = source.stream().map(l -> (WarpGroup) l).toList();

        getMenu().getSlot(ALTERNATE_PLUS_SLOT).setItem(MenuItems.ITEM_BACKGROUND);

        // Create the country items
        int slot = 0;

        for (WarpGroup warpGroup : warpGroups) {
            // Create a create warp group item if the player has permission
            if(isPlusItem(warpGroup) && slot == warpGroups.size() - 1){
                getMenu().getSlot(getPlusSlot(slot)).setItem(
                        Item.createCustomHeadBase64(
                                CustomHeads.GREEN_PLUS, "§a§lCreate a new Warp Group",
                                ListUtil.createList("§8Click to create a new warp group.")
                        )
                );
                slot++;
                continue;
            }

            getMenu().getSlot(getWarpGroupSlot(warpGroup, slot)).setItem(warpGroup.getMaterialItem());
            slot++;
        }
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {

    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
            .item(MenuItems.ITEM_BACKGROUND)
            .pattern("000000000")
            .pattern("000000000")
            .pattern("000000000")
            .pattern("111111110")
            .build();
    }

    @Override
    protected List<?> getSource() {
        // Get the warp groups in the build team sorted by name
        List<WarpGroup> warpGroups;

        String mode = BuildTeamTools.getInstance().getConfig(ConfigUtil.NAVIGATION)
                .getString(ConfigPaths.Navigation.WARPS_GROUP_SORTING_MODE, "");

        if (mode.equalsIgnoreCase("name")) {
            warpGroups = buildTeam.getWarpGroups().stream().sorted((warpGroup1, warpGroup2) -> warpGroup1.getName().compareToIgnoreCase(warpGroup2.getName())).collect(Collectors.toList());
        } else warpGroups = new ArrayList<>(buildTeam.getWarpGroups());

        // If the warp group "Other" has no warps, remove it from the list
        WarpGroup otherWarpGroup = warpGroups.stream().filter(warpGroup -> warpGroup.getName().equalsIgnoreCase("Other")).findFirst().orElse(null);
        if (otherWarpGroup != null && otherWarpGroup.getWarps().isEmpty())
            warpGroups.remove(otherWarpGroup);

        // Add a create warp group item if the player has permission
        if (getMenuPlayer().hasPermission(Permissions.WARP_GROUP_CREATE))
            warpGroups.add(new WarpGroup(null, "%create-warp-group%", null, -1, null));


        return warpGroups;
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> source) {
        List<WarpGroup> warpGroups = source.stream().map(l -> (WarpGroup) l).toList();

        int slot = 0;
        for (WarpGroup warpGroup : warpGroups) {
            setClickEventForSlot(warpGroup, slot);
            slot++;
        }
    }

    protected void setClickEventForSlot(@NotNull WarpGroup warpGroup, int slot) {
        final int _slot = isPlusItem(warpGroup) ? getPlusSlot(slot) : getWarpGroupSlot(warpGroup, slot);

        getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();

            // Create a click action for the "Create Warp" item if the player has permission
            if(isPlusItem(warpGroup)){
                NavigationModule.getInstance().getWarpsComponent().createWarpGroup(clickPlayer);
                return;
            }

            if(clickInformation.getClickType().isRightClick() && clickPlayer.hasPermission(Permissions.WARP_GROUP_EDIT))
                new WarpGroupEditMenu(clickPlayer, warpGroup, true, true);
            else
                leftClickAction(clickPlayer, warpGroup);
        });
    }

    protected void leftClickAction(Player clickPlayer, @NotNull WarpGroup warpGroup) {
        new WarpMenu(clickPlayer, warpGroup, true, true);
    }

    protected boolean isPlusItem(@NotNull WarpGroup warpGroup){
        return warpGroup.getName().equals("%create-warp-group%") && getMenuPlayer().hasPermission(Permissions.WARP_GROUP_CREATE);
    }

    protected int getWarpGroupSlot(@NotNull WarpGroup warpGroup, int currentIndex){
        if (warpGroup.getSlot() == -1 && warpGroup.getInternalSlot() == -1) return currentIndex;

        if (warpGroup.getSlot() >= 0 && warpGroup.getSlot() < 27) {
            return warpGroup.getSlot();
        } else return warpGroup.getInternalSlot() == -1 ? currentIndex : warpGroup.getInternalSlot();
    }

    protected int getPlusSlot(int currentIndex){
        int warpGroupSlot = currentIndex;

        for(WarpGroup warpGroup : buildTeam.getWarpGroups())
            if (warpGroup.getSlot() != -1 && warpGroup.getSlot() > 0 && warpGroup.getSlot() < 27) {
                warpGroupSlot = ALTERNATE_PLUS_SLOT;
                break;
            }

        return warpGroupSlot;
    }
}
