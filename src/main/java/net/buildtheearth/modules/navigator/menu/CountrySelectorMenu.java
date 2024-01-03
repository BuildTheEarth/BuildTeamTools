package net.buildtheearth.modules.navigator.menu;

import com.alpsbte.alpslib.utils.item.ItemBuilder;
import lombok.NonNull;
import net.buildtheearth.Main;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.network.model.RegionType;
import net.buildtheearth.modules.utils.*;
import net.buildtheearth.modules.utils.menus.AbstractPaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CountrySelectorMenu extends AbstractPaginatedMenu {

    private final Continent continent;
    private final List<Region> regions;

    public final int BACK_ITEM_SLOT = 27;
    public static int SWITCH_PAGE_ITEM_SLOT = 34;

    public CountrySelectorMenu(@NonNull Continent continent, Player menuPlayer) {
        super(4, 3, continent.getLabel() + " - countries", menuPlayer);
        this.continent = continent;
        this.regions = new ArrayList<>(continent.getCountries());

        // Add USA region to North America because it is being built by multiple teams
        if(continent == Continent.NORTH_AMERICA)
            regions.add(
                    new Region("USA",
                            RegionType.COUNTRY,
                            Continent.NORTH_AMERICA,
                            new BuildTeam(null, null, null, "4 Teams", null, Continent.NORTH_AMERICA, false, false),
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhYzk3NzRkYTEyMTcyNDg1MzJjZTE0N2Y3ODMxZjY3YTEyZmRjY2ExY2YwY2I0YjM4NDhkZTZiYzk0YjQifX19", 9372610)
            );

        if(regions.size() > 0) {
            // Sort countries by area
            regions.sort(Comparator.comparing(Region::getArea).reversed());

            // Remove all regions that don't have a build team
            regions.removeAll(regions.stream().filter(region -> region.getBuildTeam() == null || region.getHeadBase64() == null).collect(Collectors.toList()));
        }

        ChatHelper.logDebug("Continent in constructor: %s", continent);
    }

    @Override
    protected void setPreviewItems() {
        setBackItem(BACK_ITEM_SLOT);

        // If there are more than 27 countries, add the switch page items, otherwise add glass panes
        if(regions.size() > 27)
            setSwitchPageItems(SWITCH_PAGE_ITEM_SLOT);
        else
            for(int i = -1; i < 2; i++)
                getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT + i).setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build());

        super.setPreviewItems();
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        List<Region> countries = source.stream().map(l -> (Region) l).collect(Collectors.toList());

        // Create the country items
        int slot = 0;

        for (Region region : countries) {
            ArrayList<String> countryLore = ListUtil.createList("", "§eBuild Team:", region.getBuildTeam().getBlankName(), "", "§eArea:", formatArea(region.getArea()) + " km²", "", "§8Click to join this country's server!");
            getMenu().getSlot(slot).setItem(
                    Item.createCustomHeadBase64(region.getHeadBase64() == null ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19" : region.getHeadBase64(),
              "§6§l" + region.getName(),
                    countryLore)
            );
            slot++;
        }
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        if(regions.size() > 27)
            setSwitchPageItemClickEvents(SWITCH_PAGE_ITEM_SLOT);

        getMenu().getSlot(BACK_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            new ExploreMenu(clickPlayer);
        });
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> source) {
        List<Region> countries = source.stream().map(l -> (Region) l).collect(Collectors.toList());

        int slot = 0;
        for (Region ignored : countries) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                Region clickedRegion = this.regions.get(_slot);
                ChatHelper.logDebug("%s", clickedRegion.getName());

                if (clickedRegion.getBuildTeam().isConnected())
                    Utils.sendPlayerToServer(clickPlayer, clickedRegion.getBuildTeam().getServerName());
                else
                    clickPlayer.sendMessage(ChatHelper.highlight("This country isn't connected to the network! Connect to %s instead.", clickedRegion.getBuildTeam().getIP()));

            });
            slot++;
        }
    }


    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build())
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("011111000")
                .build();
    }

    @Override
    protected List<?> getSource() {
        return regions;
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {

    }

    /** Converts an area in square meters to a string with dot notation starting from the right every 3 digits.
     *
     * @param area
     * @return
     */
    public static String formatArea(double area) {
        String areaStr = String.valueOf((int) area);
        StringBuilder formattedArea = new StringBuilder();

        int length = areaStr.length();
        for (int i = 0; i < length; i++) {
            if (i > 0 && (length - i) % 3 == 0) {
                formattedArea.append(".");
            }
            formattedArea.append(areaStr.charAt(i));
        }

        return formattedArea.toString();
    }
}
