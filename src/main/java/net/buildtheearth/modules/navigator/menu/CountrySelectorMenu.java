package net.buildtheearth.modules.navigator.menu;

import com.alpsbte.alpslib.utils.item.ItemBuilder;
import lombok.NonNull;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.network.model.Country;
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
    private final List<Country> countries;

    public final int BACK_ITEM_SLOT = 27;
    public static int SWITCH_PAGE_ITEM_SLOT = 34;

    public CountrySelectorMenu(@NonNull Continent continent, Player menuPlayer) {
        super(4, 3, continent.getLabel() + " - countries", menuPlayer);
        this.continent = continent;
        this.countries = continent.getCountries();

        if(countries.size() > 0)
            countries.sort(Comparator.comparing(Country::isConnected).reversed().thenComparing(Country::getName));

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
        List<Country> countries = source.stream().map(l -> (Country) l).collect(Collectors.toList());

        // Create the country items
        int slot = 0;

        for (Country country : countries) {
            ArrayList<String> countryLore = new ArrayList<>(Collections.singletonList(ChatHelper.colorize(ChatColor.GRAY, ChatColor.GRAY, "Visit countries in %s", continent.getLabel())));
            getMenu().getSlot(slot).setItem(Item.createCustomHeadBase64(country.getHeadBase64() == null ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19" : country.getHeadBase64(), country.getName(), countryLore));
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
        List<Country> countries = source.stream().map(l -> (Country) l).collect(Collectors.toList());

        int slot = 0;
        for (Country ignored : countries) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                Country clickedCountry = this.countries.get(_slot);
                ChatHelper.logDebug("%s", clickedCountry.getName());

                if (clickedCountry.isConnected())
                    Utils.sendPlayerToServer(clickPlayer, clickedCountry.getServerName());
                else
                    clickPlayer.sendMessage(ChatHelper.highlight("This country isn't connected to the network! Connect to %s instead.", clickedCountry.getIP()));

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
        return countries;
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {

    }
}
