package net.buildtheearth.modules.navigation.components.warps.menu;

import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.modules.navigation.menu.CountrySelectorMenu;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Permissions;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.ListUtil;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.AbstractPaginatedMenu;
import net.buildtheearth.modules.navigation.components.warps.model.WarpGroup;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.List;
import java.util.stream.Collectors;

public class WarpGroupMenu extends AbstractPaginatedMenu {

    public final int BACK_ITEM_SLOT = 27;

    public final int ALTERNATE_PLUS_SLOT = 35;

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
        if(hasBackItem)
            setBackItem(BACK_ITEM_SLOT, new CountrySelectorMenu(getMenuPlayer(), buildTeam.getContinent(), false));
    }

    @Override
    protected void setItemClickEventsAsync() {
        if(hasBackItem)
            getMenu().getSlot(BACK_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

            });
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        List<WarpGroup> warpGroups = source.stream().map(l -> (WarpGroup) l).collect(Collectors.toList());

        getMenu().getSlot(ALTERNATE_PLUS_SLOT).setItem(Item.create(Material.STAINED_GLASS_PANE, " ", (short) 15, null));

        // Create the country items
        int slot = 0;

        for (WarpGroup warpGroup : warpGroups) {
            // Create a create warp group item if the player has permission
            if(isPlusItem(warpGroup) && slot == warpGroups.size() - 1){
                getMenu().getSlot(getPlusSlot(slot)).setItem(
                        Item.createCustomHeadBase64(
                                MenuItems.GREEN_PLUS, "§a§lCreate a new Warp Group",
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
            .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short) 15, null))
            .pattern("000000000")
            .pattern("000000000")
            .pattern("000000000")
            .pattern("111111110")
            .build();
    }

    @Override
    protected List<?> getSource() {
        // Get the warp groups in the build team sorted by name
        List<WarpGroup> warpGroups = buildTeam.getWarpGroups().stream().sorted((warpGroup1, warpGroup2) -> warpGroup1.getName().compareToIgnoreCase(warpGroup2.getName())).collect(Collectors.toList());

        // If the warp group "Other" has no warps, remove it from the list
        WarpGroup otherWarpGroup = warpGroups.stream().filter(warpGroup -> warpGroup.getName().equalsIgnoreCase("Other")).findFirst().orElse(null);
        if(otherWarpGroup != null && otherWarpGroup.getWarps().size() == 0)
            warpGroups.remove(otherWarpGroup);

        // Add a create warp group item if the player has permission
        if (getMenuPlayer().hasPermission(Permissions.WARP_GROUP_CREATE))
            warpGroups.add(new WarpGroup(null, "%create-warp-group%", null, -1, null));


        return warpGroups;
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> source) {
        List<WarpGroup> warpGroups = source.stream().map(l -> (WarpGroup) l).collect(Collectors.toList());

        int slot = 0;
        for (WarpGroup warpGroup : warpGroups) {

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
                    new WarpMenu(clickPlayer, warpGroup, true, true);

            });
            slot++;
        }
    }

    protected boolean isPlusItem(WarpGroup warpGroup){
        return warpGroup.getName().equals("%create-warp-group%") && getMenuPlayer().hasPermission(Permissions.WARP_GROUP_CREATE);
    }

    protected int getWarpGroupSlot(WarpGroup warpGroup, int currentIndex){
        int warpGroupSlot = currentIndex;

        if(warpGroup.getSlot() != -1 && warpGroup.getSlot() > 0 && warpGroup.getSlot() < 27)
            warpGroupSlot = warpGroup.getSlot();

        return warpGroupSlot;
    }

    protected int getPlusSlot(int currentIndex){
        int warpGroupSlot = currentIndex;

        for(WarpGroup warpGroup : buildTeam.getWarpGroups())
            if(warpGroup.getSlot() != -1 && warpGroup.getSlot() > 0 && warpGroup.getSlot() < 27)
                warpGroupSlot = ALTERNATE_PLUS_SLOT;

        return warpGroupSlot;
    }
}
