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

public class StateSelectorMenu extends AbstractPaginatedMenu {

    private final Continent continent;
    private final List<StateEntry> stateEntries;

    public static final int BACK_ITEM_SLOT = 27;
    public static final int SWITCH_PAGE_ITEM_SLOT = 34;

    private static final String NEW_YORK_STATE_NAME = "New York";
    private static final String NEW_YORK_CITY_NAME = "New York City";

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
        this.stateEntries = loadStateEntries(continent);
    }

    private @NonNull List<StateEntry> loadStateEntries(@NonNull Continent continent) {
        List<StateEntry> entries = new ArrayList<>();

        NetworkModule networkModule = NetworkModule.getInstance();
        BuildTeam currentTeam = networkModule.getBuildTeam();

        for (BuildTeam team : networkModule.getBuildTeams()) {
            if (shouldSkipTeam(team, currentTeam)) {
                continue;
            }

            addStateEntriesForTeam(entries, team, continent);
        }

        entries.sort(Comparator.comparing(entry -> entry.stateRegion.getName()));
        return entries;
    }

    private boolean shouldSkipTeam(BuildTeam team, BuildTeam currentTeam) {
        if (team == null || team.getID() == null) {
            return true;
        }

        if (currentTeam == null) {
            return false;
        }

        return team.getID().equals(currentTeam.getID());
    }

    private void addStateEntriesForTeam(
            @NonNull List<StateEntry> entries,
            @NonNull BuildTeam team,
            @NonNull Continent continent
    ) {
        for (Region region : team.getRegions()) {
            if (!isStateInContinent(region, continent)) {
                continue;
            }

            entries.add(new StateEntry(region, team, hasCitiesForState(region)));
        }
    }

    private boolean isStateInContinent(@NonNull Region region, @NonNull Continent continent) {
        return region.getType() == RegionType.STATE && region.getContinent() == continent;
    }

    /**
     * Checks if there are any city build teams that belong to the given state.
     * <p>
     * Temporary workaround: city-to-state relationships are not available yet,
     * so New York is treated as the only state with city-level build teams.
     */
    private boolean hasCitiesForState(@NonNull Region stateRegion) {
        return NEW_YORK_STATE_NAME.equals(stateRegion.getName());
    }

    private @NonNull List<BuildTeam> getCityTeamsForState(@NonNull Region stateRegion) {
        if (!hasCitiesForState(stateRegion)) {
            return List.of();
        }

        List<BuildTeam> cityTeams = new ArrayList<>();

        for (BuildTeam team : NetworkModule.getInstance().getBuildTeams()) {
            if (team == null || team.getID() == null) {
                continue;
            }

            if (hasNewYorkCityRegion(team)) {
                cityTeams.add(team);
            }
        }

        return cityTeams;
    }

    private boolean hasNewYorkCityRegion(@NonNull BuildTeam team) {
        for (Region region : team.getRegions()) {
            if (region.getType() != RegionType.CITY) {
                continue;
            }

            if (NEW_YORK_CITY_NAME.equals(region.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void setPreviewItems() {
        setBackItem(BACK_ITEM_SLOT, new CountrySelectorMenu(getMenuPlayer(), continent, false));
        setPaginationControls();

        super.setPreviewItems();
    }

    private void setPaginationControls() {
        if (stateEntries.size() > 27) {
            setSwitchPageItems(SWITCH_PAGE_ITEM_SLOT);
            return;
        }

        for (int i = -1; i < 2; i++) {
            getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT + i).setItem(MenuItems.ITEM_BACKGROUND);
        }
    }

    @Override
    protected void setPaginatedPreviewItems(@NotNull List<?> source) {
        List<StateEntry> entries = source.stream()
                .map(StateEntry.class::cast)
                .toList();

        int slot = 0;
        for (StateEntry entry : entries) {
            getMenu().getSlot(slot).setItem(
                    Item.createCustomHeadBase64(
                            entry.stateRegion.getHeadBase64(),
                            "§6§l" + entry.stateRegion.getName(),
                            createStateLore(entry)
                    )
            );

            slot++;
        }
    }

    private @NonNull ArrayList<String> createStateLore(@NonNull StateEntry entry) {
        ArrayList<String> stateLore = ListUtil.createList(
                "",
                "§eBuild Team:",
                entry.buildTeam.getBlankName(),
                ""
        );

        if (!entry.hasSubCities) {
            stateLore.add("§8Click to join this state's server!");
            return stateLore;
        }

        getCityTeamsForState(entry.stateRegion).forEach(cityTeam ->
                stateLore.add(3, cityTeam.getBlankName()));

        stateLore.add("§8Click to view cities!");
        return stateLore;
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
        List<StateEntry> entries = source.stream()
                .map(StateEntry.class::cast)
                .toList();

        int slot = 0;
        for (StateEntry entry : entries) {
            int currentSlot = slot;

            getMenu().getSlot(currentSlot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                ChatHelper.logDebug(
                        "Clicked the state: %s (HasCities: %s)",
                        entry.stateRegion.getName(),
                        entry.hasSubCities
                );

                if (!entry.hasSubCities) {
                    NavUtils.switchToTeam(entry.buildTeam, clickPlayer);
                    return;
                }

                List<BuildTeam> cityTeams = getCityTeamsForState(entry.stateRegion);
                new CitySelectorMenu(entry.stateRegion, cityTeams, clickPlayer, true);
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
