package net.buildtheearth.modules.navigation.menu;

import lombok.NonNull;
import net.buildtheearth.modules.navigation.NavUtils;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.network.model.RegionType;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.ListUtil;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.AbstractPaginatedMenu;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StateSelectorMenu extends AbstractPaginatedMenu {

    private final Region country;
    private final List<Region> states;

    public static final int BACK_ITEM_SLOT = 27;
    public static final int SWITCH_PAGE_ITEM_SLOT = 34;

    // TODO Eloborate if this is still needed anywheere, otherwise remove it - maybe ts needed for new jersey
    public StateSelectorMenu(@NonNull Region country, Player menuPlayer, boolean autoLoad) {
        super(4, 3, country.getName() + " - states", menuPlayer, autoLoad);
        this.country = country;
        this.states = NetworkModule.getRegionsByRegionType(RegionType.STATE).stream()
                .filter(region -> region.getContinent() == country.getContinent()).collect(Collectors.toList());

        // Add the New York City region to the states because thats the only city in the USA
        if(country.getCountryCodeCca3().equalsIgnoreCase("USA"))
            states.add(NetworkModule.getInstance().getRegions().stream().filter(region -> region.getBuildTeam() != null && region.getBuildTeam().getID().equals("Qy2duN4l")).findFirst().orElse(null));

        if (!this.states.isEmpty()) {
            // Remove all regions that don't have a build team
            this.states.removeAll(this.states.stream().filter(region ->
                    region == null
                    || region.getBuildTeam() == null
                    || region.getBuildTeam().getID() == null
                    || region.getBuildTeam().getID().equals(NetworkModule.getInstance().getBuildTeam().getID())
            ).toList());

            // Sort countries by area
            this.states.sort(Comparator.comparing(Region::getName));
        }
    }

    @Override
    protected void setPreviewItems() {
        setBackItem(BACK_ITEM_SLOT, new CountrySelectorMenu(getMenuPlayer(), country.getContinent(), false));

        // If there are more than 27 countries, add the switch page items, otherwise add glass panes
        if(states.size() > 27)
            setSwitchPageItems(SWITCH_PAGE_ITEM_SLOT);
        else
            for(int i = -1; i < 2; i++)
                getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT + i).setItem(MenuItems.ITEM_BACKGROUND);

        super.setPreviewItems();
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        List<Region> collectedStates = source.stream().map(l -> (Region) l).toList();

        // Create the country items
        int slot = 0;

        for (Region region : collectedStates) {
            ArrayList<String> stateLore = ListUtil.createList("", "§eBuild Team:", region.getBuildTeam().getBlankName(), "", "§8Click to join this state's server!");
            getMenu().getSlot(slot).setItem(
                    Item.createCustomHeadBase64(region.getHeadBase64() == null ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19" : region.getHeadBase64(),
              "§6§l" + region.getName(),
                    stateLore)
            );
            slot++;
        }
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        if(states.size() > 27)
            setSwitchPageItemClickEvents(SWITCH_PAGE_ITEM_SLOT);
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(@NotNull List<?> source) {
        List<Region> countries = source.stream().map(l -> (Region) l).toList();

        int slot = 0;
        for (Region clickedRegion : countries) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                ChatHelper.logDebug("Clicked the state: %s", clickedRegion.getName() + " (" + clickedRegion.getCountryCodeCca3() + ")");

                if (clickedRegion.getBuildTeam().isConnected())
                    NavUtils.sendPlayerToConnectedServer(clickPlayer, clickedRegion.getBuildTeam().getServerName());
                else
                    NavUtils.sendNotConnectedMessage(clickPlayer, clickedRegion.getBuildTeam().getIP(), clickedRegion.getBuildTeam().getName());
            });
            slot++;
        }
    }


    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("011111000")
                .build();
    }

    @Override
    protected List<?> getSource() {
        return states;
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {

    }
}
