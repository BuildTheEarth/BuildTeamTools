package net.buildtheearth.buildteamtools.modules.navigation.menu;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.item.Item;
import lombok.NonNull;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Continent;
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
import java.util.Objects;

public class StateSelectorMenu extends AbstractPaginatedMenu {

    private final Continent continent;
    private final List<StateEntry> stateEntries;

    public static final int BACK_ITEM_SLOT = 27;
    public static final int SWITCH_PAGE_ITEM_SLOT = 34;

    /**
     * Helper class to represent a state entry which can be either from a build team's regions
     * or a standalone state build team.
     */
    private record StateEntry(Region stateRegion, BuildTeam buildTeam, boolean hasSubCities) {
    }

    /**
     * Creates a menu for selecting states, dynamically detecting states from build teams.
     *
     * @param menuPlayer The player viewing the menu
     * @param continent  The continent to show states for (e.g., North America for USA)
     * @param autoLoad   Whether to auto-load the menu
     */
    public StateSelectorMenu(Player menuPlayer, @NonNull Continent continent, boolean autoLoad) {
        super(4, 3, "USA - States", menuPlayer, autoLoad);
        this.continent = continent;
        this.stateEntries = new ArrayList<>();

        for (BuildTeam team : NetworkModule.getInstance().getBuildTeams()) {
            if (team == null || team.getID() == null) continue;

            if (NetworkModule.getInstance().getBuildTeam() != null
                    && team.getID().equals(NetworkModule.getInstance().getBuildTeam().getID())) {
                continue;
            }

            for (Region region : team.getRegions()) {
                if (region.getType() == RegionType.STATE && region.getContinent() == continent) {
                    boolean hasSubCities = hasCitiesForState(region);
                    stateEntries.add(new StateEntry(region, team, hasSubCities));
                }
            }
        }

        if (!stateEntries.isEmpty()) {
            stateEntries.sort(Comparator.comparing(entry -> entry.stateRegion.getName()));
        }
    }

    /**
     * Checks if there are any city build teams that belong to the given state.
     * Hardcoded - currently only NYC
     */
    private boolean hasCitiesForState(@NonNull Region stateRegion) {
        String stateName = stateRegion.getName();

        return Objects.equals(stateName, "New York");
    }

    /**
     * Gets all city build teams for a given state region.
     * Hardcoded - currently just returns NYC because we don't have a way to connect Cities to States
     */
    private @NonNull List<BuildTeam> getCityTeamsForState(Region stateRegion) {
        List<BuildTeam> cityTeams = new ArrayList<>();

        for (BuildTeam team : NetworkModule.getInstance().getBuildTeams()) {
            if (team == null || team.getID() == null) continue;

            for (Region region : team.getRegions()) {
                if (region.getType() == RegionType.CITY && region.getName().equals("New York City")) {
                    cityTeams.add(team);
                    break; // Only add the team once
                }
            }
        }

        return cityTeams;
    }

    @Override
    protected void setPreviewItems() {
        setBackItem(BACK_ITEM_SLOT, new CountrySelectorMenu(getMenuPlayer(), continent, false));

        if (stateEntries.size() > 27)
            setSwitchPageItems(SWITCH_PAGE_ITEM_SLOT);
        else
            for (int i = -1; i < 2; i++)
                getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT + i).setItem(MenuItems.ITEM_BACKGROUND);

        super.setPreviewItems();
    }

    @Override
    protected void setPaginatedPreviewItems(@NotNull List<?> source) {
        List<StateEntry> entries = source.stream().map(l -> (StateEntry) l).toList();

        int slot = 0;
        for (StateEntry entry : entries) {
            ArrayList<String> stateLore = ListUtil.createList(
                    "",
                    "§eBuild Team:", entry.buildTeam.getBlankName(),
                    ""
            );
            if (entry.hasSubCities) {
                getCityTeamsForState(entry.stateRegion).forEach(cityTeam ->
                        stateLore.add(3, cityTeam.getBlankName()));
            }

            if (entry.hasSubCities) {
                stateLore.add("§8Click to view cities!");
            } else {
                stateLore.add("§8Click to join this state's server!");
            }

            getMenu().getSlot(slot).setItem(
                    Item.createCustomHeadBase64(
                            entry.stateRegion.getHeadBase64(),
                            "§6§l" + entry.stateRegion.getName(),
                            stateLore
                    )
            );
            slot++;
        }
    }

    @Override
    protected void setMenuItemsAsync() {
    }

    @Override
    protected void setItemClickEventsAsync() {
        if (stateEntries.size() > 27)
            setSwitchPageItemClickEvents(SWITCH_PAGE_ITEM_SLOT);
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(@NotNull List<?> source) {
        List<StateEntry> entries = source.stream().map(l -> (StateEntry) l).toList();

        int slot = 0;
        for (StateEntry entry : entries) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                ChatHelper.logDebug("Clicked the state: %s (HasCities: %s)",
                        entry.stateRegion.getName(), entry.hasSubCities);

                if (entry.hasSubCities) { // New York City
                    List<BuildTeam> cityTeams = getCityTeamsForState(entry.stateRegion);
                    new CitySelectorMenu(entry.stateRegion, cityTeams, clickPlayer, true);
                } else {
                    NavUtils.switchToTeam(entry.buildTeam, clickPlayer);
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
        return stateEntries;
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {
    }
}
