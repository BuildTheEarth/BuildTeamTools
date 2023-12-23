package net.buildtheearth.modules.navigator.menu;

import com.alpsbte.alpslib.utils.item.ItemBuilder;
import lombok.NonNull;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.MenuItems;
import net.buildtheearth.modules.utils.Utils;
import net.buildtheearth.modules.utils.menus.AbstractPaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Collections;
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
        this.regions = continent.getRegions();

        if(regions.size() > 0)
            regions.sort(Comparator.comparing(Region::isConnected).reversed().thenComparing(Region::getName));

        ChatHelper.logDebug("Continent in constructor: %s", continent);
    }

    @Override
    protected void setPreviewItems() {
        getMenu().getSlot(BACK_ITEM_SLOT).setItem(MenuItems.getBackItem());
        setSwitchPageItems(SWITCH_PAGE_ITEM_SLOT);

        super.setPreviewItems();
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        List<Region> countries = source.stream().map(l -> (Region) l).collect(Collectors.toList());

        // Create the country items
        int slot = 0;

        for (Region region : countries) {
            ArrayList<String> countryLore = new ArrayList<>(Collections.singletonList(ChatHelper.colorize(ChatColor.GRAY, ChatColor.GRAY, "Visit countries in %s", continent.getLabel())));
            getMenu().getSlot(slot).setItem(Item.createCustomHeadBase64(region.getHeadBase64() == null ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19" : region.getHeadBase64(), region.getName(), countryLore));
            slot++;
        }
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        setSwitchPageItemClickEvents(SWITCH_PAGE_ITEM_SLOT);
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
}
