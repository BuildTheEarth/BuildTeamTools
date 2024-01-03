package net.buildtheearth.modules.navigator.menu;

import com.alpsbte.alpslib.utils.item.ItemBuilder;
import lombok.NonNull;
import net.buildtheearth.modules.network.ProxyManager;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.network.model.RegionType;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.ListUtil;
import net.buildtheearth.modules.utils.Utils;
import net.buildtheearth.modules.utils.menus.AbstractPaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StateSelectorMenu extends AbstractPaginatedMenu {

    private final Region country;
    private final List<Region> states;

    public final int BACK_ITEM_SLOT = 27;
    public static int SWITCH_PAGE_ITEM_SLOT = 34;

    public StateSelectorMenu(@NonNull Region country, Player menuPlayer) {
        super(4, 3, country.getName() + " - states", menuPlayer);
        this.country = country;
        this.states = ProxyManager.getRegionsByRegionType(RegionType.STATE).stream()
                .filter(region -> region.getContinent() == country.getContinent()).collect(Collectors.toList());

        if(this.states.size() > 0) {
            // Sort countries by area
            this.states.sort(Comparator.comparing(Region::getName).reversed());

            // Remove all regions that don't have a build team
            this.states.removeAll(this.states.stream().filter(region -> region.getBuildTeam() == null).collect(Collectors.toList()));
        }
    }

    @Override
    protected void setPreviewItems() {
        setBackItem(BACK_ITEM_SLOT);

        // If there are more than 27 countries, add the switch page items, otherwise add glass panes
        if(states.size() > 27)
            setSwitchPageItems(SWITCH_PAGE_ITEM_SLOT);
        else
            for(int i = -1; i < 2; i++)
                getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT + i).setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build());

        super.setPreviewItems();
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        List<Region> states = source.stream().map(l -> (Region) l).collect(Collectors.toList());

        // Create the country items
        int slot = 0;

        for (Region region : states) {
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

        getMenu().getSlot(BACK_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            new CountrySelectorMenu(country.getContinent(), clickPlayer);
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

                Region clickedRegion = this.states.get(_slot);
                ChatHelper.logDebug("%s", clickedRegion.getName());

                if (clickedRegion.getBuildTeam().isConnected())
                    Utils.sendPlayerToServer(clickPlayer, clickedRegion.getBuildTeam().getServerName());
                else
                    ProxyManager.sendNotConnectedMessage(clickPlayer, clickedRegion.getBuildTeam().getIP());
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
        return states;
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {

    }
}
