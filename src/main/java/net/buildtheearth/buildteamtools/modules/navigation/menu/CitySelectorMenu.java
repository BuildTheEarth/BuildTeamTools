package net.buildtheearth.buildteamtools.modules.navigation.menu;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.item.Item;
import lombok.NonNull;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.modules.network.model.Region;
import net.buildtheearth.buildteamtools.modules.network.model.RegionType;
import net.buildtheearth.buildteamtools.utils.ListUtil;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractPaginatedMenu;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CitySelectorMenu extends AbstractPaginatedMenu {

    private final Region parentState;
    private final List<BuildTeam> cityBuildTeams;

    public static final int BACK_ITEM_SLOT = 27;
    public static final int SWITCH_PAGE_ITEM_SLOT = 34;

    /**
     * Creates a menu for selecting cities within a state.
     * This is currently hardcoded for the USA - New York
     *
     * @param parentState    The state region that contains these cities
     * @param cityBuildTeams List of build teams that represent cities in this state
     * @param menuPlayer     The player viewing the menu
     * @param autoLoad       Whether to auto-load the menu
     */
    public CitySelectorMenu(@NonNull Region parentState, @NonNull List<BuildTeam> cityBuildTeams, Player menuPlayer,
                            boolean autoLoad) {
        super(4, 3, parentState.getName() + " - Cities", menuPlayer, autoLoad);
        this.parentState = parentState;
        this.cityBuildTeams = new ArrayList<>(cityBuildTeams);

        if (!this.cityBuildTeams.isEmpty()) {
            this.cityBuildTeams.sort(Comparator.comparing(BuildTeam::getBlankName));
        }
    }

    @Override
    protected void setPreviewItems() {
        setBackItem(BACK_ITEM_SLOT, new StateSelectorMenu(getMenuPlayer(), parentState.getContinent(), false));

        if (cityBuildTeams.size() > 27)
            setSwitchPageItems(SWITCH_PAGE_ITEM_SLOT);
        else
            for (int i = -1; i < 2; i++)
                getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT + i).setItem(MenuItems.ITEM_BACKGROUND);

        super.setPreviewItems();
    }

    @Override
    protected void setPaginatedPreviewItems(@NotNull List<?> source) {
        List<BuildTeam> cities = source.stream().map(l -> (BuildTeam) l).toList();

        int slot = 0;
        for (BuildTeam cityTeam : cities) {
            Region cityRegion = cityTeam.getRegions().stream()
                    .filter(r -> r.getType() == RegionType.CITY)
                    .findFirst()
                    .orElse(null);

            if (cityRegion == null) continue;

            ArrayList<String> cityLore = ListUtil.createList(
                    "",
                    "§eBuild Team:", cityTeam.getBlankName(),
                    "",
                    "§8Click to join this city's server!"
            );

            setRegionItem(cityTeam, cityLore, slot, cityRegion.getHeadBase64(), cityRegion.getName());
            slot++;
        }

        ArrayList<String> lore = ListUtil.createList(
                "",
                "§eBuild Team:", parentState.getBuildTeam().getBlankName(),
                "",
                "§8Click to join this state's server!"
        );

        setRegionItem(parentState.getBuildTeam(), lore, slot, parentState.getHeadBase64(),
                "Rest of State " + parentState.getName());
    }

    private void setRegionItem(@NotNull BuildTeam cityTeam, ArrayList<String> cityLore, int slot, String headBase64,
                               String name) {
        if (!cityTeam.getWarpGroups().isEmpty()) {
            cityLore.add("Right click to open the warp group menu!");
        }

        getMenu().getSlot(slot).setItem(
                Item.createCustomHeadBase64(
                        headBase64,
                        "§6§l" + name,
                        cityLore
                )
        );
    }

    @Override
    protected void setMenuItemsAsync() {
    }

    @Override
    protected void setItemClickEventsAsync() {
        if (cityBuildTeams.size() > 27)
            setSwitchPageItemClickEvents(SWITCH_PAGE_ITEM_SLOT);
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(@NotNull List<?> source) {
        List<BuildTeam> cities = source.stream().map(l -> (BuildTeam) l).toList();

        int slot = 0;
        for (BuildTeam clickedCityTeam : cities) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                ChatHelper.logDebug("Clicked City Team: %s", clickedCityTeam.getBlankName());

                if (clickInformation.getClickType().isRightClick() &&
                        clickPlayer.hasPermission(Permissions.WARP_USE) &&
                        !clickedCityTeam.getWarpGroups().isEmpty()) {
                    WarpsComponent.openWarpMenu(clickPlayer, clickedCityTeam, this);
                } else {
                    NavUtils.switchToTeam(clickedCityTeam, clickPlayer);
                }
            });
            slot++;
        }
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern("011111000")
                .build();
    }

    @Override
    protected List<?> getSource() {
        return cityBuildTeams;
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {
    }
}
